package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AliPayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;
import com.stylefeng.guns.api.order.OrderAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    @Reference(
            interfaceClass = OrderAPI.class,
            check = false,
            group = "order2018"
    )
    private OrderAPI orderAPI;

    @Reference(
            interfaceClass = OrderAPI.class,
            check = false,
            group = "order2017"
    )
    private OrderAPI order2017API;

    @Reference(interfaceClass = AliPayServiceAPI.class,check = false)
    private AliPayServiceAPI aliPayServiceAPI;

    private static TokenBucket tokenBucket = new TokenBucket();
    private static final String IMG_PRE="http://img.meetingshop.cn/";

    public ResponseVO error(Integer fieldId,String soldSeats,String seatName){
        return ResponseVO.serviceFail("抱歉，下单的人太多了，请稍后重试");
    }

//    @HystrixCommand(fallbackMethod = "error", commandProperties = {
//    @HystrixProperty(name="execution.isolation.strategy", value = "THREAD"),
//    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value
//            = "4000"),
//    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
//    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
//            }, threadPoolProperties = {
//            @HystrixProperty(name = "coreSize", value = "1"),
//            @HystrixProperty(name = "maxQueueSize", value = "10"),
//            @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
//            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
//            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
//            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
//    })
    @RequestMapping(value="buyTickets",method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId,String soldSeats,String seatsName){
        log.info("订单信息查询开始！");
        try{
            if(tokenBucket.getToken()){
                //选择的座位是否真实存在
                boolean isTrue = orderAPI.isTrueSeats(fieldId+"",soldSeats);
                //已经销售的座位里，有没有这些座位
                boolean isNotSold = orderAPI.isNotSoldSeats(fieldId+"",soldSeats);
                //验证，上述两个内容有一个不为真，则不创建订单信息
                if(isTrue && isNotSold){
                    //创建订单信息,获取登录人
                    String userId = CurrentUser.getCurrentUser();
                    if(userId == null || userId.trim().length() == 0){
                        return ResponseVO.serviceFail("用户未登陆");
                    }
                    OrderInfoVO orderInfoVO = orderAPI.saveOrderInfo(fieldId,soldSeats,seatsName,Integer.parseInt(userId));
                    if(orderInfoVO == null){
                        log.error("购票未成功!");
                        return ResponseVO.serviceFail("购票业务异常");
                    }else{
                        return ResponseVO.success(orderInfoVO);
                    }
                }else{
                    return ResponseVO.serviceFail("订单中的座位编号错误");
                }
            }else{
                return ResponseVO.serviceFail("购票人数过多,稍后再试");
            }
        }catch(Exception e){
            log.error("购票业务异常",e);
            return ResponseVO.serviceFail("购票业务异常");
        }
    }

    @RequestMapping(value="getOrderInfo",method = RequestMethod.POST)
    public ResponseVO getOrderInfo(@RequestParam(name = "nowPage",required = false,defaultValue = "1") int nowPage,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "5") int pageSize){
        String userId = CurrentUser.getCurrentUser();
        //使用当前登录人获取已经购买的订单
        Page<OrderInfoVO> page = new Page<>(nowPage,pageSize);
        if(userId != null && userId.trim().length()>0){
            Page<OrderInfoVO> result = orderAPI.getOrderByUserId(Integer.parseInt(userId), page);
            Page<OrderInfoVO> result2017 = order2017API.getOrderByUserId(Integer.parseInt(userId), page);
            //合并结果
            int totalPages = (int)(result.getPages()+result2017.getPages());
            //2017和2018的订单总数合并
            List<OrderInfoVO> orderInfoVOList = new ArrayList<>();
            orderInfoVOList.addAll(result.getRecords());
            orderInfoVOList.addAll(result2017.getRecords());
            return ResponseVO.success(nowPage,totalPages,"",orderInfoVOList);
        }else{
            return ResponseVO.serviceFail("用户未登陆");
        }

        //根据FieldId 获取所有已经销售的座位编号
    }

    @RequestMapping(value="getPayInfo",method=RequestMethod.POST)
    public ResponseVO getPayInfo(@RequestParam("orderId")String orderId){
        //获取当前登录人信息
        String userId = CurrentUser.getCurrentUser();
        if(userId == null||userId.trim().length()==0){
            return ResponseVO.serviceFail("抱歉，用户未登陆");
        }
        //订单——生成二维码
        AliPayInfoVO aliPayInfoVO = aliPayServiceAPI.getQRCode(orderId);
        return ResponseVO.success(IMG_PRE,aliPayInfoVO);
    }

    @RequestMapping(value="getPayResult",method=RequestMethod.POST)
    public ResponseVO getPayResult(@RequestParam("orderId")String orderId,
                                   @RequestParam(name="tryNums",required = false,defaultValue = "1")Integer tryNums){
        //获取当前登录人信息
        String userId = CurrentUser.getCurrentUser();
        if(userId == null||userId.trim().length()==0){
            return ResponseVO.serviceFail("抱歉，用户未登陆");
        }
        //判断是否支付超时
        if(tryNums>=4){
            return ResponseVO.serviceFail("订单支付失败，请稍微重试");
        }else{
            AliPayResultVO aliPayResultVO = aliPayServiceAPI.getOrderStatus(orderId);
            if(aliPayResultVO == null || ToolUtil.isEmpty(aliPayResultVO.getOrderId())){
                AliPayResultVO serviceFailVO = new AliPayResultVO();
                serviceFailVO.setOrderId(orderId);
                serviceFailVO.setOrderStatus(0);
                serviceFailVO.setOrderMsg("支付不成功");
                return ResponseVO.success(serviceFailVO);
            }
            return ResponseVO.success(aliPayResultVO);
        }
    }
}

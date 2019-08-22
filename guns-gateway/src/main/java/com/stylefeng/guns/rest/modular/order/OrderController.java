package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.OrderAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    @Reference(interfaceClass = OrderAPI.class,check = false)
    private OrderAPI orderAPI;

    @RequestMapping(value="buyTickets",method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId,String soldSeats,String seatsName){
        log.info("订单信息查询开始！");
        try{
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
            return ResponseVO.success(nowPage,(int)result.getPages(),"",result.getRecords());
        }else{
            return ResponseVO.serviceFail("用户未登陆");
        }

        //根据FieldId 获取所有已经销售的座位编号
    }
}

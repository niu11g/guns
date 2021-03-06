package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.core.util.UuidUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2017TMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderAPI.class,group = "order2017")
public class OrderAPIImpl2017 implements OrderAPI {

    @Autowired
    private MoocOrder2017TMapper moocOrder2017TMapper;

    @Reference(interfaceClass = CinemaAPI.class,check = false)
    private CinemaAPI cinemaAPI;

    @Autowired
    private FTPUtil ftpUtil;

    //验证售出的票是否为真
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        //根据FieldId找到对应的座位位置图
        String seatPath = moocOrder2017TMapper.getSeatsByFieldId(fieldId);
        //读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);
        //将fileStrByAddress转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        //seats=1,2,3  ids="1,3,4,5,6,7,88"
        String ids = jsonObject.get("ids").toString();
        //每一次匹配上的，都给isTrue+1
        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue = 0;
        for(String id : idArrs){
            for(String seat : seatArrs){
                if(seat.equalsIgnoreCase(id)){
                    isTrue++;
                }
            }
        }
        //如果匹配上的数量与已售座位数一致，则表示全部匹配上了
        if(seatArrs.length == isTrue){
            return true;
        }else{
            return false;
        }
    }
    //已经销售的座位里，有没有这些座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id",fieldId);

        List<MoocOrder2017T> list = moocOrder2017TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");

        for(MoocOrder2017T moocOrderT : list){
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for(String id : ids){
                for(String seat : seatArrs){
                    if(id.equalsIgnoreCase(seat)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    //创建订单信息
    @Override
    public OrderInfoVO saveOrderInfo(
            Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        //编号--自动生成
        String uuid = UuidUtil.genUuid();
        //影片信息
        FilmInfoVO filmInfoVO = cinemaAPI.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());

        //获取影院信息
        OrderQueryVO orderQueryVO = cinemaAPI.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());

        //求订单总金额  //1,2,3,4,5
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds,filmPrice);

        MoocOrder2017T moocOrder2017T = new MoocOrder2017T();
        moocOrder2017T.setUuid(uuid);
        moocOrder2017T.setSeatsName(seatsName);
        moocOrder2017T.setSeatsIds(soldSeats);
        moocOrder2017T.setOrderUser(userId);
        moocOrder2017T.setOrderPrice(totalPrice);
        moocOrder2017T.setFilmPrice(filmPrice);
        moocOrder2017T.setFilmId(filmId);
        moocOrder2017T.setFieldId(fieldId);
        moocOrder2017T.setCinemaId(cinemaId);

        Integer insert = moocOrder2017TMapper.insert(moocOrder2017T);
        if(insert>0){
            //返回查询结果
            OrderInfoVO orderInfoVO = moocOrder2017TMapper.getOrderInfoById(uuid);
            if(orderInfoVO == null || orderInfoVO.getOrderId() == null){
                log.error("订单信息查询失败，订单编号为{}",uuid);
                return null;
            }else{
                return orderInfoVO;
            }
        }else{
            //插入出错
            log.error("订单插入失败");
            return null;
        }
    }
    private static double getTotalPrice(int solds,double filmPrice){
        BigDecimal soldsDeci = new BigDecimal(solds);
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);

        BigDecimal result = soldsDeci.multiply(filmPriceDeci);

        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }

    public static void main(String[] args){
        double totalPrice = getTotalPrice(2,13.223423);
    }
    //使用当前登录人获取已经购买的订单
    @Override
    public Page<OrderInfoVO> getOrderByUserId(Integer userId, Page<OrderInfoVO> orderInfo) {
        Page<OrderInfoVO> result = new Page<>();
        if(userId == null){
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        }else{
            List<OrderInfoVO> orderInfoByUserId = moocOrder2017TMapper.getOrderInfoByUserId(userId,orderInfo);
            if(orderInfoByUserId==null && orderInfoByUserId.size()==0){
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            }else{
                EntityWrapper<MoocOrder2017T> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user",userId);
                Integer counts = moocOrder2017TMapper.selectCount(entityWrapper);
                //将结果放入Page
                result.setTotal(counts);
                result.setRecords(orderInfoByUserId);
                return result;
            }
        }
    }
    //根据FieldId 获取所有已经销售的座位编号
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if(fieldId == null){
            log.error("查询已售座位错误，未传入任何场次编号");
            return "";
        }else{
            String soldSeatsByFieldId = moocOrder2017TMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }

    @Override
    public OrderInfoVO getOrderInfoById(String orderId) {


        OrderInfoVO orderInfoById = moocOrder2017TMapper.getOrderInfoById(orderId);
        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {
        MoocOrder2017T moocOrderT = new MoocOrder2017T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);
        Integer integer = moocOrder2017TMapper.updateById(moocOrderT);
        if(integer>=1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrder2017T moocOrderT = new MoocOrder2017T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);
        Integer integer = moocOrder2017TMapper.updateById(moocOrderT);
        if(integer>=1){
            return true;
        }else{
            return false;
        }
    }
}

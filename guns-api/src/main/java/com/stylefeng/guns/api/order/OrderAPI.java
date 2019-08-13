package com.stylefeng.guns.api.order;

import com.stylefeng.guns.api.order.vo.OrderInfoVO;

import java.util.List;

public interface OrderAPI {
    //验证售出的票是否为真
    boolean isTrueSeats(String fieldId,String seats);
    //已经销售的座位里，有没有这些座位
    boolean isNotSoldSeats(String fieldId,String seats);
    //创建订单信息
    OrderInfoVO saveOrderInfo(Integer fieldId,String soldSeats,String seatsName,Integer userId);
    //使用当前登录人获取已经购买的订单
    List<OrderInfoVO> getOrderByUserId(Integer userId);
    //根据FieldId 获取所有已经销售的座位编号
    String getSoldSeatsByFieldId(Integer fieldId);
}

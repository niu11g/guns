package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;


public interface OrderAPI {
    //验证售出的票是否为真
    boolean isTrueSeats(String fieldId,String seats);
    //已经销售的座位里，有没有这些座位
    boolean isNotSoldSeats(String fieldId,String seats);
    //创建订单信息
    OrderInfoVO saveOrderInfo(Integer fieldId,String soldSeats,String seatsName,Integer userId);
    //使用当前登录人获取已经购买的订单
    Page<OrderInfoVO> getOrderByUserId(Integer userId, Page<OrderInfoVO> page);
    //根据FieldId 获取所有已经销售的座位编号
    String getSoldSeatsByFieldId(Integer fieldId);
    //根据订单编号获取订单信息
    OrderInfoVO getOrderInfoById(String orderId);

    boolean paySuccess(String orderId);

    boolean payFail(String orderId);

}

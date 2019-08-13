package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.order.OrderAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Service(interfaceClass = OrderAPI.class)
public class OrderAPIImpl implements OrderAPI {

    @Autowired
    private MoocOrderTMapper moocOrderTMapper;

    //验证售出的票是否为真
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        //根据FieldId找到对应的座位位置图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);
        //读取位置图，判断seats是否为真

        return false;
    }
    //已经销售的座位里，有没有这些座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        return false;
    }
    //创建订单信息
    @Override
    public OrderInfoVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        return null;
    }
    //使用当前登录人获取已经购买的订单
    @Override
    public List<OrderInfoVO> getOrderByUserId(Integer userId) {
        return null;
    }
    //根据FieldId 获取所有已经销售的座位编号
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        return null;
    }
}

package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.cinema.CinemaAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.core.util.UuidUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
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
@Service(interfaceClass = OrderAPI.class)
public class OrderAPIImpl implements OrderAPI {

    @Autowired
    private MoocOrderTMapper moocOrderTMapper;

    @Autowired
    private CinemaAPI cinemaAPI;

    @Autowired
    private FTPUtil ftpUtil;

    //验证售出的票是否为真
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        //根据FieldId找到对应的座位位置图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);
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

        List<MoocOrderT> list = moocOrderTMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");

        for(MoocOrderT moocOrderT : list){
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

        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setFilmId(filmId);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setCinemaId(cinemaId);

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if(insert>0){
            //返回查询结果
            OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
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
    public List<OrderInfoVO> getOrderByUserId(Integer userId) {

        if(userId == null){
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        }else{
            List<OrderInfoVO> orderInfoByUserId = moocOrderTMapper.getOrderInfoByUserId(userId);
            if(orderInfoByUserId==null && orderInfoByUserId.size()==0){
                return new ArrayList<>();
            }else{
                return orderInfoByUserId;
            }
        }
    }
    //根据FieldId 获取所有已经销售的座位编号
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        return null;
    }
}

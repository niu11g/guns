package com.stylefeng.guns.rest.modular.order;

import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order/")
public class OrderController {

    @RequestMapping(value="buyTickets",method = RequestMethod.POST)
    public ResponseVO buyTickets(int fieldId,int soldSeats,String seatsName){

        //验证售出的票是否为真

        //已经销售的座位里，有没有这些座位

        //创建订单信息,获取登录人

        return null;

    }

    @RequestMapping(value="getOrderInfo",method = RequestMethod.POST)
    public ResponseVO getOrderInfo(@RequestParam(name = "nowPage",required = false,defaultValue = "1") int nowPage,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "5") int pageSize){
        //使用当前登录人获取已经购买的订单

        //根据FieldId 获取所有已经销售的座位编号

        return null;
    }
}

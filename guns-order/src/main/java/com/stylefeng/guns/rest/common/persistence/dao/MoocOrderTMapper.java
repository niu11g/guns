package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author ly
 * @since 2019-08-11
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    String getSeatsByFieldId(@Param("fieldId") String fieldId);

    OrderInfoVO getOrderInfoById(@Param("orderId") String orderId);

    List<OrderInfoVO> getOrderInfoByUserId(@Param("userId") Integer userId);

}

package com.cloud.mail.service.order.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.api.order.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Update("update orders set status=2 where id=#{orderId}")
    int updatePayStatus(String orderId, int status);
}

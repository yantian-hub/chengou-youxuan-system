package com.cloud.mail.service.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.api.seckill.model.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    @Update("update seckill_order set status = #{status} where order_id = #{orderId}")
    void updateStatus(String orderId, int i);
}

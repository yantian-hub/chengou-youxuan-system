package com.cloud.mail.service.order.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.api.order.model.Ordersku;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderSkuMapper extends BaseMapper<Ordersku> {
}

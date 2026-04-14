package com.cloud.mail.service.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.api.pay.model.PayLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayLogMapper extends BaseMapper<PayLog> {
}

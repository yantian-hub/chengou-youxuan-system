package com.cloud.mail.service.pay.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.api.pay.model.PayLog;
import com.cloud.mail.service.pay.mapper.PayLogMapper;
import com.cloud.mail.service.pay.service.PayLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {
    @Resource
    private PayLogMapper payLogMapper;

    @Override
    public void add(PayLog payLog) {
        payLogMapper.insert(payLog);
    }
}

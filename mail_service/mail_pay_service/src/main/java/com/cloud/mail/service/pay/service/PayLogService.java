package com.cloud.mail.service.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.api.pay.model.PayLog;

public interface PayLogService extends IService<PayLog> {
    void add(PayLog payLog);
}

package com.cloud.mail.service.pay;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cloud.mail.service.pay.mapper")
@OpenAPIDefinition(info = @Info(
        title = "支付服务",
        description = "微信支付集成、支付回调处理、支付日志",
        version = "1.0.0"
))
public class MailPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailPayApplication.class, args);
    }
}

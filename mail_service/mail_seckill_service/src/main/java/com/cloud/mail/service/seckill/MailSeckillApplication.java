package com.cloud.mail.service.seckill;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.cloud.mail.service.seckill.mapper")
@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
        title = "秒杀服务",
        description = "秒杀下单、订单查询、高并发秒杀处理（Redis+Lua+MQ）",
        version = "1.0.0"
))
public class MailSeckillApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(MailSeckillApplication.class, args);
    }
}

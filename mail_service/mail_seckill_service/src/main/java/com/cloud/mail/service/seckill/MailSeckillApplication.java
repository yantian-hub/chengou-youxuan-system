package com.cloud.mail.service.seckill;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cloud.mail.service.seckill.mapper")
@SpringBootApplication
public class MailSeckillApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(MailSeckillApplication.class, args);
    }
}

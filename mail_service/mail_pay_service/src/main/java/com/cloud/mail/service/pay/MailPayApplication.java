package com.cloud.mail.service.pay;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cloud.mail.service.pay.mapper")
public class MailPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailPayApplication.class, args);
    }
}

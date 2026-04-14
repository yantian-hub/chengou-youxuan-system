package com.cloud.mail.service.usr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cloud.mail.service.usr.mapper")
@SpringBootApplication
public class UsrApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(UsrApplication.class, args);
    }
}

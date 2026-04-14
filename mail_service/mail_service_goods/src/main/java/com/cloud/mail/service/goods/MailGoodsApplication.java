package com.cloud.mail.service.goods;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan(basePackages = {"com.cloud.mail.service.goods.mapper"})
@EnableCaching
@EnableFeignClients
public class MailGoodsApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(MailGoodsApplication.class, args);
    }


}

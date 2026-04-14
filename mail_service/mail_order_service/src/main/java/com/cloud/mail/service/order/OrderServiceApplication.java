package com.cloud.mail.service.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.cloud.mail.service.order.Mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.cloud.mail.service.goods.feign", "com.cloud.mail.api.cart.feign"})
public class OrderServiceApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(OrderServiceApplication.class, args);
    }
}

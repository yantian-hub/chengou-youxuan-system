package com.cloud.mail.service.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.cloud.mail.service.cart.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = {
    "com.cloud.mail.api",
    "com.cloud.mail.service.goods.feign"
})
public class CartApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(CartApplication.class, args);
    }
}

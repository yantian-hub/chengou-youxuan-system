package com.cloud.mail.service.cart;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.cloud.mail.service.goods.feign"})
@OpenAPIDefinition(info = @Info(
        title = "购物车服务",
        description = "购物车增删改查、商品数量管理",
        version = "1.0.0"
))
public class CartApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(CartApplication.class, args);
    }
}

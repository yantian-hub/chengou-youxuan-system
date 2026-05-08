package com.cloud.mail.service.order;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.cloud.mail.service.goods.feign", "com.cloud.mail.api.cart.feign"})
@OpenAPIDefinition(info = @Info(
        title = "订单服务",
        description = "订单创建、订单查询、订单状态管理",
        version = "1.0.0"
))
public class OrderServiceApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(OrderServiceApplication.class, args);
    }
}

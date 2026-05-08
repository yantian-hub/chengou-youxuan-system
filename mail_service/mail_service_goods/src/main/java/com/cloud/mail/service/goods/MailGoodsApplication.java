package com.cloud.mail.service.goods;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan(basePackages = {"com.cloud.mail.service.goods.mapper"})
@EnableCaching
@EnableFeignClients
@OpenAPIDefinition(info = @Info(
        title = "商品服务",
        description = "商品分类、品牌管理、SKU/SPU管理、商品搜索",
        version = "1.0.0"
))
public class MailGoodsApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(MailGoodsApplication.class, args);
    }


}

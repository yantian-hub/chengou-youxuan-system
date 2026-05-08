package com.cloud.mail.service.usr;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cloud.mail.service.usr.mapper")
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "用户服务",
        description = "用户登录、地址管理",
        version = "1.0.0"
))
public class UsrApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(UsrApplication.class, args);
    }
}

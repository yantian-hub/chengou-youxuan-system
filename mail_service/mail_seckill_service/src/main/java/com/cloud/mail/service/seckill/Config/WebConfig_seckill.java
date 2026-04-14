package com.cloud.mail.service.seckill.Config;

import com.cloud.mail.service.seckill.Interceptor.UsrIdInterceptor_seckill;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig_seckill implements WebMvcConfigurer {

    @Resource
    private UsrIdInterceptor_seckill userIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userIdInterceptor)
                .addPathPatterns("/seckill/**");
    }
}

package com.cloud.mail.service.usr.Interceptor;

import com.cloud.mail.service.usr.Interceptor.UserIdInterceptor_Usr;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig_Usr implements WebMvcConfigurer {

    @Resource
    private UserIdInterceptor_Usr userIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userIdInterceptor)
                .addPathPatterns("/usr/address/**");
    }
}

package com.cloud.mail.service.usr.controller;

import com.cloud.mail.api.usr.model.LoginDto;
import com.cloud.mail.api.usr.model.Usr;
import com.cloud.mail.service.usr.service.UsrLoginService;
import com.cloud.util.RespResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户登录", description = "用户注册、登录、短信验证码")
@RestController
@RequestMapping("/usrLogin")
public class UsrLoginController {
    @Resource
    UsrLoginService usrLoginService;

    @Operation(summary = "发送短信验证码")
    @RequestMapping("/sendCode/{phone}")
    public RespResult<String> sendCode(@Parameter(description = "手机号") @PathVariable("phone") String phone) {
        String message = usrLoginService.sendCode(phone);
        return RespResult.ok(message);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public RespResult<String> register(@RequestBody Usr usr) {
        return usrLoginService.register(usr);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public RespResult<String> login(@RequestBody LoginDto usr) {
        return usrLoginService.login(usr);
    }
}

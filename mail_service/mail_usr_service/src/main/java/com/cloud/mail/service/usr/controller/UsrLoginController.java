package com.cloud.mail.service.usr.controller;

import com.cloud.mail.api.usr.model.LoginDto;
import com.cloud.mail.api.usr.model.Usr;
import com.cloud.mail.service.usr.service.UsrLoginService;
import com.cloud.util.RespResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usrLogin")
public class UsrLoginController {
    @Resource
    UsrLoginService usrLoginService;

    @RequestMapping("/sendCode/{phone}")
    public RespResult<String> sendCode(@PathVariable("phone") String phone) {
        String message = usrLoginService.sendCode(phone);
        return RespResult.ok(message);
    }

    @PostMapping("/register")
    public RespResult<String> register(@RequestBody Usr usr) {
        return usrLoginService.register(usr);
    }

    @PostMapping("/login")
    public RespResult<String> login(@RequestBody LoginDto usr) {
        return usrLoginService.login(usr);
    }
}

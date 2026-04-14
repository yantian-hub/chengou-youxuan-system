package com.cloud.mail.service.usr.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.mail.api.usr.model.LoginDto;
import com.cloud.mail.api.usr.model.Usr;
import com.cloud.mail.service.usr.mapper.UsrLoginMapper;
import com.cloud.mail.service.usr.service.UsrLoginService;
import com.cloud.util.JWTUtil;
import com.cloud.util.MD5;
import com.cloud.util.RespResult;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UsrLoginServiceImpl implements UsrLoginService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    private UsrLoginMapper usrMApper;


    @Override
    public String sendCode(String phone) {
        String code = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set("usr:login:code:" + phone, code, 5, TimeUnit.MINUTES);
        return "验证码已发送";
    }

    @Override
    public RespResult<String> register(Usr usr) {
        String redisCode = stringRedisTemplate.opsForValue().get("usr:login:code:" + usr.getPhone());
        if (redisCode == null || !redisCode.equals(usr.getCode())) {
            return RespResult.error("验证码错误或已过期");
        }

        stringRedisTemplate.delete("usr:login:code:" + usr.getPhone());

        int count = usrMApper.checkPhone(usr.getPhone());
        if (count > 0) {
            return RespResult.error("手机号已注册");
        }

        String salt = RandomUtil.randomString(8);
        String encryptedPassword = MD5.encryptWithSalt(usr.getPassword(), salt);
        usr.setSalt(salt);
        usr.setPassword(encryptedPassword);

        usrMApper.insert(usr);
        return RespResult.ok("注册成功");
    }


    @Override
    public RespResult<String> login(LoginDto usr) {
        LambdaQueryWrapper<Usr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Usr::getName, usr.getName());
        Usr user = usrMApper.selectOne(wrapper);

        if (user == null) {
            return RespResult.error("用户名或密码错误");
        }

        String inputPassword = MD5.encryptWithSalt(usr.getPassword(), user.getSalt());
        if (!inputPassword.equals(user.getPassword())) {
            return RespResult.error("用户名或密码错误");
        }

        String token = JWTUtil.generateToken(Long.valueOf(user.getId()), user.getName());

        stringRedisTemplate.opsForValue().set("login:token:" + token, String.valueOf(user.getId()), 7, TimeUnit.DAYS);

        return RespResult.ok(token);
    }

}

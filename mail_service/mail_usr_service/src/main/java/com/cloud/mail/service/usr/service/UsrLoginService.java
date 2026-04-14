package com.cloud.mail.service.usr.service;

import com.cloud.mail.api.usr.model.LoginDto;
import com.cloud.mail.api.usr.model.Usr;
import com.cloud.util.RespResult;

public interface UsrLoginService {
    String sendCode(String phone);

    RespResult<String> register(Usr usr);

    RespResult<String> login(LoginDto usr);
}

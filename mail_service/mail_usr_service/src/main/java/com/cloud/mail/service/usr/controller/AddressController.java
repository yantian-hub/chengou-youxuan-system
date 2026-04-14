package com.cloud.mail.service.usr.controller;

import com.cloud.mail.api.usr.model.Address;
import com.cloud.mail.service.usr.service.AddressService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/usr/address")
@RestController
public class AddressController {
    @Resource
    AddressService adressService;

    @GetMapping
    public RespResult<List<Address>> queryAddressList() {
        Long userId = UserIdHolder.get();
        return RespResult.ok(adressService.queryAddressList(userId));
    }
}

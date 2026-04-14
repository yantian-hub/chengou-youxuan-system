package com.cloud.mail.service.usr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.api.usr.model.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    //查询用户地址链表
    List<Address> queryAddressList(Long userId);
}

package com.cloud.mail.service.usr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.api.usr.model.Address;
import com.cloud.mail.service.usr.mapper.AddressMapper;
import com.cloud.mail.service.usr.service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    @Resource
    private AddressMapper adressMapper;

    @Override
    public List<Address> queryAddressList(Long userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return adressMapper.selectList(queryWrapper);
    }
}

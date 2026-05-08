package com.cloud.mail.service.usr.controller;

import com.cloud.mail.api.usr.model.Address;
import com.cloud.mail.service.usr.service.AddressService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "地址管理", description = "用户收货地址增删改查")
@RequestMapping("/usr/address")
@RestController
public class AddressController {
    @Resource
    AddressService addressService;

    @Operation(summary = "新增收货地址")
    @PutMapping
    public RespResult<String> add(@RequestBody Address address) {
        address.setUserId(UserIdHolder.get());
        addressService.save(address);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "更新收货地址")
    @PostMapping
    public RespResult<String> update(@RequestBody Address address) {
        addressService.updateById(address);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{id}")
    public RespResult<String> delete(@Parameter(description = "地址ID") @PathVariable Integer id) {
        addressService.removeById(id);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "根据ID查询收货地址")
    @GetMapping("/{id}")
    public RespResult<Address> getById(@Parameter(description = "地址ID") @PathVariable Integer id) {
        return RespResult.ok(addressService.getById(id));
    }

    @Operation(summary = "查询用户收货地址列表")
    @GetMapping
    public RespResult<List<Address>> queryAddressList() {
        Long userId = UserIdHolder.get();
        return RespResult.ok(addressService.queryAddressList(userId));
    }
}

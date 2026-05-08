package com.cloud.mail.api.cart.feign;


import com.cloud.mail.api.cart.model.Cart;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mail-service-cart")
@RequestMapping("/cart")
public interface Cartfeign {
    @GetMapping("/{id}/{num}")
    RespResult<String> addCart(@PathVariable("id") String id
            , @PathVariable("num") Integer num);
    //获取购物车列表
    @GetMapping("/list")
    RespResult<List<Cart>> list();
    //查询指定购物车id集合的列表
    @PostMapping("/listByIds")
    RespResult<List<Cart>> listByIds(@RequestBody List<String> Ids) ;
}

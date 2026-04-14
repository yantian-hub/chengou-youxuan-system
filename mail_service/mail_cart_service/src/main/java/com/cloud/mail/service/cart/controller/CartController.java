package com.cloud.mail.service.cart.controller;

import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.cart.service.CartService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    CartService cartService;

    @GetMapping("/{id}/{num}")
    public RespResult<String> addCart(@PathVariable("id") String id
            , @PathVariable("num") Integer num) {
        //1.获取userid
        Long userId = UserIdHolder.get();
        //执行方法
        boolean b = cartService.addCart(id, String.valueOf(userId), num);
        return RespResult.ok("添加成功");
    }
    //获取购物车列表
    @GetMapping("/list")
    public RespResult<List<Cart>> list() {
        Long userId = UserIdHolder.get();
        return RespResult.ok(cartService.list(String.valueOf(userId)));
    }

    //查询指定购物车id集合的列表
    @PostMapping("/listByIds")
    public RespResult<List<Cart>> listByIds(@RequestBody List<String> Ids) {
        Long userId = UserIdHolder.get();
        return RespResult.ok(cartService.listByIds(Ids, String.valueOf(userId)));
    }
}

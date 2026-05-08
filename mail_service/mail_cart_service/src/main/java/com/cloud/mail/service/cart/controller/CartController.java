package com.cloud.mail.service.cart.controller;

import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.cart.service.CartService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车服务", description = "购物车增删改查、批量查询")
@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @GetMapping("/{id}/{num}")
    public RespResult<String> addCart(@Parameter(description = "商品SKU ID") @PathVariable("id") String id
            , @Parameter(description = "数量") @PathVariable("num") Integer num) {
        //1.获取userid
        Long userId = UserIdHolder.get();
        //执行方法
        boolean b = cartService.addCart(id, String.valueOf(userId), num);
        return RespResult.ok("添加成功");
    }
    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public RespResult<List<Cart>> list() {
        Long userId = UserIdHolder.get();
        return RespResult.ok(cartService.list(String.valueOf(userId)));
    }

    @Operation(summary = "根据ID集合批量查询购物车")
    @PostMapping("/listByIds")
    public RespResult<List<Cart>> listByIds(@RequestBody List<String> Ids) {
        Long userId = UserIdHolder.get();
        return RespResult.ok(cartService.listByIds(Ids, String.valueOf(userId)));
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{skuId}")
    public RespResult<String> deleteCart(@Parameter(description = "商品SKU ID") @PathVariable String skuId) {
        Long userId = UserIdHolder.get();
        cartService.deleteCart(skuId, String.valueOf(userId));
        return RespResult.ok("删除成功");
    }

    @Operation(summary = "修改购物车商品数量")
    @PutMapping("/{skuId}/{num}")
    public RespResult<String> updateCartNum(@Parameter(description = "商品SKU ID") @PathVariable String skuId,
                                            @Parameter(description = "数量") @PathVariable Integer num) {
        Long userId = UserIdHolder.get();
        cartService.updateCartNum(skuId, String.valueOf(userId), num);
        return RespResult.ok("修改成功");
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public RespResult<String> clearCart() {
        Long userId = UserIdHolder.get();
        cartService.clearCart(String.valueOf(userId));
        return RespResult.ok("清空成功");
    }
}

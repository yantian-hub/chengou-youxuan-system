package com.cloud.mail.service.cart.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.cart.service.CartService;
import com.cloud.mail.service.goods.feign.Skufeign;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.util.RespResult;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    Skufeign skufeign;

    @Override
    public Boolean addCart(String id, String userId, Integer num) {
        // 查询sku详情
        RespResult<Sku> sku = skufeign.one(Integer.valueOf(id));
        Sku data = sku.getData();
        Cart cart = new Cart(userId + id, userId, data.getName(), data.getPrice(), id, num);
        // 以JSON格式存储到Redis
        stringRedisTemplate.opsForHash().put("Cart:" + userId, id, JSON.toJSONString(cart));
        return true;
    }

    @Override
    public List<Cart> list(String s) {
        List<Object> values = stringRedisTemplate.opsForHash().values("Cart:" + s);
        return values.stream()
                .filter(v -> v != null)
                .map(v -> JSON.parseObject((String) v, Cart.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Cart> listByIds(List<String> Ids, String s) {
        List<Cart> carts = new ArrayList<>();
        for (String id : Ids) {
            Object value = stringRedisTemplate.opsForHash().get("Cart:" + s, id);
            if (value != null) {
                carts.add(JSON.parseObject((String) value, Cart.class));
            }
        }
        return carts;
    }

    @Override
    public Boolean deleteCart(String skuId, String userId) {
        return stringRedisTemplate.opsForHash().delete("Cart:" + userId, skuId) > 0;
    }

    @Override
    public Boolean updateCartNum(String skuId, String userId, Integer num) {
        Object value = stringRedisTemplate.opsForHash().get("Cart:" + userId, skuId);
        if (value == null) return false;
        Cart cart = JSON.parseObject((String) value, Cart.class);
        cart.setNum(num);
        stringRedisTemplate.opsForHash().put("Cart:" + userId, skuId, JSON.toJSONString(cart));
        return true;
    }

    @Override
    public void clearCart(String userId) {
        stringRedisTemplate.delete("Cart:" + userId);
    }
}

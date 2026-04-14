package com.cloud.mail.service.cart.service.Impl;

import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.cart.service.CartService;
import com.cloud.mail.service.goods.feign.Skufeign;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.util.RespResult;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    Skufeign skufeign;

    @Override
    public Boolean addCart(String id, String userId, Integer num) {
        //redis key的结构 Cart::usrId field:skuid  value:cart
        //1.删除旧数据
        stringRedisTemplate.opsForHash().delete("Cart:" + userId, id);
        //2，查询sku详情
        RespResult<Sku> sku = skufeign.one(Integer.valueOf(id));
        Sku data = sku.getData();
        Cart cart = new Cart((userId+id), userId, data.getName(), data.getPrice(), id,num);
        //3.保存新数据
        stringRedisTemplate.opsForHash().put("Cart:" + userId, id, JSON.toJSONString(cart));

        return true;
    }

    @Override
    public List<Cart> list(String s) {
        List<Object> values = stringRedisTemplate.opsForHash().values("Cart:" + s);
        List<Cart> carts = new ArrayList<>();
        for (Object value : values) {
            Cart cart = (Cart) value;
            carts.add(cart);
        }
        return carts;
    }

    @Override
    public List<Cart> listByIds(List<String> Ids, String s) {
        //根据用户id和指定ids查询
       // 第40-46行需要改为
        List<Object> values = stringRedisTemplate.opsForHash().values("Cart:" + userId);
        List<Cart> carts = new ArrayList<>();
        for (Object value : values) {
            Cart cart = JSON.parseObject(value.toString(), Cart.class);
            carts.add(cart);
    }

        return carts;
    }
}

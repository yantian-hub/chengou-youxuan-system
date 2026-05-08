package com.cloud.mail.service.cart.service;

import com.cloud.mail.api.cart.model.Cart;

import java.util.List;

public interface CartService {
    //添加购物车
    Boolean addCart(String id , String userId , Integer num);

    List<Cart> list(String s);

    List<Cart> listByIds(List<String> Ids , String s);

    //删除购物车项
    Boolean deleteCart(String skuId, String userId);

    //修改购物车商品数量
    Boolean updateCartNum(String skuId, String userId, Integer num);

    //清空购物车
    void clearCart(String userId);
}

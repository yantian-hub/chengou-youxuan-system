package com.cloud.mail.service.cart.service;

import com.cloud.mail.api.cart.model.Cart;

import java.util.List;

public interface CartService {
    //添加购物车
    Boolean addCart(String id , String userId , Integer num);

    List<Cart> list(String s);

    List<Cart> listByIds(List<String> Ids , String s);
}

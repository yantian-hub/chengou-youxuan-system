package com.cloud.mail.service.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.goods.model.Sku;

import java.util.List;

public interface SkuService extends IService<Sku> {
    List<Sku> typeSkuItems(Integer typeId);

    //根据分类id删除指定数据
    void deletetypeSkuItems(Integer categoryIdd);

    //修改数据
    List<Sku> updateTypeSkuItems(Integer categoryId);

    //库存递减
    void docunt(List<Cart> carts);
}

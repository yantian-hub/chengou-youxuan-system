package com.cloud.mail.service.goods.controller;


import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;

    //根据推广分类查询推广产品列表
    @GetMapping("/aditems/type")
    public RespResult<List<Sku>> typeSkuItems(@RequestParam(value="id") Integer id) {
        List<Sku> skus = skuService.typeSkuItems(id);
        return RespResult.ok(skus);

    }

    //根据推广分类删除推广产品列表
    @DeleteMapping("/aditems/type")
    public RespResult<Object> deletetypeSkuItems(@RequestParam(value="id") Integer id) {
        skuService.deletetypeSkuItems(id);
        return RespResult.ok();
    }

    //根据推广分类查询推广产品列表
    @PutMapping("/aditems/type")
    public RespResult<Object> updatetypeSkuItems(@RequestParam(value="id") Integer id) {
        skuService.updateTypeSkuItems(id);
        return RespResult.ok();
    }

    //根据id查询商品详情
    @GetMapping("/{id}")
    public RespResult<Sku> one(@PathVariable("id") Integer id) {
        Sku sku = skuService.getById(id);
        return RespResult.ok(sku);
    }

    //库存递减
    @PostMapping("/docunt")
    public void docunt(@RequestBody List<Cart> carts) {
        skuService.docunt(carts);
    }
}

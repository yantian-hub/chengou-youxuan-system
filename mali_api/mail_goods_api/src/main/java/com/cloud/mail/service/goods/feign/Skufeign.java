package com.cloud.mail.service.goods.feign;

import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mail-service-goods")
@RequestMapping("/goods/sku")
public interface Skufeign {
    @GetMapping("/aditems/type")
    RespResult<List<Sku>> typeSkuItems(@RequestParam(value="id") Integer id);

    //根据推广分类删除推广产品列表
    @DeleteMapping("/aditems/type")
    RespResult<Object> deletetypeSkuItems(@RequestParam(value="id") Integer id);

    //根据推广分类查询推广产品列表
    @PutMapping("/aditems/type")
    RespResult<Object> updatetypeSkuItems
    (@RequestParam(value="id") Integer id);

    @GetMapping("/{id}")
    public RespResult<Sku> one(@PathVariable("id") Integer id) ;

    //库存递减
    @PostMapping("/docunt")
    public void docunt(@RequestBody List<Cart> carts);
}

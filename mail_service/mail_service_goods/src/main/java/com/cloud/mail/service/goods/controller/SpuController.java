package com.cloud.mail.service.goods.controller;


import com.cloud.mail.service.goods.model.Product;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods/spu")
public class SpuController {
    @Autowired
    SpuService spuService;

    //产品保存
    @PostMapping("/save")
    public RespResult save (@RequestBody Product product){
        spuService.saveProduct(product);
        return RespResult.ok();
    }
}

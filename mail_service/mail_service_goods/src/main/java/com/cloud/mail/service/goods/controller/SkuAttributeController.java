package com.cloud.mail.service.goods.controller;


import com.cloud.mail.service.goods.model.SkuAttribute;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.SkuAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods/skuAttribute")
public class SkuAttributeController {
    private SkuAttributeService skuAttributeService;
    @Autowired
    public SkuAttributeController(SkuAttributeService skuAttributeService) {
        this.skuAttributeService = skuAttributeService;
    }
    //根据分类id查询属性集合
    @GetMapping("/category/{id}")
    public RespResult<List<SkuAttribute>> queryList(@PathVariable("id") Integer categoryId) {
        return RespResult.ok(skuAttributeService.queryList(categoryId));
    }
}

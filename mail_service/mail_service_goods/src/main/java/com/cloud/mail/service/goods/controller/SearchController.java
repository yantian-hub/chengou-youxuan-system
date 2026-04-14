package com.cloud.mail.service.goods.controller;

import com.cloud.mail.service.goods.model.Brand;
import com.cloud.mail.service.goods.service.SearchIService;
import com.cloud.util.RespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods/search")
public class SearchController {
    @Autowired
    private SearchIService searchService;

    //查询品牌
    @RequestMapping("/brand")
    public RespResult<List<Brand>> queryBrand(
            @RequestParam(value = "name", required = false)String name
    ) {
        return  RespResult.ok(searchService.queryBrandByName(name));
    }
    //查询商品
    @RequestMapping("/spu")
    public RespResult<List> querySpu(
            @RequestParam(value = "name", required = false)String name) {
        return  RespResult.ok(searchService.querySpuByName(name));
    }

}

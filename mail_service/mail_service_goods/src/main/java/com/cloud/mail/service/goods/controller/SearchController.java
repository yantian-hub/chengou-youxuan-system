package com.cloud.mail.service.goods.controller;

import com.cloud.mail.service.goods.model.Brand;
import com.cloud.mail.service.goods.service.SearchIService;
import com.cloud.util.RespResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品搜索", description = "品牌搜索、商品搜索")
@RestController
@RequestMapping("/goods/search")
public class SearchController {
    @Autowired
    private SearchIService searchService;

    @Operation(summary = "按名称查询品牌")
    @RequestMapping("/brand")
    public RespResult<List<Brand>> queryBrand(
            @Parameter(description = "品牌名称（可选）") @RequestParam(value = "name", required = false)String name
    ) {
        return  RespResult.ok(searchService.queryBrandByName(name));
    }
    @Operation(summary = "按名称搜索商品")
    @RequestMapping("/spu")
    public RespResult<List> querySpu(
            @Parameter(description = "商品名称（可选）") @RequestParam(value = "name", required = false)String name) {
        return  RespResult.ok(searchService.querySpuByName(name));
    }

}

package com.cloud.mail.service.goods.controller;

import com.cloud.mail.service.goods.model.SkuAttribute;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.SkuAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SKU属性管理", description = "商品SKU属性增删改查")
@RestController
@RequestMapping("/goods/skuAttribute")
public class SkuAttributeController {
    private final SkuAttributeService skuAttributeService;

    @Autowired
    public SkuAttributeController(SkuAttributeService skuAttributeService) {
        this.skuAttributeService = skuAttributeService;
    }

    @Operation(summary = "新增SKU属性")
    @PutMapping
    public RespResult<String> add(@RequestBody SkuAttribute skuAttribute) {
        skuAttributeService.save(skuAttribute);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "更新SKU属性")
    @PostMapping
    public RespResult<String> update(@RequestBody SkuAttribute skuAttribute) {
        skuAttributeService.updateById(skuAttribute);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "删除SKU属性")
    @DeleteMapping("/{id}")
    public RespResult<String> delete(@Parameter(description = "属性ID") @PathVariable Integer id) {
        skuAttributeService.removeById(id);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "根据ID查询SKU属性")
    @GetMapping("/{id}")
    public RespResult<SkuAttribute> getById(@Parameter(description = "属性ID") @PathVariable Integer id) {
        return RespResult.ok(skuAttributeService.getById(id));
    }

    @Operation(summary = "查询全部SKU属性")
    @GetMapping("/list")
    public RespResult<List<SkuAttribute>> list() {
        return RespResult.ok(skuAttributeService.list());
    }

    @Operation(summary = "根据分类ID查询SKU属性")
    @GetMapping("/category/{id}")
    public RespResult<List<SkuAttribute>> queryList(@Parameter(description = "分类ID") @PathVariable("id") Integer categoryId) {
        return RespResult.ok(skuAttributeService.queryList(categoryId));
    }
}

package com.cloud.mail.service.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SKU管理", description = "商品SKU增删改查、推广、库存递减")
@RestController
@RequestMapping("/goods/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;

    @Operation(summary = "新增SKU")
    @PutMapping
    public RespResult<String> add(@RequestBody Sku sku) {
        skuService.save(sku);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "更新SKU")
    @PostMapping
    public RespResult<String> update(@RequestBody Sku sku) {
        skuService.updateById(sku);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "删除SKU")
    @DeleteMapping("/{id}")
    public RespResult<String> delete(@Parameter(description = "SKU ID") @PathVariable String id) {
        skuService.removeById(id);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "查询全部SKU")
    @GetMapping("/list")
    public RespResult<List<Sku>> list() {
        return RespResult.ok(skuService.list());
    }

    @Operation(summary = "根据ID查询SKU详情")
    @GetMapping("/{id}")
    public RespResult<Sku> one(@Parameter(description = "SKU ID") @PathVariable("id") Integer id) {
        Sku sku = skuService.getById(id);
        return RespResult.ok(sku);
    }

    @Operation(summary = "分页查询SKU")
    @PostMapping("/search/{page}/{size}")
    public RespResult<Page<Sku>> search(@RequestBody Sku sku,
                                        @Parameter(description = "页码") @PathVariable Integer page,
                                        @Parameter(description = "每页条数") @PathVariable Integer size) {
        QueryWrapper<Sku> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(sku.getName() != null, "name", sku.getName());
        queryWrapper.eq(sku.getBrandId() != null, "brand_id", sku.getBrandId());
        queryWrapper.eq(sku.getCategoryId() != null, "category_id", sku.getCategoryId());
        return RespResult.ok(skuService.page(new Page<>(page, size), queryWrapper));
    }

    @Operation(summary = "根据推广分类查询推广商品")
    @GetMapping("/aditems/type")
    public RespResult<List<Sku>> typeSkuItems(@Parameter(description = "推广分类ID") @RequestParam(value="id") Integer id) {
        List<Sku> skus = skuService.typeSkuItems(id);
        return RespResult.ok(skus);
    }

    @Operation(summary = "删除推广分类")
    @DeleteMapping("/aditems/type")
    public RespResult<Object> deletetypeSkuItems(@Parameter(description = "推广分类ID") @RequestParam(value="id") Integer id) {
        skuService.deletetypeSkuItems(id);
        return RespResult.ok();
    }

    @Operation(summary = "更新推广分类")
    @PutMapping("/aditems/type")
    public RespResult<Object> updatetypeSkuItems(@Parameter(description = "推广分类ID") @RequestParam(value="id") Integer id) {
        skuService.updateTypeSkuItems(id);
        return RespResult.ok();
    }

    @Operation(summary = "库存递减（购物车下单后调用）")
    @PostMapping("/docunt")
    public void docunt(@RequestBody List<Cart> carts) {
        skuService.docunt(carts);
    }
}

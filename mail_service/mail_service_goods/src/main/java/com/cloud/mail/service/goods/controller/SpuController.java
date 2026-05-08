package com.cloud.mail.service.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.mail.service.goods.model.Product;
import com.cloud.mail.service.goods.model.Spu;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SPU管理", description = "商品SPU/产品管理")
@RestController
@RequestMapping("/goods/spu")
public class SpuController {
    @Autowired
    SpuService spuService;

    @Operation(summary = "保存商品（SPU+SKU组合保存）")
    @PostMapping("/save")
    public RespResult<String> save(@RequestBody Product product) {
        spuService.saveProduct(product);
        return RespResult.ok();
    }

    @Operation(summary = "根据ID查询SPU")
    @GetMapping("/{id}")
    public RespResult<Spu> getById(@Parameter(description = "SPU ID") @PathVariable String id) {
        return RespResult.ok(spuService.getById(id));
    }

    @Operation(summary = "更新SPU")
    @PostMapping
    public RespResult<String> update(@RequestBody Spu spu) {
        spuService.updateById(spu);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "删除SPU")
    @DeleteMapping("/{id}")
    public RespResult<String> delete(@Parameter(description = "SPU ID") @PathVariable String id) {
        spuService.removeById(id);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "分页查询SPU")
    @PostMapping("/search/{page}/{size}")
    public RespResult<Page<Spu>> search(@RequestBody Spu spu,
                                        @Parameter(description = "页码") @PathVariable Integer page,
                                        @Parameter(description = "每页条数") @PathVariable Integer size) {
        QueryWrapper<Spu> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(spu.getName() != null, "name", spu.getName());
        queryWrapper.eq(spu.getBrandId() != null, "brand_id", spu.getBrandId());
        queryWrapper.eq(spu.getIsMarketable() != null, "is_marketable", spu.getIsMarketable());
        return RespResult.ok(spuService.page(new Page<>(page, size), queryWrapper));
    }
}

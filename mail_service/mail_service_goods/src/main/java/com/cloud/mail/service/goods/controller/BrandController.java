package com.cloud.mail.service.goods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.mail.service.goods.model.Brand;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "品牌管理", description = "品牌增删改查、分页、按分类查询")
@RestController
@RequestMapping("/goods/brand")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService =  brandService;
    }


    @Operation(summary = "添加品牌")
    @PutMapping
    public RespResult<String> addBrand(@RequestBody Brand brand) {
        boolean result = brandService.save(brand);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "更新品牌")
    @PostMapping
    public RespResult<String> updateBrand(@RequestBody Brand brand) {
        boolean result = brandService.updateById(brand);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{id}")
    public RespResult<String> deleteBrand(@Parameter(description = "品牌ID") @PathVariable String id) {
        boolean result = brandService.removeById(id);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "条件查询品牌")
    @PostMapping("/search")
    public RespResult<List<Brand>> searchBrand(@RequestBody Brand brand) {
       List<Brand> brands = brandService.queryList(brand);
       return RespResult.ok(brands);
    }

    @Operation(summary = "分页查询品牌")
    @PostMapping("/search/{page}/{size}")
    public RespResult<Page<Brand>> queryPageList
            (@RequestBody Brand brand, @Parameter(description = "页码") @PathVariable Integer page, @Parameter(description = "每页条数") @PathVariable Integer size) {
        Page<Brand> brands =  brandService.queryList(brand, page, size);
        return RespResult.ok(brands);
    }

    @Operation(summary = "根据分类ID查询品牌")
    @GetMapping("/category/{pid}")
    public RespResult<List<Brand>> queryByCategoryId(@Parameter(description = "分类ID") @PathVariable Integer pid) {
        List<Brand> brands = brandService.queryByCategoryId(pid);
        return RespResult.ok(brands);
    }

}























package com.cloud.mail.service.goods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.mail.service.goods.model.Brand;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods/brand")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService =  brandService;
    }


    @PutMapping
    public RespResult<String> addBrand(@RequestBody Brand brand) {
        boolean result = brandService.save(brand);
        return RespResult.ok("操作成功");
    }

    @PostMapping
    public RespResult<String> updateBrand(@RequestBody Brand brand) {
        boolean result = brandService.updateById(brand);
        return RespResult.ok("操作成功");
    }

    @DeleteMapping("/{id}")
    public RespResult<String> deleteBrand(@PathVariable String id) {
        boolean result = brandService.removeById(id);
        return RespResult.ok("操作成功");
    }

    //条件查询
    @PostMapping("/search")
    public RespResult<List<Brand>> searchBrand(@RequestBody Brand brand) {
       List<Brand> brands = brandService.queryList(brand);
       return RespResult.ok(brands);
    }

    @PostMapping("/search/{page}/{size}")
    public RespResult<Page<Brand>> queryPageList
            (@RequestBody Brand brand, @PathVariable Integer page, @PathVariable Integer size) {
        Page<Brand> brands =  brandService.queryList(brand, page, size);
        return RespResult.ok(brands);
    }

    //根据分类id查询品牌集合
    @GetMapping("/category/{pid}")
    public RespResult<List<Brand>> queryByCategoryId(@PathVariable Integer pid) {
        List<Brand> brands = brandService.queryByCategoryId(pid);
        return RespResult.ok(brands);
    }

}























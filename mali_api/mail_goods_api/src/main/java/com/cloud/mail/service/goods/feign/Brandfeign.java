package com.cloud.mail.service.goods.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.mail.service.goods.model.Brand;
import com.cloud.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mail-service-goods")
@RequestMapping("/goods")
public interface Brandfeign {

    @PutMapping("/brand")
    RespResult<String> addBrand(@RequestBody Brand brand);

    @PostMapping("/brand")
    RespResult<String> updateBrand(@RequestBody Brand brand);

    @DeleteMapping("/brand/{id}")
    RespResult<String> deleteBrand(@PathVariable String id);

    @PostMapping("/brand/search")
    RespResult<List<Brand>> searchBrand(@RequestBody Brand brand);

    @PostMapping("/brand/search/{page}/{size}")
    RespResult<Page<Brand>> queryPageList(
            @RequestBody Brand brand,
            @PathVariable Integer page,
            @PathVariable Integer size);

    @GetMapping("/brand/category/{pid}")
    RespResult<List<Brand>> queryByCategoryId(@PathVariable Integer pid);
}

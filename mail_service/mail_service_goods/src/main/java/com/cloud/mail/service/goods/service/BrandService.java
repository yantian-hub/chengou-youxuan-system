package com.cloud.mail.service.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.service.goods.model.Brand;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;


public interface BrandService extends IService<Brand> {

    //条件查询
    List<Brand> queryList(Brand brand);

    //条件分页查询
    Page<Brand> queryList(Brand brand, Integer page, Integer size);

    //根据分类id查询品牌集合
    List<Brand> queryByCategoryId(Integer categoryId);

    @CacheEvict(key = "#brand.id", beforeInvocation = true)
    boolean updateBrandById(Brand brand);

    @CacheEvict(key = "#brand.id", beforeInvocation = true)
    boolean deleteBrandById(Integer id);

    @CacheEvict(allEntries = true, beforeInvocation = true)
    boolean saveBrand(Brand brand);


}

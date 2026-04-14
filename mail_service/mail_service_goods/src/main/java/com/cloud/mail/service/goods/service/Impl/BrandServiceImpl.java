package com.cloud.mail.service.goods.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.service.goods.model.Brand;
import com.cloud.mail.service.goods.mapper.BrandMapper;
import com.cloud.mail.service.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@CacheConfig(cacheNames = "brands")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {


    private final BrandMapper brandMapper;

    @Autowired
    public BrandServiceImpl(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @Cacheable(key = "#brand.name + ':' + #brand.initial", unless = "#result == null")
    @Override
    public List<Brand> queryList(Brand brand) {
        //条件包装对象
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        //根据名称模糊查询
        queryWrapper.like
                (brand.getName() != null,
                        "name", brand.getName());
        //根据首字母查询
        queryWrapper.eq
                (brand.getInitial() != null,
                        "initial", brand.getInitial());
        return brandMapper.selectList(queryWrapper);
    }

    @Cacheable(key = "'page:' + #page + ':size:' + #size + ':name:' + #brand.name", unless = "#result == null")
    @Override
    public Page<Brand> queryList(Brand brand, Integer page, Integer size) {
        //条件包装对象
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        //根据名称模糊查询
        queryWrapper.like
                (brand.getName() != null,
                        "name", brand.getName());
        return brandMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    //根据分类 id 查询品牌集合
    @Cacheable(key = "'category:' + #categoryId", unless = "#result == null")
    @Override
    public List<Brand> queryByCategoryId(Integer categoryId) {
        List<Integer> brandIds = brandMapper.queryByCategoryId(categoryId);
        if(brandIds == null || brandIds.isEmpty()){
            return List.of();
        }
        return brandMapper.selectList(
                new QueryWrapper<Brand>().in("id", brandIds)
        );

    }

    @CacheEvict(key = "#brand.id", beforeInvocation = true)
    @Override
    public boolean updateBrandById(Brand brand) {
        return this.updateById(brand);
    }

    @CacheEvict(key = "#brand.id", beforeInvocation = true)
    @Override
    public boolean deleteBrandById(Integer id) {
        return this.removeById(id);
    }

    @CacheEvict(allEntries = true, beforeInvocation = true)
    @Override
    public boolean saveBrand(Brand brand) {
        return this.save(brand);
    }

}


























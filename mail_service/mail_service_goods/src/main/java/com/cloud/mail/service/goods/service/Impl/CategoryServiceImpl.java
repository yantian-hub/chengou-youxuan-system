package com.cloud.mail.service.goods.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.service.goods.model.Category;
import com.cloud.mail.service.goods.mapper.CategoryMapper;
import com.cloud.mail.service.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "categories")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;
    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Cacheable(key = "'parent:' + #parentId", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<Category> findByParentId(Integer parentId) {
        //条件包装对象
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        return baseMapper.selectList(queryWrapper);
    }

    // 根据父分类 ID 删除缓存
    @CacheEvict(key = "'parent:' + #parentId", beforeInvocation = true)
    @Override
    public void evictCategoryCacheByParentId(Integer parentId) {}

    // 清空所有分类缓存
    @CacheEvict(allEntries = true, beforeInvocation = true)
    @Override
    public void evictAllCategoryCache() {}
}
































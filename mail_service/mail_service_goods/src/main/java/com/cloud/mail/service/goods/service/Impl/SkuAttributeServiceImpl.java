package com.cloud.mail.service.goods.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.service.goods.model.SkuAttribute;
import com.cloud.mail.service.goods.mapper.SkuAttributeMapper;
import com.cloud.mail.service.goods.service.SkuAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "sku-attributes")
public class SkuAttributeServiceImpl extends
        ServiceImpl<SkuAttributeMapper, SkuAttribute>
        implements SkuAttributeService {

    private final SkuAttributeMapper skuAttributeMapper;
    @Autowired
    public SkuAttributeServiceImpl(SkuAttributeMapper skuAttributeMapper) {
        this.skuAttributeMapper = skuAttributeMapper;
    }

    @Cacheable(key = "'category:' + #categoryId", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<SkuAttribute> queryList(Integer categoryId) {
        return skuAttributeMapper.queryByCategoryId(categoryId);
    }

    // 根据分类 ID 删除属性缓存
    @CacheEvict(key = "'category:' + #categoryId", beforeInvocation = true)
    @Override
    public void evictSkuAttributeCacheByCategory(Integer categoryId) {}

    // 清空所有 SKU 属性缓存
    @CacheEvict(allEntries = true, beforeInvocation = true)
    @Override
    public void evictAllSkuAttributeCache() {}
}












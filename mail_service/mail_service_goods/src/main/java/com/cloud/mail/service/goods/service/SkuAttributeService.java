package com.cloud.mail.service.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.service.goods.model.SkuAttribute;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public interface SkuAttributeService extends IService<SkuAttribute> {

    //根据分类id查询属性集合
    List<SkuAttribute> queryList(Integer categoryId);

    // 根据分类 ID 删除属性缓存
    @CacheEvict(key = "'category:' + #categoryId", beforeInvocation = true)
    void evictSkuAttributeCacheByCategory(Integer categoryId);

    // 清空所有 SKU 属性缓存
    @CacheEvict(allEntries = true, beforeInvocation = true)
    void evictAllSkuAttributeCache();
}

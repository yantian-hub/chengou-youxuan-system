package com.cloud.mail.service.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.service.goods.model.Category;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;


public interface CategoryService extends IService<Category> {
    //根据分类父id查询分类
    List<Category> findByParentId(Integer parentId);

    // 根据父分类 ID 删除缓存
    @CacheEvict(key = "'parent:' + #parentId", beforeInvocation = true)
    void evictCategoryCacheByParentId(Integer parentId);

    // 清空所有分类缓存
    @CacheEvict(allEntries = true, beforeInvocation = true)
    void evictAllCategoryCache();
}

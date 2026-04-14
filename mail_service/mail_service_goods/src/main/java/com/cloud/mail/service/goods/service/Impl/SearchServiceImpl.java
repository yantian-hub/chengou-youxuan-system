package com.cloud.mail.service.goods.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloud.mail.service.goods.mapper.BrandMapper;
import com.cloud.mail.service.goods.mapper.SpuMapper;
import com.cloud.mail.service.goods.model.Brand;
import com.cloud.mail.service.goods.model.Spu;
import com.cloud.mail.service.goods.service.SearchIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchIService {
    // ... existing code ...
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuMapper spuMapper;

    //根据名字模糊查找品牌
    @Override
    public List<Brand> queryBrandByName(String name) {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        // 使用 like 进行模糊查询，%name%
        queryWrapper.like("name", name);
        return brandMapper.selectList(queryWrapper);
    }
    //根据名字模糊查找商品
    @Override
    public List<Spu> querySpuByName(String name) {
    // 1. 创建查询条件
    QueryWrapper<Spu> queryWrapper = new QueryWrapper<>();

    // 2. 设置模糊查询 (SQL: WHERE name LIKE '%name%')
    // isMarketable = 1 确保只搜索已上架的商品
    queryWrapper.like("name", name)
                .eq("is_marketable", 1)
                .eq("is_delete", 0); // 排除已删除的

    // 3. 执行查询
    List<Spu> spuList = spuMapper.selectList(queryWrapper);

    return spuList;
}



}

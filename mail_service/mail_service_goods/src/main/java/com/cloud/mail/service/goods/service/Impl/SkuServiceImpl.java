package com.cloud.mail.service.goods.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.service.goods.model.AdItems;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.mail.service.goods.mapper.AdItemMapper;
import com.cloud.mail.service.goods.mapper.SkuMapper;
import com.cloud.mail.service.goods.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "ad-items-skus")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku>
        implements SkuService {

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private AdItemMapper adItemMapper;

    //@Cacheable(cacheNames = "ad-items-skus", key = "#typeId")
    @Cacheable(key = "#typeId")
    @Override
    public List<Sku> typeSkuItems(Integer typeId) {
        //查询当前分类下的所有列表信息
        QueryWrapper<AdItems> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",typeId);
        List<AdItems> adItems = adItemMapper.selectList(queryWrapper);
        //获取所有列表信息对应的skuId
        List<Integer> skuIds = adItems.stream().map(AdItems::getSkuId).toList();
        return skuMapper.selectBatchIds(skuIds);
    }

    //根据分类id删除缓存指定数据
    @CacheEvict(key = "#categoryId")
    @Override
    public void deletetypeSkuItems(Integer categoryId) {}


    @Cacheable(key = "#categoryId")
    @Override
    public List<Sku> updateTypeSkuItems(Integer categoryId) {
        //查询当前分类下的所有列表信息
        QueryWrapper<AdItems> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",categoryId);
        List<AdItems> adItems = adItemMapper.selectList(queryWrapper);
        //获取所有列表信息对应的skuId
        List<Integer> skuIds = adItems.stream().map(AdItems::getSkuId).toList();
        return skuMapper.selectBatchIds(skuIds);
    }

    @Override
    public void docunt(List<Cart> carts) {
        for(Cart cart:carts){
            String skuid = cart.getSkuId();
            int count = skuMapper.docunt(skuid,cart.getNum());
            if(count==0){
                throw new RuntimeException("库存不足");
            }
        }
    }
}

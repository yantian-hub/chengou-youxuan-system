package com.cloud.mail.service.goods.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.service.goods.model.Product;
import com.cloud.mail.service.goods.model.Sku;
import com.cloud.mail.service.goods.model.Spu;
import com.cloud.mail.service.goods.mapper.BrandMapper;
import com.cloud.mail.service.goods.mapper.CategoryMapper;
import com.cloud.mail.service.goods.mapper.SkuMapper;
import com.cloud.mail.service.goods.mapper.SpuMapper;
import com.cloud.mail.service.goods.service.SpuService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;


@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu>
        implements SpuService {
    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;

    @Autowired
    public SpuServiceImpl(SpuMapper spuMapper, SkuMapper skuMapper,
                          CategoryMapper categoryMapper, BrandMapper brandMapper) {
        this.spuMapper = spuMapper;
        this.skuMapper = skuMapper;
        this.categoryMapper = categoryMapper;
        this.brandMapper = brandMapper;
    }


    //保存商品
    @Transactional
    @Override
    public void saveProduct(Product product) {
        //保存spu
        Spu spu = product.getSpu();
        spu.setIsMarketable(1);//1:上架
        spu.setIsDelete(0);//0:未删除
        spu.setStatus(1);//1:审核通过
        spuMapper.insert(spu);

        LocalDateTime now = LocalDateTime.now();
        String categoryName = categoryMapper.selectById(spu.getCategoryThreeId()).getName();
        String brandName = brandMapper.selectById(spu.getBrandId()).getName();
        //保存List<Sku>
        for(Sku sku : product.getSku()){
            //sku名称
            String name = spu.getName();
            Map<String,String> skuAttribute = JSON.parseObject(sku.getSkuAttribute(), Map.class);
            for(String key : skuAttribute.keySet()){
                name += " " + skuAttribute.get(key);
            }
            sku.setName(name);
            //创建时间
            sku.setCreateTime(now);
            //修改时间
            sku.setUpdateTime(now);
            //分类id
            sku.setCategoryId(spu.getCategoryThreeId());
            //分类名字
            sku.setCategoryName(categoryName);
            //品牌id
            sku.setBrandId(spu.getBrandId());
            //品牌名字
            sku.setBrandName(brandName);
            //spuid
            sku.setSpuId(spu.getId());
            //状态
            sku.setStatus(1);
            skuMapper.insert(sku);
        }

    }
}



























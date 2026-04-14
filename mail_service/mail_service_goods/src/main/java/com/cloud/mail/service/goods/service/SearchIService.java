package com.cloud.mail.service.goods.service;

import com.cloud.mail.service.goods.model.Brand;
import com.cloud.mail.service.goods.model.Spu;

import java.util.List;

public interface SearchIService {
    //搜索相关品牌信息
    List<Brand> queryBrandByName(String name);

    //根据名字模糊查找商品
    List<Spu> querySpuByName(String name);



}

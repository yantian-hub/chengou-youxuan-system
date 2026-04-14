package com.cloud.mail.service.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.service.goods.model.Product;
import com.cloud.mail.service.goods.model.Spu;

public interface SpuService extends IService<Spu> {

    //产品保存
    void saveProduct(Product product);
}



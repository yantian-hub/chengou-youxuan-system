package com.cloud.mail.service.goods.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Setter
@Getter
public class Product implements Serializable {

    private Spu spu;

    private List<Sku> sku;

    public Product() {
    }

    public Product(Spu spu, List<Sku> sku) {
        this.spu = spu;
        this.sku = sku;
    }

}

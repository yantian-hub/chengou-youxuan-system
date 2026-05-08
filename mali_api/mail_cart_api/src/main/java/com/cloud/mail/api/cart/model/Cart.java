package com.cloud.mail.api.cart.model;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private String id;
    private String userId;
    private String name;        //商品名字
    private Integer price;
    private String  skuId;
    private Integer num;
}

package com.cloud.mail.api.order.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("order_sku")
public class Ordersku implements Serializable {

    private String id;

    @TableField("spu_id")
    private String spuId;

    @TableField("sku_id")
    private String skuId;

    @TableField("order_id")
    private String orderId;

    private String name;

    private Integer price;

    private Integer num;

    private Integer money;

    private String image;

}
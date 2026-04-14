package com.cloud.mail.service.goods.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TableName("sku")
public class Sku {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private Integer price;

    private Integer num;

    @TableField("brand_id")
    private Integer brandId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("spu_id")
    private String spuId;

    @TableField("category_id")
    private Integer categoryId;

    @TableField("category_name")
    private String categoryName;

    @TableField("brand_name")
    private String brandName;

    @TableField("sku_attribute")
    private String skuAttribute;

    private Integer status;
}

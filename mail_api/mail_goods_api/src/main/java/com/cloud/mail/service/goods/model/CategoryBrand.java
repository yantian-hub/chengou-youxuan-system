package com.cloud.mail.service.goods.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TableName("category_brand")
public class CategoryBrand {

    @TableField("category_id")
    private Integer categoryId;

    @TableField("brand_id")
    private Integer brandId;
}

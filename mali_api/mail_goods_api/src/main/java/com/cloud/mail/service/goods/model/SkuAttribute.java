package com.cloud.mail.service.goods.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TableName("sku_attribute")
public class SkuAttribute {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String options;

    private Integer sort;

    @TableField("category_id")
    private String categoryId;

    @TableField(exist = false)
    private List<Category> categories;
}

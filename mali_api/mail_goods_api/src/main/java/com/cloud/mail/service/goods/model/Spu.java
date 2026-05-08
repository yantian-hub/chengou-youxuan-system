package com.cloud.mail.service.goods.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("spu")
public class Spu {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String intro;

    @TableField("brand_id")
    private Integer brandId;

    @TableField("category_one_id")
    private Integer categoryOneId;

    @TableField("category_two_id")
    private Integer categoryTwoId;

    @TableField("category_three_id")
    private Integer categoryThreeId;

    private String images;

    @TableField("after_sales_service")
    private String afterSalesService;

    private String content;

    @TableField("attribute_list")
    private String attributeList;

    @TableField("is_marketable")
    private Integer isMarketable;

    @TableField("is_delete")
    private Integer isDelete;

    private Integer status;
}

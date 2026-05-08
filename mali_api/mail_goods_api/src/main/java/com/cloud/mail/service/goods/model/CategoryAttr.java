package com.cloud.mail.service.goods.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TableName("category_attr")
public class CategoryAttr {

    private Integer categoryId;

    private String arrtId;


}

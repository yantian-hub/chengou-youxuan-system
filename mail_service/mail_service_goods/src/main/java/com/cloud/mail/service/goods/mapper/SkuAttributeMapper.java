package com.cloud.mail.service.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.service.goods.model.SkuAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkuAttributeMapper extends BaseMapper<SkuAttribute> {

    //根据分类i的查询属性id集合，然后根据id集合查询属性集合
    @Select("select sku_attribute where id in (" +
            "select attr_id from category_attr where category_id=#{id})")
    List<SkuAttribute> queryByCategoryId(Integer id);
}

package com.cloud.mail.service.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.service.goods.model.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrandMapper extends BaseMapper<Brand> {

    //根据分类Id查询品牌id集合
    @Select("select brand_id from category_brand where category_id=#{id}")
    List<Integer> queryByCategoryId(@Param("id") Integer id);
}





















package com.cloud.mail.service.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.service.goods.model.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SkuMapper  extends BaseMapper<Sku> {
    @Update("update sku set num=num-#{num} where id=#{id} and num>=#{num}")
    int docunt(@Param("id")String id , @Param("num")Integer num);
}

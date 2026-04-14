package com.cloud.mail.service.usr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mail.api.usr.model.Usr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UsrLoginMapper extends BaseMapper<Usr> {
    //检查usr库中是否存在该手机号
    @Select("select count(*) from usr where phone = #{phone}")
    int checkPhone(String phone);
}

package com.cloud.mail.api.usr.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Usr")
public class Usr {
    @TableId(type = IdType.AUTO)
    private long id;
    private String name;
    private String salt;//密码盐
    private String password;
    private String phone;
    //code不在数据库
    @TableField(exist = false)
    private String code;
}

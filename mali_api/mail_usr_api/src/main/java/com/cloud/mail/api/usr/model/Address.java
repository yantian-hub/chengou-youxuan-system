package com.cloud.mail.api.usr.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("address")
public class Address implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Long userId;

    private String provinceid;

    private String cityid;

    private String areaid;

    private String phone;

    private String address;

    private String contact;

    @TableField("is_default")
    private Integer isDefault;
}

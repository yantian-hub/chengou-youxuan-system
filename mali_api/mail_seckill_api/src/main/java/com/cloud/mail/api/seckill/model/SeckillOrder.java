package com.cloud.mail.api.seckill.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("seckill_order")
public class SeckillOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;


    private String seckillGoodsId;

    private Integer money;

    private String username;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private Integer status;

    private String weixinTransactionId;
}

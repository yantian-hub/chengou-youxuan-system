package com.cloud.mail.api.order.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@TableName("order")
public class Order implements Serializable {

    private String id;

    private List<String> CartIds;

    private Integer totalNum;

    private Integer money;

    private String payType;

    private Date createTime;

    private Date updateTime;

    private LocalDateTime payTime;

    private LocalDateTime consignTime;

    private LocalDateTime endTime;

    private String userId;

    private String recipients;

    private String recipientsMobiles;

    private String recipientsAddress;

    private String weixinTransactionId;

    private Integer orderStatus;

    private Integer payStatus;

    private Boolean isDelete;

}

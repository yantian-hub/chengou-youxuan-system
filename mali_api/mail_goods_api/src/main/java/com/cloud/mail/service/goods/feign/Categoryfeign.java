package com.cloud.mail.service.goods.feign;

import com.cloud.mail.service.goods.model.Category;
import com.cloud.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "mail-service-goods")
@RequestMapping("/goods")
public interface Categoryfeign {

    @RequestMapping("/parent/{id}")
    RespResult<List<Category>> findByParentId(@PathVariable("id") Integer parentId);
}
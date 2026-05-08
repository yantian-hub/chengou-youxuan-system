package com.cloud.mail.service.goods.feign;

import com.cloud.mail.service.goods.model.SkuAttribute;
import com.cloud.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "mail-service-goods")
@RequestMapping("/goods/skuAttribute")
public interface SkuAttributefiegn {
    @GetMapping("/category/{id}")
    public RespResult<List<SkuAttribute>> queryList(@PathVariable("id") Integer categoryId);
}

package com.cloud.mail.service.goods.feign;

import com.cloud.mail.service.goods.model.Product;
import com.cloud.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "mail-service-goods")
@RequestMapping("/goods/spu")
public interface Spufeign {
    @PostMapping("/save")
    public RespResult save (@RequestBody Product product);
}

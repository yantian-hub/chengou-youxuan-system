package com.cloud.mail.service.goods.feign;

import com.cloud.mail.service.goods.model.Brand;
import com.cloud.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mail-service-goods")
@RequestMapping("/goods/search")
public interface Searchfeign {
    //查询品牌
    @RequestMapping("/brand")
    public RespResult<List<Brand>> queryBrand(
            @RequestParam(value = "name", required = false)String name
    );
    //查询商品
    @RequestMapping("/spu")
    public RespResult<List> querySpu(
            @RequestParam(value = "name", required = false)String name);
}

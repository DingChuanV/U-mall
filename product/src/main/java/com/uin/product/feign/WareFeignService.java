package com.uin.product.feign;

import com.uin.to.SkuHasStcokVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("ware")
public interface WareFeignService {
    @RequestMapping("/ware/waresku/getSkuHasStocks")
    public List<SkuHasStcokVo> getSkuHasStock(@RequestBody List<Long> ids);
}

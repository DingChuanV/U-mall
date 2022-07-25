package com.uin.esclient.feign;


import com.uin.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("product")
public interface ProductFeignService {
    @GetMapping("product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);
}

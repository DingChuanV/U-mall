package com.uin.esclient.feign;


import com.uin.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("product")
public interface ProductFeignService {
    @GetMapping("product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

    @GetMapping("product/brand/infos}")
    R productInfo(@RequestParam("brandIds") List<Long> branId);
}

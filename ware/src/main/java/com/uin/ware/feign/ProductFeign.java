package com.uin.ware.feign;

import com.uin.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("product")
public interface ProductFeign {

    /**
     * /product/skuinfo/info/{skuId}
     *      @FeignClient("product")
     *      第一种是ware给Product服务发起请求，product有向网关发送请求
     * /api/product/skuinfo/info/{skuId}
     *      @FeignClient("gateway")
     *      第二种是直接给网关发请求，网关在路由到product服务
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}

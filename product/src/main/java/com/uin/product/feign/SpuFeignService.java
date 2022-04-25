package com.uin.product.feign;

import com.uin.to.SkuReductionTo;
import com.uin.to.SpuBoundsTo;
import com.uin.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("coupon")
public interface SpuFeignService {
    /**
     * SpringCloud的远程逻辑
     * 1. 我们有一个service调用了远程服务的SpuFeignService.#saveSpuBounds(spuBoundsTo)
     * 我们的SpringCloud就会将spuBoundsTo对象转化json（也就是我们需要添加@RequestBody这个注解）
     * 2.SpringCloud会在（注册中心找到这个服务下的请求）我们的远程服务发送/coupon/spubounds/save 这个请求。
     * 会将上一步转的json放在请求体中，发送请求。
     * 3.对方服务接受到请求了（也就是请求体中json数据），由于对方的服务的中也写了@RequestBody SpuBoundsEntity spuBounds，
     * 他就会自动将json数据转换成对象SpuBoundsEntity
     * <p>
     * 总结：只要json数据模型有兼容，我们双方服务无需需要同一个TO（数据传输对象）
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}

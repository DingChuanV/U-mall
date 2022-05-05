package com.uin.product.feign;

import com.uin.to.es.SpuEsTO;
import com.uin.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("esclient")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SpuEsTO> esTOList);

}

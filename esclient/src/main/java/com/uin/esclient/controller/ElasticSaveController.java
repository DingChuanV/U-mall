package com.uin.esclient.controller;

import com.uin.esclient.service.ProductSaveService;
import com.uin.exception.UinException;
import com.uin.to.es.SpuEsTO;
import com.uin.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;

    /**
     * 将上架的商品的保存到ES服务器中
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SpuEsTO> esTOList) {
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(esTOList);
        } catch (IOException e) {
            log.error("商品上架的ElasticSaveController的接口出现异常{}", e);
            return R.error(UinException.PRODUCT_UP_EXCEPTION.getCode(), UinException.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if (b) {
            return R.ok();
        } else {
            return R.error(UinException.PRODUCT_UP_EXCEPTION.getCode(), UinException.PRODUCT_UP_EXCEPTION.getMessage());
        }
    }
}

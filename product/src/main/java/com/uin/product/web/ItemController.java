package com.uin.product.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    /**
     * 展示当前sku的详情页
     *
     * @param skuId
     * @return java.lang.String
     * @author wanglufei
     * @date 2022/9/19 10:21 PM
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId) {
        System.out.println("准备查询" + skuId + "的详情");
        return "item.html";
    }
}

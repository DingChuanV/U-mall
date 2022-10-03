package com.uin.product.web;

import com.uin.product.service.SkuInfoService;
import com.uin.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情页
     *
     * @param skuId
     * @return java.lang.String
     * @author wanglufei
     * @date 2022/9/19 10:21 PM
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        System.out.println("准备查询" + skuId + "的详情");
        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("iten", vo);
        return "item";
    }
}

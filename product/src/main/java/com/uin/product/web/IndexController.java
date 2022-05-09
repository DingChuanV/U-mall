package com.uin.product.web;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;
import com.uin.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntityList = categoryService.getLevel_one();
        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }

    //index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    /**
     * 简单服务
     *
     * @return java.lang.String
     * @author wanglufei
     * @date 2022/5/8 11:28 PM
     */
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }
}

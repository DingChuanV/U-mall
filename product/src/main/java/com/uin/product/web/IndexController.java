package com.uin.product.web;

import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
}

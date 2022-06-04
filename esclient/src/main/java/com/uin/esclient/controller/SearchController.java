package com.uin.esclient.controller;

import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String list(SearchParams params) {
        Object result = mallSearchService.search(params);
        return "list";
    }
}

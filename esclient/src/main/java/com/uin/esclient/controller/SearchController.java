package com.uin.esclient.controller;

import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String list(SearchParams params) {
        SearchResult result = mallSearchService.search(params);
        return "list";
    }
}

package com.uin.esclient.controller;

import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@Slf4j
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String list(SearchParams params) {
        try {
            SearchResult result = mallSearchService.search(params);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return "list";
    }
}

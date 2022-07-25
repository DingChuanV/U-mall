package com.uin.esclient.controller;

import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String list(SearchParams params, Model model, HttpServletRequest request) {
        try {
            params.set_queryString(request.getQueryString());
            SearchResult result = mallSearchService.search(params);
            model.addAttribute("result", result);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return "list";
    }
}

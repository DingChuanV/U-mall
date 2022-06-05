package com.uin.esclient.service;

import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;

import java.io.IOException;

public interface MallSearchService {
    SearchResult search(SearchParams params) throws IOException;
}

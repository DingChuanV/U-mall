package com.uin.esclient.service;

import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParams params);
}

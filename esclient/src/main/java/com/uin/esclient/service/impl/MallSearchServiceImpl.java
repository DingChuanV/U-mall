package com.uin.esclient.service.impl;

import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.SearchParams;
import org.springframework.stereotype.Service;

@Service
public class MallSearchServiceImpl implements MallSearchService {


    /**
     * 根据检索参数 去es中检索 返回检索的结果
     *
     * @param params
     * @return java.lang.Object
     * @author wanglufei
     * @date 2022/6/4 11:08 AM
     */
    @Override
    public Object search(SearchParams params) {

        return null;
    }
}

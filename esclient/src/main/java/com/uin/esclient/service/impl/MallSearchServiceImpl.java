package com.uin.esclient.service.impl;

import com.uin.esclient.config.EsClientConfig;
import com.uin.esclient.constant.EsConstant;
import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    RestHighLevelClient client;

    /**
     * 根据检索参数 去es中检索 返回检索的结果
     *
     * @param params
     * @return java.lang.Object
     * @author wanglufei
     * @date 2022/6/4 11:08 AM
     */
    @Override
    public SearchResult search(SearchParams params) throws IOException {
        //最终要返回的数据
        SearchResult result = null;
        // 1.动态的构建出查询所需要的DSL语句
        // 1.1 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(params);
        // 1.2 执行检索请求
        SearchResponse response = client.search(searchRequest, EsClientConfig.COMMON_OPTIONS);
        result = buildSearchRequest(response);
        // 1.3 分析响应数据成我们想要的格式

        return null;
    }

    /**
     * 构建结果数据
     *
     * @param response
     * @return com.uin.esclient.vo.SearchResult
     * @author wanglufei
     * @date 2022/6/5 8:52 PM
     */
    private SearchResult buildSearchRequest(SearchResponse response) {
        return null;
    }

    /**
     * 组装检索条件 请求
     *
     * @return org.elasticsearch.action.search.SearchRequest
     * @author wanglufei
     * @date 2022/6/5 8:52 PM
     */
    private SearchRequest buildSearchRequest(SearchParams params) {
        //1.构建DSL语句的构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * 1.模糊匹配过滤（按照属性、分类、品牌、价格区间、库存）
         * 3.排序
         * 4.分页
         * 5.高亮
         * 6.聚合分析
         */

        //1.模糊匹配过滤（按照属性、分类、品牌、价格区间、库存）
        //1.1 构建BoolQuery 对象
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        //1.2 must 条件 模糊匹配
        if (!StringUtils.isEmpty(params.getKeyword())) {
            queryBuilder.must(QueryBuilders.matchQuery("skuTitle", params.getKeyword()));
        }
        //1.3 bool filter
        if (params.getCatalog3Id() != null) {
            //1.3.1 按照三级分类的id来查
            queryBuilder.filter(QueryBuilders.termQuery("catalogId", params.getCatalog3Id()));
        }
        //1.3.2 按照品牌的id
        if (params.getBrandId() != null && params.getBrandId().size() > 0) {
            queryBuilder.filter(QueryBuilders.termsQuery("brandId", params.getBrandId()));
        }

        //1.3.3 按照属性的来匹配 嵌入式的查询
        if (params.getAttrs() != null && params.getAttrs().size() > 0) {
            for (String attr : params.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                //attrs:1_5寸:8寸&attrs:2_16G:8G
                String[] s = attr.split("_");
                String attrId = s[0];
                //5寸:8寸
                String[] split = s[1].split(":");
                String attrValue = split[1];

                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));

                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                queryBuilder.filter(attrs);
            }
        }


        //1.3.4 按照仅有库存
        queryBuilder.filter(QueryBuilders.termQuery("hasStock", params.getHasStock() == 1));

        //1.3.5 按照价格区间
        /**
         * 1. 1_500：1-500
         * 2. _500:x>500
         * 3. 500_:x<500
         */
        if (!StringUtils.isEmpty(params.getSkuPrice())) {
            RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
            //将字符串按照 _ 截取
            String[] s = params.getSkuPrice().split("_");
            if (s.length == 2) {
                //1_500
                skuPrice.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                // _500 and 500_
                if (params.getSkuPrice().startsWith("_")) {
                    skuPrice.lte(s[0]);
                } else {
                    skuPrice.gte(s[0]);
                }
            }
            queryBuilder.filter(skuPrice);
        }


        searchSourceBuilder.query(queryBuilder);
        new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return null;
    }
}

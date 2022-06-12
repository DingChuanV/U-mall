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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
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
    public SearchResult search(SearchParams params) {

        //最终要返回的数据
        SearchResult result = null;
        try {
            // 1.动态的构建出查询所需要的DSL语句
            // 1.1 准备检索请求
            SearchRequest searchRequest = buildSearchRequest(params);
            // 1.2 执行检索请求
            SearchResponse response = null;
            response = client.search(searchRequest, EsClientConfig.COMMON_OPTIONS);
            result = buildSearchRequest(response);
            return null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // 1.3 聚合分析 分析响应数据成我们想要的格式
        return result;
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
         * 2.排序
         * 3.分页
         * 4.高亮
         * 5.聚合分析
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

        //2.排序
        /**
         * 排序条件
         * 1.saleCount_desc
         * 2.saleCount_asc
         * 3.skuPrice_asc/desc
         * 4.hotScore_asc/desc
         */
        //2.1 构建DSL语句
        if (StringUtils.isNotEmpty(params.getSort())) {
            String sort = params.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }
        //2.2 分页
        /**
         * pageNum 第1页 -->from:0 size 5
         * pageNum 第2页 -->from:5 size 5
         * pageNum 第3页 -->from:(pageNum-1)*size size 5
         *
         */
        searchSourceBuilder.from((params.getPageNumber() - 1) * EsConstant.SIZE);
        searchSourceBuilder.size(EsConstant.SIZE);

        //2.3 高亮
        if (StringUtils.isNotEmpty(params.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        //测试
        String s = searchSourceBuilder.toString();
        System.out.println("构建的DSL语句：" + s);

        //3.聚合分析
        //3.1 品牌的聚合信息
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //3.1.1 品牌的聚合信息的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        //3.2 品牌的聚合信息
        searchSourceBuilder.aggregation(brand_agg);

        //3.3 分类的聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field(
                "catalogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);

        //3.4 属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //3.4.1 嵌入聚合进行聚合

        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
        //3.4.2 嵌入聚合进行聚合 进行子聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(10));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10));
        attr_agg.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(attr_agg);


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
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
        SearchResult result = new SearchResult();
        //1.封装当前所有的的商品信息
        //result.setProducts();
        //2.封装分页
        //result.setPageNum();
        //result.setTotal();
        //result.setTotalPages();

        //result.setBrands();
        //3.封装属性
        //result.setAttrs();
        //4.分类信息
        //result.setCatalogs();


        return null;
    }
}

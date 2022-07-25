package com.uin.esclient.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.uin.esclient.config.EsClientConfig;
import com.uin.esclient.constant.EsConstant;
import com.uin.esclient.feign.ProductFeignService;
import com.uin.esclient.service.MallSearchService;
import com.uin.esclient.vo.AttrResponseVo;
import com.uin.esclient.vo.BrandVo;
import com.uin.esclient.vo.SearchParams;
import com.uin.esclient.vo.SearchResult;
import com.uin.to.es.SpuEsTO;
import com.uin.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;
    @Resource
    ProductFeignService productFeignService;

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
            result = buildSearchRequest(response, params);
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
        if (params.getHasStock() != null) {
            queryBuilder.filter(QueryBuilders.termQuery("hasStock", params.getHasStock() == 1));
        }

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
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg" +
                ".keyword").size(1));
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
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs" +
                ".attrName.keyword").size(10));
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
     * @param params
     * @return com.uin.esclient.vo.SearchResult
     * @author wanglufei
     * @date 2022/6/5 8:52 PM
     */
    private SearchResult buildSearchRequest(SearchResponse response, SearchParams params) {
        SearchResult result = new SearchResult();
        //1.封装当前所有的的商品信息
        SearchHits hits = response.getHits();

        List<SpuEsTO> list = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String source = hit.getSourceAsString();
                SpuEsTO spuEsTO = JSON.parseObject(source, SpuEsTO.class);
                list.add(spuEsTO);
            }
        }
        result.setProducts(list);
        //2.封装分页
        result.setPageNum(params.getPageNumber());
        long value = hits.getTotalHits().value;
        result.setTotal(value);//总记录数
        // 页码
        int pages = value % EsConstant.SIZE == 0 ? (int) value / EsConstant.SIZE :
                (int) (value / EsConstant.SIZE + 1);
        result.setTotalPages(pages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= pages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        //4.分类信息
        //result.setCatalogs();
        List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> bucket_catalog = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : bucket_catalog) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();

            //分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.valueOf(keyAsString));

            //分类名
            ParsedStringTerms nameAgg = bucket.getAggregations().get("catalog_name_agg");
            String asString = nameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(asString);

            catalogVoList.add(catalogVo);
        }
        //5.封装品牌
        //result.setBrands();
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 品牌的id
            Long keyAsString = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(keyAsString);

            //品牌的name
            ParsedLongTerms brand_name_aggs = bucket.getAggregations().get("brand_name_aggs");
            String keyAsString1 = brand_name_aggs.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(keyAsString1);
            //品牌的img
            String catalog_img_aggs = ((ParsedStringTerms) bucket.getAggregations().get("catalog_img_aggs")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(catalog_img_aggs);

            brandVos.add(brandVo);
        }

        //3.封装属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //属性的id
            long l = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(l);
            //属性的name
            String attr_name_agg = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name_agg);
            //属性的值
            List<String> attr_value_agg = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());
            attrVo.setAttrValue(attr_value_agg);
            attrVos.add(attrVo);
        }
        // 6. 构建面包屑导航
        List<String> attrs = params.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
                String[] split = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //6.1 设置属性值
                navVo.setNavValue(split[1]);
                //6.2 查询并设置属性名
                try {
                    R r = productFeignService.info(Long.parseLong(split[0]));
                    if (r.getCode() == 0) {
                        AttrResponseVo attrResponseVo = JSON.parseObject(JSON.toJSONString(r.get("attr")), new TypeReference<AttrResponseVo>() {
                        });
                        navVo.setNavName(attrResponseVo.getAttrName());
                    }
                } catch (Exception e) {
                    log.error("远程调用商品服务查询属性失败", e);
                }
                //6.3 设置面包屑跳转链接
                String queryString = replaceQueryString(params, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html" + queryString);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        //品牌、分类
        if (params.getBrandId() != null && params.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            //TODO 调用远程服务去查询
            R r = productFeignService.productInfo(params.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer stringBuffer = new StringBuffer();
                String replace = "";
                for (BrandVo item : brand) {
                    stringBuffer.append(item.getBrandName() + ";");
                    replace = replaceQueryString(params, item.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(stringBuffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html" + replace);
            }
            navs.add(navVo);
        }

        return result;
    }

    private String replaceQueryString(SearchParams params, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params.get_queryString().replace("&" + key + "=" + encode, "");
    }
}

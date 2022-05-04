package com.uin.esclient;

import com.alibaba.fastjson.JSON;
import com.uin.esclient.config.EsClientConfig;
import com.uin.esclient.pojo.JsonRootBean;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SpringBootTest
public class EsclientApplicationTests {
    @Autowired
    RestHighLevelClient client;

    @Test
    public void test1() {
        System.out.println(client);
    }

    /**
     * index Api
     */
    @Test
    public void test_index() throws IOException {
        //设置索引的名字
        IndexRequest indexRequest = new IndexRequest("users");
        //添加数据
        indexRequest.id("1");
        //indexRequest.source("username","uin","age","18","gender","男");

        //将User对象转化为JSON数据
        User user = new User();
        user.setUsername("uin");
        user.setGender("男");
        user.setAge(22);
        String s = JSON.toJSONString(user);

        indexRequest.source(s, XContentType.JSON);

        //执行保存数据分为：同步保存和异步保存

        IndexResponse index = client.index(indexRequest, EsClientConfig.COMMON_OPTIONS);
        //提取响应的数据
        System.out.println(index);

    }

    @Data
    class User {
        private String username;
        private String gender;
        private Integer age;
    }

    @Test
    public void test_search() throws IOException {
        //1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //2.指定要检索的索引
        searchRequest.indices("bank");
        //3.构建检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //检索的条件
        builder.query(QueryBuilders.matchQuery("address", "mill"));
        //对年龄进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg")
                .field("age").size(10);
        builder.aggregation(ageAgg);
        //求出这些年龄分布的平均工资
        AvgAggregationBuilder banlanceAvg = AggregationBuilders.avg("banlanceAvg").field(
                "balance");
        builder.aggregation(banlanceAvg);
        //builder.from();
        //builder.size();
        //builder.sort();
        System.out.println("检索条件" + builder);

        //4.执行检索
        SearchResponse search = client.search(searchRequest, EsClientConfig.COMMON_OPTIONS);
        System.out.println(search.toString());

        //5.分析结果 将json数据转换成对象
        //获取所有命中到的数据
        SearchHits hits1 = search.getHits();
        //System.out.println(hits1);
        SearchHit[] hits = hits1.getHits();

        for (SearchHit hit : hits) {
            //Map<String, Object> asMap = hit.getSourceAsMap();
            String sourceAsString = hit.getSourceAsString();
            JsonRootBean bean = JSON.parseObject(sourceAsString, JsonRootBean.class);
            System.out.println("检索出来的对象：" + bean);
            //System.out.println(sourceAsString);
        }
        //Map map = JSON.parseObject(String.valueOf(search), Map.class);

        //6.获取分析之后的数据
        Aggregations aggregations = search.getAggregations();
        //aggregations.asList().forEach(aggregation -> System.out.println("当前聚合的名字" + aggregation
        // .getName()));
        Terms ageAgg1 = aggregations.get("ageAgg");
        ageAgg1.getBuckets().forEach((Consumer<Terms.Bucket>) bucket -> {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString);
        });

        Avg banlanceAvg1 = aggregations.get("banlanceAvg");
        System.out.println("平均薪资："+banlanceAvg1);
    }

}

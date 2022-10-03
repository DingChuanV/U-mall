package com.uin.esclient.service.impl;

import com.alibaba.fastjson.JSON;
import com.uin.esclient.config.EsClientConfig;
import com.uin.esclient.constant.EsConstant;
import com.uin.esclient.service.ProductSaveService;
import com.uin.to.es.SpuEsTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SpuEsTO> esTOList) throws IOException {
        // 保存到ES中
        //1.给ES建立索引（发送post请求，建立好Mapping关系）

        //2.给ES中保存数据（bulk批量操作）
        BulkRequest bulkRequest = new BulkRequest();
        for (SpuEsTO spuEsTO : esTOList) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //指定id
            indexRequest.id(spuEsTO.getSkuId().toString());
            //数据源
            String s = JSON.toJSONString(spuEsTO);
            indexRequest.source(s, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, EsClientConfig.COMMON_OPTIONS);
        //3.保存成功或者失败的处理
        boolean b = bulk.hasFailures();
        log.info("ES保存成功是否{}", !b);
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成：{}", collect);
        return b;
    }
}

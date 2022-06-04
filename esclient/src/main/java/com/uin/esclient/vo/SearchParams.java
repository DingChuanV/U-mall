package com.uin.esclient.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装检索的条件
 */
@Data
public class SearchParams {
    /**
     * 检索关键字 全文匹配
     */
    private String keyword;

    /**
     * 三级分类的id
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * 1.saleCount_desc
     * 2.saleCount_asc
     * 3.skuPrice_asc/desc
     * 4.hotScore_asc/desc
     */
    private Long sort;
    /**
     * 过滤条件
     * 1.hasStock 0/1
     * 2.skuPrice区间
     * 3.品牌 brandId
     */
    private Integer hasStock; //仅显示有货

    private String skuPrice;// 价格区间

    private List<Long> brandId;//品牌 支持多选


    /**
     * 属性 attr
     * 1.价格
     * 2.屏幕尺寸
     * 3.机身存储
     * 4.热点信息
     */
    private List<String> attrs;//按照属性来封装

    private Integer pageNumber;//第几页的数据

}

package com.uin.esclient.vo;

import com.uin.to.es.SpuEsTO;
import lombok.Data;

import java.util.List;

/**
 * 根据前台穿过来的查询参数 返回响应的查询结果
 *
 * @author wanglufei
 * @date 2022/6/4 11:39 AM
 */
@Data
public class SearchResult {

    //es中封装的商品信息
    private List<SpuEsTO> products;

    private Integer pageNum;//当前第几页

    private Long total;//总记录数

    private Integer totalPages;//总页码

    private List<Integer> pageNavs;//导航页

    //品牌信息 当前查询结果涉及的品牌信息
    private List<BrandVo> brands;

    //属性信息
    private List<AttrVo> attrs;

    //分类的信息
    private List<CatalogVo> catalogs;

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}

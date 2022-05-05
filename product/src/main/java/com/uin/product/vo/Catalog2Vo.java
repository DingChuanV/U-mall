package com.uin.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo {
    /**
     * 一级父分类的id
     */
    private String catalog1Id;
    /**
     * 三级分类
     */
    private List<Object> catalog3List;
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }

}

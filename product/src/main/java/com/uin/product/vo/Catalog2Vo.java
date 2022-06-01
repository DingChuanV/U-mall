package com.uin.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo implements Serializable {
    /**
     * 一级父分类的id
     */
    private String catalog1Id;
    /**
     * 三级分类
     */
    private List<Catalog3Vo> catalog3List;
    //二级分类的id
    private String id;
    //二级分类的名字
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo implements Serializable{
        //二级分类的ID
        private String catalog2Id;
        //三级分类的id
        private String id;
        //三级分类的名字
        private String name;
    }

}

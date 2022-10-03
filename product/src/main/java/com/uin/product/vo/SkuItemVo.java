package com.uin.product.vo;

import com.uin.product.entity.SkuImagesEntity;
import com.uin.product.entity.SkuInfoEntity;
import com.uin.product.entity.SpuInfoDescEntity;
import com.uin.product.vo.itemVo.SkuItemSaleAttrVo;
import com.uin.product.vo.itemVo.SpuBaseAttrGroupVo;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    // 1.skuId的基本信息 pms_sku_info
    SkuInfoEntity skuInfoEntity;

    // 2.sku的图片信息 pms_sku_images
    List<SkuImagesEntity> imagesEntities;

    // 3.获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttrVos;

    // 4.获取spu的介绍
    SpuInfoDescEntity desc;

    // 5.获取spu的规格参数信息
    List<SpuBaseAttrGroupVo> groupVos;

}

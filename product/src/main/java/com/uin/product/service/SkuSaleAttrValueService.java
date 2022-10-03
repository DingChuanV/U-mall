package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.SkuSaleAttrValueEntity;
import com.uin.product.vo.itemVo.SkuItemSaleAttrVo;
import com.uin.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取spu的销售属性组合
     *
     * @param spuId
     * @return java.util.List<com.uin.product.vo.itemVo.SkuItemSaleAttrVo>
     * @author wanglufei
     * @date 2022/10/3 7:40 PM
     */
    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);
}


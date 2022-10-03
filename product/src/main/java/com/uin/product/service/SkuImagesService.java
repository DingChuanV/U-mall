package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.SkuImagesEntity;
import com.uin.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * sku的图片信息
     *
     * @param skuId
     * @return java.util.List<com.uin.product.entity.SkuImagesEntity>
     * @author wanglufei
     * @date 2022/9/27 3:41 PM
     */
    List<SkuImagesEntity> getImageBySkuId(Long skuId);
}


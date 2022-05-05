package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.SkuInfoEntity;
import com.uin.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusById(Long spuId);
}


package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.BrandEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 品牌
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateRelation(BrandEntity brand);
}


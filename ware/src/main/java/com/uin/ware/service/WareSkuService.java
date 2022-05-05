package com.uin.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.to.SkuHasStcokVo;
import com.uin.utils.PageUtils;
import com.uin.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:41:51
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStcok(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStcokVo> getSkuHasStocks(List<Long> ids);
}


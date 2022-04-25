package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.SpuInfoEntity;
import com.uin.product.vo.SpuSaveVo;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * spu信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuSaveVo(SpuSaveVo vo);
}


package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.AttrGroupEntity;
import com.uin.product.vo.AttrGroupWithAttrsVo;
import com.uin.product.vo.itemVo.SpuBaseAttrGroupVo;
import com.uin.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取对应分类ID下的属性分组
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAtrrsByCatelogId(Long catelogId);

    /**
     * 获取spu的规格和参数信息
     *
     * @param spuId
     * @param catalogId
     * @return java.util.List<com.uin.product.vo.SkuItemVo.SpuBaseAttrGroupVo>
     * @author wanglufei
     * @date 2022/9/27 4:41 PM
     */
    List<SpuBaseAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}


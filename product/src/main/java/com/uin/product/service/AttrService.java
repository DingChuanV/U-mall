package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.AttrAttrgroupRelationEntity;
import com.uin.product.entity.AttrEntity;
import com.uin.product.vo.AttrRelationVo;
import com.uin.product.vo.AttrResponseVo;
import com.uin.product.vo.AttrVo;
import com.uin.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface AttrService extends IService<AttrEntity> {

    void saveAttrVo(AttrVo attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrResponseVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteAttrRelation(AttrRelationVo[] vos);

    PageUtils getNoRelation(Map<String, Object> params, Long attrgroupId);

    void saveBatchRelation(List<AttrAttrgroupRelationEntity> relationEntities);
}


package com.uin.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uin.product.entity.AttrGroupEntity;
import com.uin.product.vo.itemVo.SpuBaseAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {
    /**
     * 获取spu的规格参数信息
     *
     * @param spuId
     * @param catalogId
     * @return java.util.List<com.uin.product.vo.SkuItemVo.SpuBaseAttrGroupVo>
     * @author wanglufei
     * @date 2022/9/27 8:05 PM
     */
    List<SpuBaseAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}

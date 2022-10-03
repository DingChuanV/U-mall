package com.uin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.AttrAttrgroupRelationDao;
import com.uin.product.dao.AttrGroupDao;
import com.uin.product.entity.AttrEntity;
import com.uin.product.entity.AttrGroupEntity;
import com.uin.product.service.AttrGroupService;
import com.uin.product.service.AttrService;
import com.uin.product.vo.AttrGroupWithAttrsVo;
import com.uin.product.vo.itemVo.SpuBaseAttrGroupVo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //模糊查询
        //如果没有传递参数
        String key = (String) params.get("key");
        //select * from pms_attr_group where catelog_id=? and (attr_group_id=key or
        // attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        //  查询所有
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        } else {
            //根据id查询
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类的id查出当前分类下的所有属性分组
     * 查出每个属性分组的所有属性
     *
     * @param catelogId
     * @return java.util.List<com.uin.product.vo.AttrGroupWithAttrs>
     * @author wanglufei
     * @date 2022/4/24 10:42 PM
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAtrrsByCatelogId(Long catelogId) {
        //1.查询分组信息
        List<AttrGroupEntity> list = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //根据分组信息查询出属性
        List<AttrGroupWithAttrsVo> collect = list.stream().map((item) -> {
            AttrGroupWithAttrsVo attrGroupWithAttrs = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrGroupWithAttrs);
            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupWithAttrs.getAttrGroupId());
            attrGroupWithAttrs.setAttrs(relationAttr);
            return attrGroupWithAttrs;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取spu的规格参数信息
     *
     * @param spuId
     * @param catalogId
     * @return java.util.List<com.uin.product.vo.SkuItemVo.SpuBaseAttrGroupVo>
     * @author wanglufei
     * @date 2022/9/27 4:41 PM
     */
    @Override
    public List<SpuBaseAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        // 1.查出当前spu的对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        AttrGroupDao attrGroupDao = this.getBaseMapper();
        List<SpuBaseAttrGroupVo> vos = attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        return vos;
    }


}

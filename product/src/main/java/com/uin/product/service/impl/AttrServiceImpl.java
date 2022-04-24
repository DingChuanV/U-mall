package com.uin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.uin.constant.ProductConstant;
import com.uin.product.dao.AttrAttrgroupRelationDao;
import com.uin.product.dao.AttrGroupDao;
import com.uin.product.dao.CategoryDao;
import com.uin.product.entity.AttrAttrgroupRelationEntity;
import com.uin.product.entity.AttrGroupEntity;
import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;
import com.uin.product.vo.AttrResponseVo;
import com.uin.product.vo.AttrVo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.uin.product.dao.AttrDao;
import com.uin.product.entity.AttrEntity;
import com.uin.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryService categoryService;


    @Transactional
    @Override
    public void saveAttrVo(AttrVo attrVo) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, entity);
        //保存基本属性
        this.save(entity);
        //保存关联关系属性
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(entity.getAttrId());
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    @Transactional
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        //条件构造器
        //根据attrType
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type",
                "base".equalsIgnoreCase(attrType) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                        ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        //模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //分页
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),
                queryWrapper);


        List<AttrEntity> records = page.getRecords();

        List<AttrResponseVo> collect = records.stream().map((entity) -> {
            AttrResponseVo respVO = new AttrResponseVo();
            //复制属性
            BeanUtils.copyProperties(entity, respVO);
            //设置分类名
            CategoryEntity categoryEntity =
                    categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", entity.getCatelogId()));
            if (categoryEntity != null) {
                respVO.setCatelogName(categoryEntity.getName());
            }

            //如果是查询规格参数才查询设置分组名
            if ("base".equalsIgnoreCase(attrType)) {
                //设置分类的名字和分组的名字
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                        attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", entity.getAttrId()));

                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity =
                            attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq(
                                    "attr_group_id", attrAttrgroupRelationEntity.getAttrGroupId()));
                    respVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            return respVO;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(collect);
        return pageUtils;
    }

    /**
     * 查询属性参数所有信息
     * 并查询分类名、分类的路径、分组名
     *
     * @param attrId
     * @return com.uin.product.vo.AttrResponseVo
     * @author wanglufei
     * @date 2022/4/24 11:03 AM
     */
    /**
     * @Cacheable("attr") 表示可以缓存调用方法（或类中的所有方法）的结果的注释。
     * 每次调用建议的方法时，都会应用缓存行为
     */
    @Cacheable("attr")
    @Transactional
    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.baseMapper.selectById(attrId);
        AttrResponseVo respVo = new AttrResponseVo();
        BeanUtils.copyProperties(attrEntity, respVo);
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //查询设置分组名
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao
                    .selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            //如果分组id不为空，则查出分组名
            if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id",
                        attrAttrgroupRelationEntity.getAttrGroupId()));
                //设置分组名
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                respVo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
            }
        }

        //查询到分类的信息
        CategoryEntity categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id",
                attrEntity.getCatelogId()));
        //设置分类名
        respVo.setCatelogName(categoryEntity.getName());
        //查询设置并设置分类路径
        Long[] path = categoryService.findCatcatelogPath(categoryEntity.getCatId());
        respVo.setCatelogPath(path);
        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);
        this.baseMapper.updateById(entity);

        //只有当属性分组不为空时，说明更新的是规则参数，则需要更新关联表

        if (entity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            if (attr.getAttrGroupId() != null) {
                //查询属性-分组名对应关系
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                Long count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrAttrgroupRelationEntity.getAttrId()));
                if (count > 0) {
                    attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity,
                            new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",
                                    attr.getAttrId()));
                } else {
                    attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
                }
            }
        }


    }

}

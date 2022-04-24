package com.uin.product.service.impl;

import com.uin.product.dao.AttrAttrgroupRelationDao;
import com.uin.product.dao.AttrGroupDao;
import com.uin.product.dao.CategoryDao;
import com.uin.product.entity.AttrAttrgroupRelationEntity;
import com.uin.product.entity.AttrGroupEntity;
import com.uin.product.entity.CategoryEntity;
import com.uin.product.vo.AttrResponseVo;
import com.uin.product.vo.AttrVo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<AttrEntity> page = this.page(
//                new Query<AttrEntity>().getPage(params),
//                new QueryWrapper<AttrEntity>()
//        );
//
//        return new PageUtils(page);
//    }

    @Transactional
    @Override
    public void saveAttrVo(AttrVo attrVo) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, entity);
        //保存基本属性
        this.save(entity);
        //保存关联关系属性
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(entity.getAttrId());
        relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationDao.insert(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        //条件构造器
        //根据attrType
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type",
                "base".equalsIgnoreCase(attrType) ? 1 : 0);

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

}

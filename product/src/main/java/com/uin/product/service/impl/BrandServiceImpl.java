package com.uin.product.service.impl;

import com.uin.product.dao.CategoryBrandRelationDao;
import com.uin.product.entity.CategoryBrandRelationEntity;
import com.uin.product.service.CategoryBrandRelationService;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.uin.product.dao.BrandDao;
import com.uin.product.entity.BrandEntity;
import com.uin.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<BrandEntity>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("brand_id", key)
                    .or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper);

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void updateRelation(BrandEntity brand) {
        //需要保证中间表中的冗余字段的数据和主表的数据一致
        //第一步，先擦自己的屁股
        this.updateById(brand);
        //第二步，在擦别人的屁股
        if (!StringUtils.isEmpty(brand.getName())) {
            categoryBrandRelationService.updateRelationBrand(brand.getBrandId(), brand.getName());
            //TODO 更新其他关联
        }
    }

}

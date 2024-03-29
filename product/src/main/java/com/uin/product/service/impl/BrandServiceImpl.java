package com.uin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.BrandDao;
import com.uin.product.entity.BrandEntity;
import com.uin.product.service.BrandService;
import com.uin.product.service.CategoryBrandRelationService;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<BrandEntity>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper);

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Cacheable(value = "brands", key = "'brandId:'+#root.args[0]")
    @Override
    public List<BrandEntity> getBrandBybranId(List<Long> branId) {
        List<BrandEntity> entities = baseMapper.selectList(new QueryWrapper<BrandEntity>().in(
                "brand_id", branId));
        return entities;
    }

}

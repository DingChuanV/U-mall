package com.uin.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.SkuImagesDao;
import com.uin.product.entity.SkuImagesEntity;
import com.uin.product.service.SkuImagesService;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * sku的图片信息
     *
     * @param skuId
     * @return java.util.List<com.uin.product.entity.SkuImagesEntity>
     * @author wanglufei
     * @date 2022/9/27 3:42 PM
     */
    @Override
    public List<SkuImagesEntity> getImageBySkuId(Long skuId) {
        SkuImagesDao imagesDao = this.baseMapper;
        List<SkuImagesEntity> entityList = imagesDao.selectList(new QueryWrapper<SkuImagesEntity>().eq("spu_id", skuId));
        return entityList;
    }

}

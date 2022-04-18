package com.uin.coupon.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.coupon.dao.SkuLadderDao;
import com.uin.coupon.entity.SkuLadderEntity;
import com.uin.coupon.service.SkuLadderService;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderDao, SkuLadderEntity> implements SkuLadderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuLadderEntity> page = this.page(
                new Query<SkuLadderEntity>().getPage(params),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageUtils(page);
    }

}

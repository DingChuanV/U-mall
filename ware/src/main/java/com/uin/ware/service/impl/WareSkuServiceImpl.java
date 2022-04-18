package com.uin.ware.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import com.uin.ware.dao.WareSkuDao;
import com.uin.ware.entity.WareSkuEntity;
import com.uin.ware.service.WareSkuService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

}

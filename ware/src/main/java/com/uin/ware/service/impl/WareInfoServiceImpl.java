package com.uin.ware.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import com.uin.ware.dao.WareInfoDao;
import com.uin.ware.entity.WareInfoEntity;
import com.uin.ware.service.WareInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

}

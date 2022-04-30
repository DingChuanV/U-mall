package com.uin.ware.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.constant.WareStatusConstant;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import com.uin.ware.dao.PurchaseDao;
import com.uin.ware.dao.PurchaseDetailDao;
import com.uin.ware.entity.PurchaseDetailEntity;
import com.uin.ware.entity.PurchaseEntity;
import com.uin.ware.service.PurchaseDetailService;
import com.uin.ware.service.PurchaseService;
import com.uin.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unReceiveList(Map<String, Object> params) {
        //查询未分配或者未领取


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", 0)
                        .or()
                        .eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long id = mergeVo.getPurchaseId();
        //如果有采购单 就合并

        //如果没有采购单 我们就要新建一个采购单
        if (id == null) {
            //处理默认值
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareStatusConstant.PurchaseStatusEnum.CREATE_PURCHASE.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            id = purchaseEntity.getId();
        }
        List<Long> items = mergeVo.getItems();
        Long finalId = id;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalId);
            detailEntity.setStatus(WareStatusConstant.PurchaseDetailsStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(id);
        entity.setUpdateTime(new Date());
        this.updateById(entity);
    }

}

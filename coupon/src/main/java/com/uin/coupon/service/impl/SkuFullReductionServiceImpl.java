package com.uin.coupon.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.coupon.dao.SkuFullReductionDao;
import com.uin.coupon.entity.MemberPriceEntity;
import com.uin.coupon.entity.SkuFullReductionEntity;
import com.uin.coupon.entity.SkuLadderEntity;
import com.uin.coupon.service.MemberPriceService;
import com.uin.coupon.service.SkuFullReductionService;
import com.uin.coupon.service.SkuLadderService;
import com.uin.to.MemberPrice;
import com.uin.to.SkuReductionTo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1.保存满减打折，会员价
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        skuLadderService.save(skuLadderEntity);

//        //2.保存满减信息
//        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
//        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
//        this.save(skuFullReductionEntity);
//
//        //3.会员价格
//        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrices();
//        List<MemberPriceEntity> collect = memberPrices.stream().map((item) -> {
//            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
//            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
//            memberPriceEntity.setMemberLevelId(item.getId());
//            memberPriceEntity.setMemberLevelName(item.getName());
//            memberPriceEntity.setMemberPrice(item.getPrice());
//            memberPriceEntity.setAddOther(1);
//            return memberPriceEntity;
//        }).collect(Collectors.toList());
//        memberPriceService.saveBatch(collect);
    }

}

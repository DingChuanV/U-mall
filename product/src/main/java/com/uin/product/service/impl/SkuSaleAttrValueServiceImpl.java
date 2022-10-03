package com.uin.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.SkuSaleAttrValueDao;
import com.uin.product.entity.SkuSaleAttrValueEntity;
import com.uin.product.service.SkuSaleAttrValueService;
import com.uin.product.vo.itemVo.SkuItemSaleAttrVo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取spu的销售属性组合
     *
     * @param spuId
     * @return java.util.List<com.uin.product.vo.itemVo.SkuItemSaleAttrVo>
     * @author wanglufei
     * @date 2022/10/3 7:40 PM
     */
    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
        SkuSaleAttrValueDao dao = this.baseMapper;
        List<SkuItemSaleAttrVo> saleAttrVoList = dao.getSaleAttrBySpuId(spuId);
        return saleAttrVoList;
    }

}

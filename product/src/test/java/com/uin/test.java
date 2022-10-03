package com.uin;

import com.uin.product.dao.AttrGroupDao;
import com.uin.product.dao.SkuSaleAttrValueDao;
import com.uin.product.vo.itemVo.SkuItemSaleAttrVo;
import com.uin.product.vo.itemVo.SpuBaseAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wanglufei
 * @description: TODO
 * @date 2022/5/13/4:12 PM
 */
@SpringBootTest
@Slf4j
public class test {
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    public static void main(String[] args) {
        String s = getOldNum(9999L);
        System.out.println(s);
    }

    public static String getOldNum(Long count) {
        String prefix = "label";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String middle = dateFormat.format(new Date());
        String suffix = "001";
        String serialNumber = prefix + middle + suffix;

        if (serialNumber != null && !serialNumber.isEmpty()) {
            long l = Long.parseLong(String.valueOf(count));
            long l1 = ++l;
            serialNumber = String.format(prefix + middle + "%06d", l1);
        }
        return serialNumber;
    }

    @Test
    public void test_() {
        List<SpuBaseAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(9L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    public void test_Sp() {
        List<SkuItemSaleAttrVo> saleAttrBySpuId = skuSaleAttrValueDao.getSaleAttrBySpuId(10L);
        System.out.println(saleAttrBySpuId);
    }
}

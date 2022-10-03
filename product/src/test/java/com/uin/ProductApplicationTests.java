package com.uin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uin.product.entity.BrandEntity;
import com.uin.product.service.AttrGroupService;
import com.uin.product.service.BrandService;
import com.uin.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
class ProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    CategoryService categoryService;


    @Test
    public void test01() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(1L);
        brand.setName("Iphone 12Pro");
        boolean save = brandService.updateById(brand);
        System.out.println(save);
    }

    /**
     * @author wanglufei
     * @date 2022/4/18 9:22 PM
     */
    @Test
    public void test02() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        list.forEach((item) -> {
            System.out.println(item);
        });
        System.out.println(list);
    }

    @Test
    public void test03() {
        Long[] catcatelogPath = categoryService.findCatcatelogPath(225L);
        log.info("完整的路径：{}", Arrays.asList(catcatelogPath));
    }


}

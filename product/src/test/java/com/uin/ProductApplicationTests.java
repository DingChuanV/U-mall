package com.uin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uin.product.entity.BrandEntity;
import com.uin.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ProductApplicationTests {
    @Autowired(required = false)
    BrandService brandService;


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
    /**
     * 测试OSS
     * @author wanglufei
     * @date 2022/4/21 8:33 PM
     */

}

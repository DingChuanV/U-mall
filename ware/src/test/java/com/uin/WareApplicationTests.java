package com.uin;

import com.uin.ware.service.WareSkuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WareApplicationTests {
    @Autowired
    WareSkuService wareSkuService;

    @Test
    public void contextLoads() {
    }

}

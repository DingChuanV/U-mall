package com.uin;

import com.uin.coupon.entity.MemberPriceEntity;
import com.uin.coupon.service.MemberPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponApplicationTests {
    @Autowired
    MemberPriceService memberPriceService;

    @Test
    public void contextLoads() {
        MemberPriceEntity byId = memberPriceService.getById(1L);
        System.out.println(byId);
    }

}

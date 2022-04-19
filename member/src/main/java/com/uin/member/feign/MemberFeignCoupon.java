package com.uin.member.feign;

import com.uin.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wanglufei
 * @description: 远程服务调用的接口
 * @date 2022/4/19/1:20 PM
 */
@FeignClient("coupon")
public interface MemberFeignCoupon {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberWithCoupons();
}

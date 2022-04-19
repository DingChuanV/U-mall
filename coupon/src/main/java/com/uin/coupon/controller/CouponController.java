package com.uin.coupon.controller;

import java.math.BigDecimal;
import java.util.Date;


import com.uin.coupon.entity.CouponEntity;
import com.uin.coupon.service.CouponService;
import com.uin.utils.PageUtils;
import com.uin.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 优惠券信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
@RestController
@RefreshScope
@RequestMapping("coupon/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;
    @Value("${coupon.user.name}")
     String name;
    @Value("${coupon.user.age}")
     Integer age;
    @RequestMapping("/test")
    public R test(){
        return R.ok().put("name",name).put("age",age);
    }

    /**
     * 测试远程服务调用
     * 用户服务想要远程调用优惠卷服务
     *
     * @return com.uin.utils.R
     * @author wanglufei
     * @date 2022/4/19 1:02 PM
     */
    @RequestMapping("/member/list")
    public R memberWithCoupons() {
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("满100减50");
        return R.ok().put("coupons", Arrays.asList(couponEntity));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    /**
     * @RequiresPermissions("coupon:coupon:list")
     */
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    /**
     *@RequiresPermissions("coupon:coupon:info")
     */
    public R info(@PathVariable("id") Long id) {
        CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    /**
     *@RequiresPermissions("coupon:coupon:save")
     */
    public R save(@RequestBody CouponEntity coupon) {
        couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    /**
     * @RequiresPermissions("coupon:coupon:update")
     */
    public R update(@RequestBody CouponEntity coupon) {
        couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    /**
     *@RequiresPermissions("coupon:coupon:delete")
     */
    public R delete(@RequestBody Long[] ids) {
        couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

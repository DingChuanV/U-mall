package com.uin.ware.controller;


import com.uin.utils.PageUtils;
import com.uin.utils.R;
import com.uin.ware.entity.PurchaseEntity;
import com.uin.ware.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:41:51
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    // /ware/purchase/unreceive/list
    /**
     * 查询未领取的采购单
     */
    @RequestMapping("/unreceive/list")
    /**
     * @RequiresPermissions("ware:purchase:list")
     */
    public R unReceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.unReceiveList(params);
        return R.ok().put("page", page);
    }
    ///ware/purchase/merge
    /**
     * 合并采购需求
     */
    @RequestMapping("/unreceive/list")
    /**
     * @RequiresPermissions("ware:purchase:list")
     */
    public R merge(@RequestParam Map<String, Object> params){
        //purchaseId: 1, //整单id
        //  items:[1,2,3,4] //合并项集合
        PageUtils page = purchaseService.mergePurchase(params);
        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    /**
     * @RequiresPermissions("ware:purchase:list")
     */
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    /**
     *@RequiresPermissions("ware:purchase:info")
     */
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    /**
     *@RequiresPermissions("ware:purchase:save")
     */

    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    /**
     *@RequiresPermissions("ware:purchase:update")
     */
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    /**
     *@RequiresPermissions("ware:purchase:delete")
     */
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

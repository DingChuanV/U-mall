package com.uin.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.uin.product.entity.AttrAttrgroupRelationEntity;
import com.uin.product.entity.AttrEntity;
import com.uin.product.service.AttrService;
import com.uin.product.service.CategoryService;
import com.uin.product.vo.AttrGroupWithAttrsVo;
import com.uin.product.vo.AttrRelationVo;
import com.uin.utils.PageUtils;
import com.uin.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uin.product.entity.AttrGroupEntity;
import com.uin.product.service.AttrGroupService;


/**
 * 属性分组
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    AttrService attrService;

    ///product/attrgroup/{catelogId}/withattr
    //获取分类下所有分组&关联属性
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAtrrs(@PathVariable("catelogId") Long catelogId) {
        //1.查处当前分类下的所有属性分组
        //2.查处每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> data =
                attrGroupService.getAttrGroupWithAtrrsByCatelogId(catelogId);
        return R.ok().put("data", data);
    }

    // /product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R AttrRelation(@RequestBody @PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", attrEntities);
    }

    ///product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noAtrrRelation(Map<String, Object> params,
                            @PathVariable("attrgroupId") Long attrgroupId) {
        PageUtils page = attrService.getNoRelation(params, attrgroupId);
        return R.ok().put("page", page);

    }

    ///product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R saveBatch(@RequestBody List<AttrAttrgroupRelationEntity> relationEntities) {
        attrService.saveBatchRelation(relationEntities);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    /**
     *@RequiresPermissions("product:attrgroup:list")
     */
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        //PageUtils page = attrGroupService.queryPage(params);
        /**
         * 获取对应分类id下的属性分组数据
         */
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    /**
     *@RequiresPermissions("product:attrgroup:save")
     */
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    /**
     *@RequiresPermissions("product:attrgroup:info")
     */
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatcatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    /**
     *@RequiresPermissions("product:attrgroup:update")
     */
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    /**
     *@RequiresPermissions("product:attrgroup:delete")
     */
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    ///product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrRelationVo[] vos) {
        attrService.deleteAttrRelation(vos);
        return R.ok();
    }

}

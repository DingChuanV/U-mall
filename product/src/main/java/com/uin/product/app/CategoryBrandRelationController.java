package com.uin.product.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uin.product.entity.CategoryBrandRelationEntity;
import com.uin.product.service.CategoryBrandRelationService;
import com.uin.utils.PageUtils;
import com.uin.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌分类关联
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    ///product/categorybrandrelation/brands/list

    /**
     * RequestParam 获取请求参数的中的参数
     * 小tips
     * 1.controller 只用来处理请求、接受和校验数据
     * 2.service层接受controller传来的数据，进行业务处理
     * 3.controller接受service处理完的数据，封装成页面指定的VO
     */
    @GetMapping("/brands/list")
    public R getBrandsByCategory(@RequestParam("catId") Long catelogId) {
        List<CategoryBrandRelationEntity> entities=categoryBrandRelationService.getBrandsByCayId(catelogId);
        return R.ok().put("data", entities);
    }

    /**
     * 获取品牌关联的分类
     */
    //@RequestMapping(value = "/catelog/list", method = RequestMethod.GET)
    @GetMapping("/catelog/list")
    /**
     *@RequiresPermissions("product:categorybrandrelation:list")
     */
    public R catelogList(@RequestParam("brandId") Long brandId) {
        List<CategoryBrandRelationEntity> data =
                categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    /**
     *@RequiresPermissions("product:categorybrandrelation:list")
     */
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    /**
     *@RequiresPermissions("product:categorybrandrelation:info")
     */
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存  新增品牌与分类关联关系
     */
    @RequestMapping("/save")
    /**
     *@RequiresPermissions("product:categorybrandrelation:save")
     */
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        //categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveBrandCategoty(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    /**
     *@RequiresPermissions("product:categorybrandrelation:update")
     */
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    /**
     *@RequiresPermissions("product:categorybrandrelation:delete")
     */
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}

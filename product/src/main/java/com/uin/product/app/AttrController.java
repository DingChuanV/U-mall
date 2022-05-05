package com.uin.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.uin.product.entity.ProductAttrValueEntity;
import com.uin.product.service.ProductAttrValueService;
import com.uin.product.vo.AttrResponseVo;
import com.uin.product.vo.AttrVo;
import com.uin.utils.PageUtils;
import com.uin.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uin.product.service.AttrService;


/**
 * 商品属性
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;

    ///product/attr/update/{spuId}
    @PostMapping("/update/{spuId}")
    public R updateSpu(@RequestBody List<ProductAttrValueEntity> productAttrValueEntities,
                       @PathVariable("spuId") Long spuId) {
        productAttrValueService.updateSpuAttr(spuId, productAttrValueEntities);
        return R.ok();
    }

    /**
     * 获取spu规格
     * // /product/attr/base/listforspu/{spuId}
     *
     * @PathVariable 路径变量
     * @author wanglufei
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R listSpuAttr(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> data = productAttrValueService.listSpuAttr(spuId);
        return R.ok().put("data", data);
    }

    /**
     * 获取分类规格参数
     *
     * @author wanglufei
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    /**
     *@RequiresPermissions("product:attr:info")
     */
    public R info(@PathVariable("attrId") Long attrId) {
        //AttrEntity attr = attrService.getById(attrId);
        AttrResponseVo respvo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respvo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    /**
     *@RequiresPermissions("product:attr:save")
     */
    public R save(@RequestBody AttrVo attrVo) {
        attrService.saveAttrVo(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    /**
     *@RequiresPermissions("product:attr:update")
     */
    public R update(@RequestBody AttrVo attr) {
        //attrService.updateById(attr);
        attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    /**
     *@RequiresPermissions("product:attr:delete")
     */
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }


}

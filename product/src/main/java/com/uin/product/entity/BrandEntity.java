package com.uin.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.uin.valid.AddGroup;
import com.uin.valid.ListValue;
import com.uin.valid.UpdateGroup;
import com.uin.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @Null(message = "新增品牌brandId必须为空", groups = {AddGroup.class})
    @NotNull(message = "修改品牌brandId不能为空", groups = {UpdateGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     * 带注释的元素不能为空，并且必须包含至少一个非空白符号。接受字符序列。
     */
    @NotBlank(message = "品牌名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;
    /**
     * 品牌logo地址
     */
    @NotEmpty(groups = {AddGroup.class})
    @URL(message = "品牌logo地址不合法", groups = {AddGroup.class, UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(groups = {UpdateStatusGroup.class,AddGroup.class})
    @ListValue(valuse = {0, 1}, groups = {UpdateStatusGroup.class,AddGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotEmpty(groups = {AddGroup.class})
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索字母必须是a-z或者A-Z的字母", groups = {AddGroup.class,
            UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     * 不能为空，接受任何类型
     */
    @NotNull(groups = {AddGroup.class})
    @Min(value = 0, message = "排序规则必须大于等于0", groups = {AddGroup.class,
            UpdateGroup.class})
    private Integer sort;

}

package com.uin.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

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
    @TableId
    private Long brandId;
    /**
     * 品牌名
     * 带注释的元素不能为空，并且必须包含至少一个非空白符号。接受字符序列。
     */
    @NotBlank(message = "品牌名不能为空")
    private String name;
    /**
     * 品牌logo地址
     */
    @URL(message = "品牌logo地址不合法")
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotEmpty
    @Pattern(regexp = "/^[a-zA-Z]$/", message = "检索字母必须是a-z或者A-Z的字母")
    private String firstLetter;
    /**
     * 排序
     * 不能为空，接受任何类型
     */
    @NotNull
    @Min(value = 0,message = "排序规则必须大于等于0")
    private Integer sort;

}

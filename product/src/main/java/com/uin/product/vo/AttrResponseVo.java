package com.uin.product.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class AttrResponseVo extends AttrVo {
    /**
     * "手机/数码/手机", //所属分类名字
     */
    private String catelogName;
    /**
     * "主体", //所属分组名字
     */
    private String groupName;

    private Long[] catelogPath;
}

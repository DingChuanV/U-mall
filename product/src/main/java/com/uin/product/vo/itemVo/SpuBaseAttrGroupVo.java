package com.uin.product.vo.itemVo;

import com.uin.product.vo.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuBaseAttrGroupVo {
    private String attrName;
    private List<Attr> attrValues;
}

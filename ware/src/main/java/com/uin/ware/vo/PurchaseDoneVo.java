package com.uin.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 完成采购的vo对象
 * @author wanglufei
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;
    private List<Itmes> itmes;
}

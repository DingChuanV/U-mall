package com.uin.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.utils.PageUtils;
import com.uin.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:41:51
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


package com.uin.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.member.entity.MemberLevelEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 会员等级
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:54:06
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


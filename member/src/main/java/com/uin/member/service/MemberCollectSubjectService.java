package com.uin.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.member.entity.MemberCollectSubjectEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:54:06
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


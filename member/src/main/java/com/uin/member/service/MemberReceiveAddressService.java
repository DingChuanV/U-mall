package com.uin.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.member.entity.MemberReceiveAddressEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:54:06
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


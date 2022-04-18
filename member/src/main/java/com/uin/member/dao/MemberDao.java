package com.uin.member.dao;

import com.uin.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:54:06
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

package com.rxinyun.admin.member.modular.groups.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.member.modular.groups.entity.ClientMemberGroups;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupMapper extends BaseMapper<ClientMemberGroups> {
}

package com.rxinyun.admin.member.modular.groups.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.member.modular.groups.entity.ClientMemberGroups;
import com.rxinyun.admin.member.modular.groups.param.ClientGroupIdParam;
import com.rxinyun.admin.member.modular.groups.param.ClientMemberGroupsAddParam;
import com.rxinyun.admin.member.modular.groups.param.ClientMemberGroupsEditParam;
import com.rxinyun.admin.member.modular.groups.param.GroupPageParam;
import java.util.List;

public interface GroupsService extends IService<ClientMemberGroups> {
   void add(ClientMemberGroupsAddParam clientMemberGroups);

   void delete(ClientGroupIdParam clientGroupIdParam);

   void update(ClientMemberGroupsEditParam clientMemberGroups);

   ClientMemberGroups queryById(String id);

   Page<ClientMemberGroups> page(GroupPageParam groupPageParam);

   List<ClientMemberGroups> list();
}

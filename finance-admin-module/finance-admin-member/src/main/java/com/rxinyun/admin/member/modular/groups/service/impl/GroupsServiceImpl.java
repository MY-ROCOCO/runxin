package com.rxinyun.admin.member.modular.groups.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.groups.entity.ClientMemberGroups;
import com.rxinyun.admin.member.modular.groups.mapper.GroupMapper;
import com.rxinyun.admin.member.modular.groups.param.ClientGroupIdParam;
import com.rxinyun.admin.member.modular.groups.param.ClientMemberGroupsAddParam;
import com.rxinyun.admin.member.modular.groups.param.ClientMemberGroupsEditParam;
import com.rxinyun.admin.member.modular.groups.param.GroupPageParam;
import com.rxinyun.admin.member.modular.groups.service.GroupsService;
import com.rxinyun.admin.member.modular.user.service.AdminClientUserService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupsServiceImpl extends ServiceImpl<GroupMapper, ClientMemberGroups> implements GroupsService {
   @Resource
   AdminClientUserService adminClientUserService;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ClientMemberGroupsAddParam clientMemberGroupsAddParam) {
      LambdaQueryWrapper<ClientMemberGroups> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.eq(ClientMemberGroups::getGroupName, clientMemberGroupsAddParam.getGroupName());
      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("会员组名称不能重复", new Object[0]);
      } else {
         ClientMemberGroups clientMemberGroups = new ClientMemberGroups();
         BeanUtils.copyProperties(clientMemberGroupsAddParam, clientMemberGroups);
         this.save(clientMemberGroups);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(ClientGroupIdParam clientGroupIdParam) {
      ClientMemberGroups clientMemberGroups = (ClientMemberGroups)this.getById(clientGroupIdParam.getId());
      Long count = this.adminClientUserService.countGroupNumber(clientGroupIdParam.getId());
      if (!count.equals(0L)) {
         throw new CommonException("该会员组下存在会员，不能删除", new Object[0]);
      } else if (clientMemberGroups.getGroupName().equals("普通会员")) {
         throw new CommonException("该会员组为默认会员组，不能删除", new Object[0]);
      } else {
         this.removeById(clientGroupIdParam.getId());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(ClientMemberGroupsEditParam clientMemberGroupsEditParam) {
      LambdaQueryWrapper<ClientMemberGroups> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.eq(ClientMemberGroups::getGroupName, clientMemberGroupsEditParam.getGroupName());
      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("会员组名称不能重复", new Object[0]);
      } else {
         ClientMemberGroups discountTemplate = this.queryEntity(clientMemberGroupsEditParam.getId());
         BeanUtil.copyProperties(clientMemberGroupsEditParam, discountTemplate, new String[0]);
         this.updateById(discountTemplate);
      }
   }

   private ClientMemberGroups queryEntity(String id) {
      ClientMemberGroups clientMemberGroups = (ClientMemberGroups)this.getById(id);
      if (ObjectUtil.isEmpty(clientMemberGroups)) {
         throw new CommonException("用户组不存在：{}", new Object[]{id});
      } else {
         return clientMemberGroups;
      }
   }

   @Override
   public ClientMemberGroups queryById(String id) {
      ClientMemberGroups clientMemberGroups = (ClientMemberGroups)this.getById(id);
      if (clientMemberGroups != null) {
         clientMemberGroups.setGroupNumber(this.adminClientUserService.countGroupNumber(id));
      }

      return clientMemberGroups;
   }

   @Override
   public Page<ClientMemberGroups> page(GroupPageParam groupPageParam) {
      QueryWrapper<ClientMemberGroups> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isAllNotEmpty(new Object[]{groupPageParam.getSortField(), groupPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(groupPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, groupPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(groupPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(ClientMemberGroups::getPower);
      }

      return (Page<ClientMemberGroups>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<ClientMemberGroups> list() {
      QueryWrapper<ClientMemberGroups> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().orderByAsc(ClientMemberGroups::getPower);
      return this.list(queryWrapper);
   }
}

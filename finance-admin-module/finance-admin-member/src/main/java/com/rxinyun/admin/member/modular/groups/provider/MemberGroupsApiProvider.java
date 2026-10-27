package com.rxinyun.admin.member.modular.groups.provider;

import com.rxinyun.admin.member.modular.groups.entity.ClientMemberGroups;
import com.rxinyun.admin.member.modular.groups.service.GroupsService;
import com.rxinyun.member.member.api.MemberGroupsApi;
import com.rxinyun.member.member.pojo.BaseMemberGroups;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberGroupsApiProvider implements MemberGroupsApi {
   @Resource
   private GroupsService groupsService;

   public List<BaseMemberGroups> list() {
      List<ClientMemberGroups> list = this.groupsService.list();
      return (List<BaseMemberGroups>)(list == null ? new ArrayList<>() : list.stream().map(item -> {
         BaseMemberGroups baseMemberGroups = new BaseMemberGroups();
         baseMemberGroups.setId(item.getId());
         baseMemberGroups.setGroupName(item.getGroupName());
         baseMemberGroups.setGroupDescription(item.getGroupDescription());
         baseMemberGroups.setGroupNumber(item.getGroupNumber());
         baseMemberGroups.setPower(item.getPower());
         return baseMemberGroups;
      }).toList());
   }
}

package com.rxinyun.admin.member.modular.groups.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.groups.entity.ClientMemberGroups;
import com.rxinyun.admin.member.modular.groups.param.ClientGroupIdParam;
import com.rxinyun.admin.member.modular.groups.param.ClientMemberGroupsAddParam;
import com.rxinyun.admin.member.modular.groups.param.ClientMemberGroupsEditParam;
import com.rxinyun.admin.member.modular.groups.param.GroupPageParam;
import com.rxinyun.admin.member.modular.groups.service.GroupsService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员组控制器"
)
@RestController
@RequestMapping({"/admin/groups"})
public class GroupsController {
   @Autowired
   private GroupsService groupsService;

   @Operation(
      summary = "添加会员组"
   )
   @PostMapping({"/add"})
   public CommonResult<String> addGroups(@RequestBody ClientMemberGroupsAddParam clientMemberGroups) {
      this.groupsService.add(clientMemberGroups);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除会员组列表"
   )
   @PostMapping({"/delete"})
   public CommonResult<String> deleteGroups(@RequestBody ClientGroupIdParam clientGroupIdParam) {
      this.groupsService.delete(clientGroupIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改会员组"
   )
   @PostMapping({"/update"})
   public CommonResult<String> updateGroups(@RequestBody ClientMemberGroupsEditParam clientMemberGroups) {
      this.groupsService.update(clientMemberGroups);
      return CommonResult.ok();
   }

   @Operation(
      summary = "查询会员组"
   )
   @GetMapping({"/query"})
   public CommonResult<ClientMemberGroups> queryGroups(@RequestParam String id) {
      return CommonResult.data(this.groupsService.queryById(id));
   }

   @Operation(
      summary = "查询会员组分页列表"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<ClientMemberGroups>> listGroups(GroupPageParam groupPageParam) {
      return CommonResult.data(this.groupsService.page(groupPageParam));
   }
}

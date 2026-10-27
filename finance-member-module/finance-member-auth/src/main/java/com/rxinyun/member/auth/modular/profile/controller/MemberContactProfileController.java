package com.rxinyun.member.auth.modular.profile.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.profile.entity.MemberContactProfile;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileAddParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileEditParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileIdParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfilePageParam;
import com.rxinyun.member.auth.modular.profile.service.MemberContactProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员基本资料控制器"
)
@RestController
@RequestMapping({"/member/contact-profile"})
@Validated
public class MemberContactProfileController {
   @Resource
   private MemberContactProfileService memberContactProfileService;

   @Operation(
      summary = "会员基本资料分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberContactProfile>> page(MemberContactProfilePageParam pageParam) {
      return CommonResult.data(this.memberContactProfileService.page(pageParam));
   }

   @Operation(
      summary = "会员新增基本资料"
   )
   @CommonLog("会员新增基本资料")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid MemberContactProfileAddParam addParam) {
      this.memberContactProfileService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "会员修改基本资料"
   )
   @CommonLog("会员修改基本资料")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid MemberContactProfileEditParam editParam) {
      this.memberContactProfileService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "会员删除基本资料"
   )
   @CommonLog("会员删除基本资料")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid MemberContactProfileIdParam idParam) {
      this.memberContactProfileService.delete(idParam);
      return CommonResult.ok();
   }
}

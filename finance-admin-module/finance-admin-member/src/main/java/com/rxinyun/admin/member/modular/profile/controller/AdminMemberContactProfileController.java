package com.rxinyun.admin.member.modular.profile.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.profile.entity.AdminMemberContactProfile;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfileEditParam;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfileIdParam;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfilePageParam;
import com.rxinyun.admin.member.modular.profile.service.AdminMemberContactProfileService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
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
   name = "后台会员基本资料控制器"
)
@RestController
@RequestMapping({"/admin/member/contact-profile"})
@Validated
public class AdminMemberContactProfileController {
   @Resource
   private AdminMemberContactProfileService adminMemberContactProfileService;

   @Operation(
      summary = "后台会员基本资料分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<AdminMemberContactProfile>> page(AdminMemberContactProfilePageParam pageParam) {
      return CommonResult.data(this.adminMemberContactProfileService.page(pageParam));
   }

   @Operation(
      summary = "后台修改会员基本资料"
   )
   @CommonLog("后台修改会员基本资料")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid AdminMemberContactProfileEditParam editParam) {
      this.adminMemberContactProfileService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "后台删除会员基本资料"
   )
   @CommonLog("后台删除会员基本资料")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid AdminMemberContactProfileIdParam idParam) {
      this.adminMemberContactProfileService.delete(idParam);
      return CommonResult.ok();
   }
}

package com.rxinyun.member.auth.modular.real.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.real.entity.PersonalRealInfo;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoAddOrUpdateParam;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoIdParam;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoPageParam;
import com.rxinyun.member.auth.modular.real.service.MemberPersonalRealInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "个人实名认证控制器"
)
@RestController
@RequestMapping({"/member/real"})
@Validated
public class MemberPersonalRealInfoController {
   @Resource
   private MemberPersonalRealInfoService memberPersonalRealInfoService;

   @Operation(
      summary = "获取个人实名认证分页"
   )
   @GetMapping({"/personal/page"})
   public CommonResult<Page<PersonalRealInfo>> page(PersonalRealInfoPageParam pageParam) {
      return CommonResult.data(this.memberPersonalRealInfoService.page(pageParam));
   }

   @Operation(
      summary = "新增个人实名认证"
   )
   @CommonLog("新增个人实名认证")
   @PostMapping({"/personal/add"})
   public CommonResult add(@RequestBody PersonalRealInfoAddOrUpdateParam serverLocalLineAddParam) {
      return this.memberPersonalRealInfoService.addOrUpdate(serverLocalLineAddParam);
   }

   @Operation(
      summary = "新增或更新个人实名认证"
   )
   @CommonLog("新增或更新个人实名认证")
   @PostMapping({"/personal/addOrUpdate"})
   public CommonResult addOrUpdate(@RequestBody PersonalRealInfoAddOrUpdateParam serverLocalLineAddParam) {
      return this.memberPersonalRealInfoService.addOrUpdate(serverLocalLineAddParam);
   }

   @Operation(
      summary = "获取个人实名认证详情"
   )
   @GetMapping({"/personal/detail"})
   public CommonResult<PersonalRealInfo> detail(@Valid PersonalRealInfoIdParam serverLocalLineIdParam) {
      return CommonResult.data(this.memberPersonalRealInfoService.detail(serverLocalLineIdParam));
   }

   @Operation(
      summary = "删除个人实名认证"
   )
   @CommonLog("删除个人实名认证")
   @PostMapping({"/personal/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<PersonalRealInfoIdParam> serverLocalLineIdParamList) {
      this.memberPersonalRealInfoService.delete(serverLocalLineIdParamList);
      return CommonResult.ok();
   }
}

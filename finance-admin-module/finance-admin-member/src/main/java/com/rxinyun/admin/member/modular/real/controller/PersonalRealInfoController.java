package com.rxinyun.admin.member.modular.real.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.real.entity.PersonalRealInfo;
import com.rxinyun.admin.member.modular.real.param.PersonalRealInfoIdParam;
import com.rxinyun.admin.member.modular.real.param.PersonalRealInfoPageParam;
import com.rxinyun.admin.member.modular.real.service.PersonalRealInfoService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "个人实名认证控制器"
)
@RestController
@RequestMapping({"/admin/real"})
@Validated
public class PersonalRealInfoController {
   @Resource
   private PersonalRealInfoService personalRealInfoService;

   @Operation(
      summary = "获取个人实名认证分页"
   )
   @GetMapping({"/personal/page"})
   public CommonResult<Page<PersonalRealInfo>> page(PersonalRealInfoPageParam pageParam) {
      return CommonResult.data(this.personalRealInfoService.page(pageParam));
   }

   @Operation(
      summary = "获取个人实名认证详情"
   )
   @GetMapping({"/personal/detail"})
   public CommonResult<PersonalRealInfo> detail(@Valid PersonalRealInfoIdParam serverLocalLineIdParam) {
      return CommonResult.data(this.personalRealInfoService.detail(serverLocalLineIdParam));
   }
}

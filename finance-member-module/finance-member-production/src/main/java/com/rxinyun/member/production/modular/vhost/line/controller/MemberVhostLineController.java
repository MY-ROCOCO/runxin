package com.rxinyun.member.production.modular.vhost.line.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.vhost.line.entity.MemberVhostLine;
import com.rxinyun.member.production.modular.vhost.line.param.MemberVhostLinePageParam;
import com.rxinyun.member.production.modular.vhost.line.service.MemberVhostLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-虚拟主机线路"
)
@RestController
@RequestMapping({"/member/vhost/line"})
@Validated
public class MemberVhostLineController {
   @Resource
   private MemberVhostLineService memberVhostLineService;

   @Operation(
      summary = "下单按地域查询虚拟主机线路分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberVhostLine>> page(@Valid MemberVhostLinePageParam pageParam) {
      return CommonResult.data(this.memberVhostLineService.page(pageParam));
   }
}

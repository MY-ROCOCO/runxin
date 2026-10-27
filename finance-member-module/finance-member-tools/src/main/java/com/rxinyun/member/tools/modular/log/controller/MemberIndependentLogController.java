package com.rxinyun.member.tools.modular.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.tools.modular.log.param.MemberIndependentLogPageParam;
import com.rxinyun.member.tools.modular.log.result.MemberServerOperationLogResult;
import com.rxinyun.member.tools.modular.log.service.MemberServerOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员独立服务器日志控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberIndependentLogController {
   @Resource
   private MemberServerOperationLogService memberServerOperationLogService;

   @Operation(
      summary = "获取会员独立服务器日志分页"
   )
   @GetMapping({"/tools/independent/log/page"})
   public CommonResult<Page<MemberServerOperationLogResult>> page(@Valid MemberIndependentLogPageParam memberIndependentLogPageParam) {
      return CommonResult.data(this.memberServerOperationLogService.independentPage(memberIndependentLogPageParam));
   }
}

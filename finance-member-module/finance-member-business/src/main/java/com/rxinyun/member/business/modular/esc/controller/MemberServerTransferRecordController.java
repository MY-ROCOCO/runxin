package com.rxinyun.member.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServerTransferRecord;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerTransferRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端云服务器转让记录管理控制器"
)
@RestController
@RequestMapping({"/member/cloud/server/transfer"})
@Validated
public class MemberServerTransferRecordController {
   @Resource
   private MemberServerTransferRecordService memberServerTransferRecordService;

   @Operation(
      summary = "会员端云服务器转让记录分页"
   )
   @CommonLog("会员端云服务器转让记录分页")
   @GetMapping({"/page"})
   public CommonResult<Page<MemberServerTransferRecord>> page(MemberServerTransferRecordPageParam pageParam) {
      return CommonResult.data(this.memberServerTransferRecordService.page(pageParam));
   }
}

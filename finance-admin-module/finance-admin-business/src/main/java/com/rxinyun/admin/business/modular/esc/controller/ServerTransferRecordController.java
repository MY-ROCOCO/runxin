package com.rxinyun.admin.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerTransferRecord;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordPageParam;
import com.rxinyun.admin.business.modular.esc.service.ServerTransferRecordService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "云服务器转让记录管理控制器"
)
@RestController
@RequestMapping({"/admin/server/transfer"})
@Validated
public class ServerTransferRecordController {
   @Resource
   private ServerTransferRecordService serverTransferRecordService;

   @Operation(
      summary = "查询云服务器转让记录分页"
   )
   @CommonLog("查询云服务器转让记录分页")
   @GetMapping({"/page"})
   public CommonResult<Page<ServerTransferRecord>> page(ServerTransferRecordPageParam pageParam) {
      return CommonResult.data(this.serverTransferRecordService.page(pageParam));
   }
}

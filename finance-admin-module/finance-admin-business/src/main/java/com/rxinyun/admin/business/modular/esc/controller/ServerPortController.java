package com.rxinyun.admin.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import com.rxinyun.admin.business.modular.esc.param.ServerPortAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortPageParam;
import com.rxinyun.admin.business.modular.esc.service.ServerPortService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "云服务器端口管理控制器"
)
@RestController
@RequestMapping({"/admin/cloud/port"})
@Validated
public class ServerPortController {
   @Resource
   private ServerPortService serverPortService;

   @Operation(
      summary = "查询云服务器端口分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<ServerPort>> page(ServerPortPageParam pageParam) {
      return CommonResult.data(this.serverPortService.page(pageParam));
   }

   @Operation(
      summary = "查询云服务器端口详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<ServerPort> detail(@Valid ServerPortIdParam serverPortIdParam) {
      return CommonResult.data(this.serverPortService.detail(serverPortIdParam));
   }

   @Operation(
      summary = "新增云服务器端口"
   )
   @PostMapping({"/add"})
   public CommonResult<ServerPort> add(@RequestBody ServerPortAddParam serverPortAddParam) {
      this.serverPortService.add(serverPortAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除云服务器端口"
   )
   @CommonLog("删除云服务器端口")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody List<ServerPortIdParam> serverPortIdParamList) {
      this.serverPortService.delete(serverPortIdParamList);
      return CommonResult.ok();
   }
}

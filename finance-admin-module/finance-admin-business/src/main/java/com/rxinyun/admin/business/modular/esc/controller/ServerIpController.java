package com.rxinyun.admin.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.param.ServerIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpPageParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpReplaceParam;
import com.rxinyun.admin.business.modular.esc.param.ServerResetIpParam;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
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
   name = "云服务器IP管理控制器"
)
@RestController
@RequestMapping({"/admin/server/ip"})
@Validated
public class ServerIpController {
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ServerService serverService;

   @Operation(
      summary = "查询云服务器IP分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<ServerIp>> page(ServerIpPageParam pageParam) {
      return CommonResult.data(this.serverIpService.page(pageParam));
   }

   @Operation(
      summary = "新增云服务器IP"
   )
   @PostMapping({"/add"})
   public CommonResult<ServerIp> add(ServerIpAddParam serverIpAddParam) {
      this.serverIpService.add(serverIpAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "批量新增云服务器IP"
   )
   @CommonLog("批量新增云服务器IP")
   @PostMapping({"/batch"})
   public CommonResult addBatch(@RequestBody ServerIpParam serverIdParam) {
      return this.serverIpService.addBatch(serverIdParam);
   }

   @Operation(
      summary = "查询云服务器IP详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<ServerIp> detail(@Valid ServerIpIdParam serverIpIdParam) {
      return CommonResult.data(this.serverIpService.detail(serverIpIdParam));
   }

   @Operation(
      summary = "设置云服务器主IP"
   )
   @CommonLog("设置云服务器主IP")
   @PostMapping({"/setMainIp"})
   public CommonResult setMainIp(@RequestBody ServerIpIdParam serverIpIdParam) {
      this.serverIpService.setMainIp(serverIpIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "重置云服务器IP"
   )
   @CommonLog("重置云服务器IP")
   @PostMapping({"/resetIp"})
   public CommonResult resetIp(@RequestBody ServerResetIpParam serverResetIpParam) {
      ServerIdParam serverIdParam = new ServerIdParam();
      serverIdParam.setId(serverResetIpParam.getId());
      return this.serverService.resetIpServer(serverIdParam);
   }

   @Operation(
      summary = "更换云服务器IP"
   )
   @CommonLog("更换云服务器IP")
   @PostMapping({"/replaceIp"})
   public CommonResult replaceIp(@RequestBody ServerIpReplaceParam serverIpReplaceParam) {
      return this.serverIpService.replacePublicIp(serverIpReplaceParam);
   }

   @Operation(
      summary = "编辑云服务器IP"
   )
   @CommonLog("编辑云服务器IP")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody ServerIpEditParam serverIpEditParam) {
      this.serverIpService.edit(serverIpEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除云服务器IP"
   )
   @CommonLog("删除云服务器IP")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody List<ServerIpIdParam> serverIpIdParamList) {
      this.serverIpService.delete(serverIpIdParamList);
      return CommonResult.ok();
   }
}

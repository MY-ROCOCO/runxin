package com.rxinyun.admin.production.modular.esc.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMaster;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterAddParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterEditParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterIdParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterLimitedEditParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterPageParam;
import com.rxinyun.admin.production.modular.esc.master.param.ServerMasterStatusParam;
import com.rxinyun.admin.production.modular.esc.master.service.ServerMasterService;
import com.rxinyun.admin.production.modular.esc.master.vo.MasterStatusVO;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import com.rxinyun.common.qzsystem.entity.SystemInfo;
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
   name = "服务器宿主机管理控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class ServerMasterController {
   @Resource
   private ServerMasterService serverMasterService;

   @Operation(
      summary = "获取宿主机分页"
   )
   @GetMapping({"/master/page"})
   public CommonResult<Page<ServerMaster>> page(ServerMasterPageParam pageParam) {
      return CommonResult.data(this.serverMasterService.page(pageParam));
   }

   @Operation(
      summary = "新增宿主机"
   )
   @PostMapping({"/master/add"})
   public CommonResult<ServerMaster> add(@RequestBody ServerMasterAddParam serverMasterAddParam) {
      this.serverMasterService.add(serverMasterAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "新增或更新被控参数"
   )
   @PostMapping({"/master/limited"})
   public CommonResult<ServerMaster> Limited(@RequestBody ServerMasterLimitedEditParam serverMasterLimitedEditParam) {
      this.serverMasterService.edit(serverMasterLimitedEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取宿主机详情"
   )
   @GetMapping({"/master/detail"})
   public CommonResult<ServerMaster> detail(@Valid ServerMasterIdParam serverMasterIdParam) {
      return CommonResult.data(this.serverMasterService.detail(serverMasterIdParam));
   }

   @Operation(
      summary = "编辑宿主机"
   )
   @CommonLog("编辑宿主机")
   @PostMapping({"/master/edit"})
   public CommonResult<String> edit(@RequestBody ServerMasterEditParam serverMasterEditParam) {
      this.serverMasterService.edit(serverMasterEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除宿主机"
   )
   @CommonLog("删除宿主机")
   @PostMapping({"/master/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ServerMasterIdParam> serverMasterIdParamList) {
      this.serverMasterService.delete(serverMasterIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "检测宿主机状态"
   )
   @CommonLog("检测宿主机状态")
   @PostMapping({"/master/status"})
   public CommonResult<MasterStatusVO> selectByStatus(@RequestBody ServerMasterStatusParam serverMasterStatusParam) {
      return this.serverMasterService.selectByStatus(serverMasterStatusParam);
   }

   @Operation(
      summary = "宿主机性能监控"
   )
   @CommonLog("宿主机性能监控")
   @PostMapping({"/master/companyinfo"})
   public CommonResult<SystemInfo> selectByCompanyInfo(@RequestBody ServerMasterStatusParam serverMasterStatusParam) {
      return this.serverMasterService.selectByCompanyInfo(serverMasterStatusParam);
   }

   @Operation(
      summary = "统计当前宿主机下面云服务器状态"
   )
   @CommonLog("统计当前宿主机下面云服务器状态")
   @PostMapping({"/master/monitoring"})
   public CommonResult<List<MasterMonitoringItem>> selectByNetworkMonitoring(@RequestBody ServerMasterStatusParam serverMasterStatusParam) {
      return this.serverMasterService.selectByNetworkMonitoring(serverMasterStatusParam);
   }
}

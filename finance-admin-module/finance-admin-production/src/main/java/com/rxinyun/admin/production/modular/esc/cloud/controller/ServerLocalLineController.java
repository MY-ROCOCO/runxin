package com.rxinyun.admin.production.modular.esc.cloud.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineAddParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineEditParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineEditSortParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineIdParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLinePageParam;
import com.rxinyun.admin.production.modular.esc.cloud.param.ServerLocalLineUpdateDiscountTemplateParam;
import com.rxinyun.admin.production.modular.esc.cloud.response.ServerLocalLineResponse;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLocalLineService;
import com.rxinyun.admin.production.modular.esc.cloud.vo.ServerLocalLineVO;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
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
   name = "服务器线路管理控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class ServerLocalLineController {
   @Resource
   private ServerLocalLineService serverLocalLineService;

   @Operation(
      summary = "获取线路分页"
   )
   @GetMapping({"/line/page"})
   public CommonResult<Page<ServerLocalLineResponse>> page(ServerLocalLinePageParam pageParam) {
      return CommonResult.data(this.serverLocalLineService.page(pageParam));
   }

   @Operation(
      summary = "新增线路"
   )
   @PostMapping({"/line/add"})
   public CommonResult<ServerLocalLine> add(@RequestBody ServerLocalLineAddParam serverLocalLineAddParam) {
      this.serverLocalLineService.add(serverLocalLineAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取线路详情"
   )
   @GetMapping({"/line/detail"})
   public CommonResult<ServerLocalLineVO> detail(@Valid ServerLocalLineIdParam serverLocalLineIdParam) {
      return CommonResult.data(this.serverLocalLineService.detail(serverLocalLineIdParam));
   }

   @Operation(
      summary = "编辑线路"
   )
   @CommonLog("编辑线路")
   @PostMapping({"/line/edit"})
   public CommonResult<String> edit(@RequestBody ServerLocalLineEditParam serverLocalLineEditParam) {
      this.serverLocalLineService.edit(serverLocalLineEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除线路"
   )
   @CommonLog("删除线路")
   @PostMapping({"/line/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ServerLocalLineIdParam> serverLocalLineIdParamList) {
      this.serverLocalLineService.delete(serverLocalLineIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取云服务器线路Map集合"
   )
   @CommonLog("获取云服务器线路Map集合")
   @GetMapping({"/line/lineList"})
   public CommonResult lineList() {
      return CommonResult.data(this.serverLocalLineService.lineList());
   }

   @Operation(
      summary = "修改排序"
   )
   @CommonLog("修改排序")
   @PostMapping({"/line/updateSort"})
   public CommonResult<String> updateSort(@RequestBody ServerLocalLineEditSortParam serverLocalLineEditSortParam) {
      this.serverLocalLineService.updateSort(serverLocalLineEditSortParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "线路修改折扣模板"
   )
   @CommonLog("线路修改折扣模板")
   @PostMapping({"/line/updateTemplate"})
   public CommonResult<String> updateDiscountTemplate(@RequestBody ServerLocalLineUpdateDiscountTemplateParam serverLocalLineUpdateTemplateParam) {
      this.serverLocalLineService.updateDiscountTemplate(serverLocalLineUpdateTemplateParam);
      return CommonResult.ok();
   }
}

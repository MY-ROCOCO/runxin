package com.rxinyun.admin.production.modular.vhost.line.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineAddParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineEditParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineIdParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLinePageParam;
import com.rxinyun.admin.production.modular.vhost.line.service.VhostLineService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "虚拟主机线路管理"
)
@RestController
@RequestMapping({"/admin/vhost/line"})
@Validated
public class VhostLineController {
   @Resource
   private VhostLineService vhostLineService;

   @Operation(
      summary = "虚拟主机线路分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<VhostLine>> page(VhostLinePageParam pageParam) {
      return CommonResult.data(this.vhostLineService.page(pageParam));
   }

   @Operation(
      summary = "新增虚拟主机线路"
   )
   @CommonLog("新增虚拟主机线路")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid VhostLineAddParam addParam) {
      this.vhostLineService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑虚拟主机线路"
   )
   @CommonLog("编辑虚拟主机线路")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid VhostLineEditParam editParam) {
      this.vhostLineService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "虚拟主机线路详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<VhostLine> detail(@Valid VhostLineIdParam idParam) {
      return CommonResult.data(this.vhostLineService.detail(idParam));
   }

   @Operation(
      summary = "删除虚拟主机线路"
   )
   @CommonLog("删除虚拟主机线路")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<VhostLineIdParam> idParamList) {
      this.vhostLineService.delete(idParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "虚拟主机线路下拉"
   )
   @GetMapping({"/selector"})
   public CommonResult<List<VhostLine>> selector(@RequestParam(required = false) String regionId) {
      return CommonResult.data(this.vhostLineService.selector(regionId));
   }
}

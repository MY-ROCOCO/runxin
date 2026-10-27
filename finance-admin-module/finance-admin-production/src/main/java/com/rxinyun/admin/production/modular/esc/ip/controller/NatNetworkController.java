package com.rxinyun.admin.production.modular.esc.ip.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.NatNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.NatNetworkPageParam;
import com.rxinyun.admin.production.modular.esc.ip.service.NatNetworkService;
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
   name = "NatIP管理控制器"
)
@RestController
@RequestMapping({"/admin/nat"})
@Validated
public class NatNetworkController {
   @Resource
   private NatNetworkService natNetworkService;

   @Operation(
      summary = "获取NatIP分页"
   )
   @GetMapping({"/ip/page"})
   public CommonResult<Page<NatNetwork>> page(NatNetworkPageParam pageParam) {
      return CommonResult.data(this.natNetworkService.page(pageParam));
   }

   @Operation(
      summary = "新增NatIP"
   )
   @PostMapping({"/ip/add"})
   public CommonResult<String> add(@RequestBody NatNetworkAddParam networkAddParam) {
      return this.natNetworkService.add(networkAddParam);
   }

   @Operation(
      summary = "获取NatIP"
   )
   @GetMapping({"/ip/detail"})
   public CommonResult<NatNetwork> detail(@Valid NatNetworkIdParam natNetworkIdParam) {
      return CommonResult.data(this.natNetworkService.detail(natNetworkIdParam));
   }

   @Operation(
      summary = "编辑NatIP"
   )
   @CommonLog("编辑NatIP")
   @PostMapping({"/ip/edit"})
   public CommonResult<String> edit(@RequestBody NatNetworkEditParam natNetworkEditParam) {
      this.natNetworkService.edit(natNetworkEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除NatIP"
   )
   @CommonLog("删除NatIP")
   @PostMapping({"/ip/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<NatNetworkIdParam> natNetworkIdParamList) {
      this.natNetworkService.delete(natNetworkIdParamList);
      return CommonResult.ok();
   }
}

package com.rxinyun.admin.notice.modular.controller;

import com.rxinyun.admin.notice.modular.domain.dto.TabEmailSmsParam;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateTabDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateTab;
import com.rxinyun.admin.notice.modular.service.TabService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/admin/notice/tab"})
@Tag(
   name = "标签模版管理",
   description = "标签模版接口"
)
public class TabController {
   @Resource
   TabService tabService;

   @Operation(
      summary = "添加标签"
   )
   @CommonLog("添加标签")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid TemplateTabDTO a) {
      this.tabService.add(a);
      return CommonResult.ok();
   }

   @Operation(
      summary = "edit标签"
   )
   @CommonLog("edit标签")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid TemplateTabDTO a) {
      this.tabService.edit(a);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取标签详情"
   )
   @CommonLog("获取标签详情")
   @GetMapping({"/getById/{id}"})
   public CommonResult<TemplateTab> getById(@PathVariable String id) {
      return CommonResult.data(this.tabService.getById(id));
   }

   @Operation(
      summary = "保存标签与模板间关系"
   )
   @CommonLog("保存标签与模板间关系")
   @PostMapping({"/save"})
   public CommonResult<String> save(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<TabEmailSmsParam> list) {
      this.tabService.save(list);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取标签集合"
   )
   @CommonLog("获取标签集合")
   @GetMapping({"/tableList"})
   public CommonResult<List<TemplateTab>> tabList() {
      return CommonResult.data(this.tabService.tabList());
   }
}

package com.rxinyun.admin.notice.modular.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.notice.modular.domain.dto.TemplateDTO;
import com.rxinyun.admin.notice.modular.domain.dto.TemplatePageParam;
import com.rxinyun.admin.notice.modular.domain.entity.Template;
import com.rxinyun.admin.notice.modular.domain.vo.TemplateVO;
import com.rxinyun.admin.notice.modular.service.TemplateService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/admin/notice/template"})
@Tag(
   name = "通知模版管理",
   description = "通知模版接口"
)
public class TemplateController {
   @Resource
   TemplateService templateService;

   @Operation(
      summary = "添加通知模版"
   )
   @CommonLog("添加通知模版")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid TemplateDTO templateDTO) {
      this.templateService.add(templateDTO);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取通知模版分页"
   )
   @CommonLog("获取通知模版分页")
   @GetMapping({"/page"})
   public CommonResult<Page<TemplateVO>> page(TemplatePageParam pageParam) {
      return CommonResult.data(this.templateService.page(pageParam));
   }

   @Operation(
      summary = "编辑通知模版"
   )
   @CommonLog("编辑通知模版")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid TemplateDTO templateDTO) {
      this.templateService.edit(templateDTO);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取通知模版详情"
   )
   @CommonLog("获取通知模版详情")
   @GetMapping({"/getById/{id}"})
   public CommonResult<Template> getById(@PathVariable String id) {
      return CommonResult.data(this.templateService.getById(id));
   }
}

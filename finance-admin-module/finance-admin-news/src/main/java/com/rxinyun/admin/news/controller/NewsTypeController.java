package com.rxinyun.admin.news.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.news.model.dto.NewsTypeDto;
import com.rxinyun.admin.news.model.dto.NewsTypePageParam;
import com.rxinyun.admin.news.model.po.NewsType;
import com.rxinyun.admin.news.service.NewsTypeService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "新闻类型管理控制器"
)
@RequestMapping({"/admin"})
@RestController
@Validated
public class NewsTypeController {
   @Resource
   NewsTypeService newsTypeService;

   @Operation(
      summary = "获取新闻类型分页列表"
   )
   @CommonLog("获取新闻类型分页列表")
   @GetMapping({"/news/type/page"})
   public CommonResult<Page<NewsType>> page(NewsTypePageParam newsTypePageParam) {
      return CommonResult.data(this.newsTypeService.list(newsTypePageParam));
   }

   @Operation(
      summary = "添加新闻类型"
   )
   @CommonLog("添加新闻类型")
   @PostMapping({"/newsType/add"})
   public CommonResult<String> add(@RequestBody @Valid NewsTypeDto newsTypeDto) {
      this.newsTypeService.add(newsTypeDto);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据id获取新闻类型详情"
   )
   @CommonLog("根据id获取新闻类型详情")
   @GetMapping({"/news/searchNewsTypeById"})
   public CommonResult<NewsType> searchNewsById(@Parameter(description = "新闻类型ID") @NotBlank @RequestParam String id) {
      return CommonResult.data(this.newsTypeService.searchNewsTypeById(id));
   }

   @Operation(
      summary = "编辑新闻类型"
   )
   @CommonLog("编辑新闻类型")
   @PutMapping({"/newsType/edit"})
   public CommonResult<String> edit(@RequestBody @Valid NewsTypeDto newsTypeDto) {
      this.newsTypeService.edit(newsTypeDto);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据id删除新闻类型"
   )
   @CommonLog("删除新闻")
   @DeleteMapping({"/newsType/delete/{id}"})
   public CommonResult<String> delete(@Parameter(description = "新闻类型ID") @NotBlank @PathVariable String id) {
      this.newsTypeService.delete(id);
      return CommonResult.ok();
   }
}

package com.rxinyun.admin.news.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.news.model.dto.NewsDto;
import com.rxinyun.admin.news.model.dto.NewsPageParam;
import com.rxinyun.admin.news.model.po.News;
import com.rxinyun.admin.news.model.vo.NewsVo;
import com.rxinyun.admin.news.service.impl.NewsServiceImpl;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "新闻管理控制器"
)
@RequestMapping({"/admin"})
@RestController
@Validated
public class NewsController {
   @Resource
   private NewsServiceImpl newsService;

   @Operation(
      summary = "获取新闻分页"
   )
   @CommonLog("获取新闻分页")
   @GetMapping({"/news/page"})
   public CommonResult<Page<NewsVo>> page(NewsPageParam pageParam) {
      return CommonResult.data(this.newsService.page(pageParam));
   }

   @Operation(
      summary = "添加新闻"
   )
   @CommonLog("添加新闻")
   @PostMapping({"/news/add"})
   public CommonResult<String> add(@RequestBody @Valid NewsDto newsDto) {
      this.newsService.add(newsDto);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据id获取新闻详情"
   )
   @CommonLog("根据id获取新闻详情")
   @GetMapping({"/news/searchNewsById"})
   public CommonResult<News> searchNewsById(@Parameter(description = "新闻ID") @NotBlank @RequestParam String id) {
      return CommonResult.data(this.newsService.searchNewsById(id));
   }

   @Operation(
      summary = "编辑新闻"
   )
   @CommonLog("编辑新闻")
   @PostMapping({"/news/edit"})
   public CommonResult<String> edit(@RequestBody @Valid NewsDto newsDto) {
      this.newsService.edit(newsDto);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据id删除新闻"
   )
   @CommonLog("删除新闻")
   @DeleteMapping({"/news/delete/{id}"})
   public CommonResult<String> delete(@Parameter(description = "新闻ID") @NotBlank @PathVariable("id") String id) {
      this.newsService.delete(id);
      return CommonResult.ok();
   }
}

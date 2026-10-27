package com.rxinyun.member.news.modular.news.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.news.modular.news.model.dto.NewsPageParam;
import com.rxinyun.member.news.modular.news.model.po.News;
import com.rxinyun.member.news.modular.news.model.vo.NewsVo;
import com.rxinyun.member.news.modular.news.service.impl.MemberNewsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "member新闻管理控制器"
)
@RequestMapping({"/member"})
@RestController
@Validated
public class MemberNewsController {
   @Resource
   private MemberNewsServiceImpl memberNewsService;

   @Operation(
      summary = "获取新闻分页"
   )
   @CommonLog("获取新闻分页")
   @GetMapping({"/news/noticePage"})
   public CommonResult<Page<NewsVo>> noticePage(NewsPageParam pageParam) {
      return CommonResult.data(this.memberNewsService.noticePage(pageParam));
   }

   @Operation(
      summary = "获取新闻前三类别及各自前五新闻"
   )
   @CommonLog("获取新闻前三类别及各自前五新闻")
   @GetMapping({"news/homeSort"})
   public CommonResult<List<List<NewsVo>>> sortNews() {
      return CommonResult.data(this.memberNewsService.homeSort());
   }

   @Operation(
      summary = "根据新闻id获取新闻详情"
   )
   @CommonLog("根据新闻id获取新闻详情")
   @GetMapping({"news/getNewsById/{id}"})
   public CommonResult<News> getNewsById(@NotBlank @PathVariable String id) {
      if (Objects.equals(id, "")) {
         CommonResult.error("新闻ID为空", id);
      }

      return CommonResult.data((News)this.memberNewsService.getById(id));
   }

   @Operation(
      summary = "根据新闻类型id获取新闻数据分页"
   )
   @CommonLog("根据新闻类型id获取新闻数据分页")
   @GetMapping({"/news/page"})
   public CommonResult<Page<News>> pageByType(NewsPageParam pageParam) {
      return CommonResult.data(this.memberNewsService.page(pageParam));
   }
}

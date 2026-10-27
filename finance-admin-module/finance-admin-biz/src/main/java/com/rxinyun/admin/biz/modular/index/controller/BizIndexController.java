package com.rxinyun.admin.biz.modular.index.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.rxinyun.admin.biz.modular.index.param.BizIndexNoticeIdParam;
import com.rxinyun.admin.biz.modular.index.param.BizIndexNoticeListParam;
import com.rxinyun.admin.biz.modular.index.param.BizIndexSlideshowListParam;
import com.rxinyun.admin.biz.modular.index.result.BizIndexNoticeListResult;
import com.rxinyun.admin.biz.modular.index.result.BizIndexSlideshowDetailResult;
import com.rxinyun.admin.biz.modular.index.result.BizIndexSlideshowListResult;
import com.rxinyun.admin.biz.modular.index.service.BizIndexService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "业务首页控制器"
)
@ApiSupport(
   author = "RUNXIN_TEAM",
   order = 0
)
@RestController
@Validated
public class BizIndexController {
   @Resource
   private BizIndexService bizIndexService;

   @ApiOperationSupport(
      order = 1
   )
   @Operation(
      summary = "获取轮播图列表"
   )
   @GetMapping({"/admin/biz/index/slideshow/list"})
   public CommonResult<List<BizIndexSlideshowListResult>> slideshowListByPlace(@Valid BizIndexSlideshowListParam bizIndexSlideshowListParam) {
      return CommonResult.data(this.bizIndexService.slideshowListByPlace(bizIndexSlideshowListParam));
   }

   @ApiOperationSupport(
      order = 2
   )
   @Operation(
      summary = "获取通知公告列表"
   )
   @GetMapping({"/admin/biz/index/notice/list"})
   public CommonResult<List<BizIndexNoticeListResult>> noticeListByLimit(BizIndexNoticeListParam bizIndexNoticeListParam) {
      return CommonResult.data(this.bizIndexService.noticeListByLimit(bizIndexNoticeListParam));
   }

   @ApiOperationSupport(
      order = 3
   )
   @Operation(
      summary = "获取通知公告详情"
   )
   @GetMapping({"/admin/biz/index/notice/detail"})
   public CommonResult<BizIndexSlideshowDetailResult> noticeDetailById(@Valid BizIndexNoticeIdParam bizIndexNoticeIdParam) {
      return CommonResult.data(this.bizIndexService.noticeDetailById(bizIndexNoticeIdParam));
   }
}

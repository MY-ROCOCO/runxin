package com.rxinyun.admin.biz.modular.notice.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.biz.modular.notice.entity.BizNotice;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticeAddParam;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticeEditParam;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticeIdParam;
import com.rxinyun.admin.biz.modular.notice.param.BizNoticePageParam;
import com.rxinyun.admin.biz.modular.notice.service.BizNoticeService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "通知公告控制器"
)
@RestController
@Validated
public class BizNoticeController {
   @Resource
   private BizNoticeService bizNoticeService;

   @Operation(
      summary = "获取通知公告分页"
   )
   @SaCheckPermission({"/admin/biz/notice/page"})
   @GetMapping({"/admin/biz/notice/page"})
   public CommonResult<Page<BizNotice>> page(BizNoticePageParam bizNoticePageParam) {
      return CommonResult.data(this.bizNoticeService.page(bizNoticePageParam));
   }

   @Operation(
      summary = "添加通知公告"
   )
   @CommonLog("添加通知公告")
   @SaCheckPermission({"/admin/biz/notice/add"})
   @PostMapping({"/admin/biz/notice/add"})
   public CommonResult<String> add(@RequestBody @Valid BizNoticeAddParam bizNoticeAddParam) {
      this.bizNoticeService.add(bizNoticeAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑通知公告"
   )
   @CommonLog("编辑通知公告")
   @SaCheckPermission({"/admin/biz/notice/edit"})
   @PostMapping({"/admin/biz/notice/edit"})
   public CommonResult<String> edit(@RequestBody @Valid BizNoticeEditParam bizNoticeEditParam) {
      this.bizNoticeService.edit(bizNoticeEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除通知公告"
   )
   @CommonLog("删除通知公告")
   @SaCheckPermission({"/admin/biz/notice/delete"})
   @PostMapping({"/admin/biz/notice/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<BizNoticeIdParam> bizNoticeIdParamList) {
      this.bizNoticeService.delete(bizNoticeIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取通知公告详情"
   )
   @SaCheckPermission({"/admin/biz/notice/detail"})
   @GetMapping({"/admin/biz/notice/detail"})
   public CommonResult<BizNotice> detail(@Valid BizNoticeIdParam bizNoticeIdParam) {
      return CommonResult.data(this.bizNoticeService.detail(bizNoticeIdParam));
   }

   @Operation(
      summary = "禁用通知公告"
   )
   @CommonLog("禁用通知公告")
   @SaCheckPermission({"/admin/biz/notice/disableStatus"})
   @PostMapping({"/admin/biz/notice/disableStatus"})
   public CommonResult<String> disableStatus(@RequestBody @Valid BizNoticeIdParam bizNoticeIdParam) {
      this.bizNoticeService.disableStatus(bizNoticeIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "启用通知公告"
   )
   @CommonLog("启用通知公告")
   @SaCheckPermission({"/admin/biz/notice/enableStatus"})
   @PostMapping({"/admin/biz/notice/enableStatus"})
   public CommonResult<String> enableStatus(@RequestBody @Valid BizNoticeIdParam bizNoticeIdParam) {
      this.bizNoticeService.enableStatus(bizNoticeIdParam);
      return CommonResult.ok();
   }
}

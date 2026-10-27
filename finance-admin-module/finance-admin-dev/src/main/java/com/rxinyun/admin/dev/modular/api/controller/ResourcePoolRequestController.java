package com.rxinyun.admin.dev.modular.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.handler.ResourcePoolRequestHandler;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolRequestIdParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolRequestPageParam;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolRequestService;
import com.rxinyun.common.enums.ResourcePoolRequestStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "资源池发单请求"
)
@RestController
@Validated
public class ResourcePoolRequestController {
   @Resource
   private ResourcePoolRequestService resourcePoolRequestService;
   @Resource
   private ResourcePoolRequestHandler resourcePoolRequestHandler;

   @Operation(
      summary = "资源池请求列表分页"
   )
   @GetMapping({"/admin/dev/api/resourcePool/request/page"})
   public CommonResult<Page<ResourcePoolRequest>> page(ResourcePoolRequestPageParam param) {
      return CommonResult.data(this.resourcePoolRequestService.page(param));
   }

   @Operation(
      summary = "资源池请求详情"
   )
   @GetMapping({"/admin/dev/api/resourcePool/request/detail"})
   public CommonResult<ResourcePoolRequest> detail(@Valid ResourcePoolRequestIdParam param) {
      ResourcePoolRequest entity = this.resourcePoolRequestService.getById(param.getId());
      if (entity == null) {
         throw new CommonException("资源池请求不存在", new Object[0]);
      } else {
         return CommonResult.data(entity);
      }
   }

   @Operation(
      summary = "重试资源池请求"
   )
   @PostMapping({"/admin/dev/api/resourcePool/request/retry"})
   public CommonResult<String> retry(@Valid ResourcePoolRequestIdParam param) {
      ResourcePoolRequest entity = this.resourcePoolRequestService.getById(param.getId());
      if (entity == null) {
         throw new CommonException("资源池请求不存在", new Object[0]);
      } else if (!ResourcePoolRequestStatusEnum.FAILED.getCode().equals(entity.getRequestStatus())) {
         throw new CommonException("仅失败状态的请求允许重试", new Object[0]);
      } else {
         try {
            this.resourcePoolRequestHandler.sendRequest(entity);
            return CommonResult.ok("重试提交成功");
         } catch (Exception var4) {
            throw new CommonException("重试失败：" + var4.getMessage(), new Object[0]);
         }
      }
   }
}

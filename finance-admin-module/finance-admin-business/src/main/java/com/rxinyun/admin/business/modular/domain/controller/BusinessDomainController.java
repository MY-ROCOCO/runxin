package com.rxinyun.admin.business.modular.domain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.admin.business.modular.domain.param.DomainPageParam;
import com.rxinyun.admin.business.modular.domain.service.BusinessDomainService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.production.api.DomainCreateStatusApi;
import com.rxinyun.production.api.DomainManagementPwdApi;
import com.rxinyun.production.api.dto.DomainCreateStatusVO;
import com.rxinyun.production.api.dto.DomainManagementPwdVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "业务域名管理"
)
@RestController
@RequestMapping({"/admin/domain"})
@Validated
public class BusinessDomainController {
   @Resource
   private BusinessDomainService businessDomainService;
   @Resource
   private DomainCreateStatusApi domainCreateStatusApi;
   @Resource
   private DomainManagementPwdApi domainManagementPwdApi;

   @Operation(
      summary = "域名分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<BusinessDomain>> page(DomainPageParam pageParam) {
      return CommonResult.data(this.businessDomainService.page(pageParam));
   }

   @Operation(
      summary = "查询域名注册结果状态（调新网 createStatus，用于联调/点击查询当前域名注册状态）"
   )
   @GetMapping({"/createStatus"})
   public CommonResult<DomainCreateStatusVO> createStatus(@Parameter(description = "域名全称",required = true) @RequestParam String domainName) {
      DomainCreateStatusVO vo = this.domainCreateStatusApi.getCreateStatus(domainName);
      if (vo.getStatus() != null) {
         this.businessDomainService.updateRegistrationStatus(domainName, vo.getStatus());
      }

      return CommonResult.data(vo);
   }

   @Operation(
      summary = "获取域名管理密码（调新网 queryManagementPwd，用于查看域名管理密码）"
   )
   @GetMapping({"/managementPwd"})
   public CommonResult<DomainManagementPwdVO> managementPwd(@Parameter(description = "域名全称",required = true) @RequestParam String domainName) {
      DomainManagementPwdVO vo = this.domainManagementPwdApi.getManagementPwd(domainName);
      return CommonResult.data(vo);
   }
}

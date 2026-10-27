package com.rxinyun.admin.business.modular.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.domain.entity.DomainRegistrationRequest;
import com.rxinyun.admin.business.modular.domain.mapper.AdminDomainRegistrationRequestMapper;
import com.rxinyun.admin.business.modular.domain.param.DomainRegistrationRequestQueryParam;
import com.rxinyun.admin.business.modular.domain.service.DomainRegistrationRequestService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("adminDomainRegistrationRequestService")
public class DomainRegistrationRequestServiceImpl implements DomainRegistrationRequestService {
   @Resource
   private AdminDomainRegistrationRequestMapper adminDomainRegistrationRequestMapper;

   @Override
   public Page<DomainRegistrationRequest> page(DomainRegistrationRequestQueryParam param) {
      LambdaQueryWrapper<DomainRegistrationRequest> qw = (LambdaQueryWrapper<DomainRegistrationRequest>)((LambdaQueryWrapper<DomainRegistrationRequest>)((LambdaQueryWrapper<DomainRegistrationRequest>)((LambdaQueryWrapper<DomainRegistrationRequest>)new LambdaQueryWrapper<DomainRegistrationRequest>()
                  .eq(StrUtil.isNotBlank(param.getOrderNo()), DomainRegistrationRequest::getOrderNo, param.getOrderNo()))
               .like(StrUtil.isNotBlank(param.getDomainName()), DomainRegistrationRequest::getDomainName, param.getDomainName()))
            .eq(param.getStatus() != null, DomainRegistrationRequest::getStatus, param.getStatus()))
         .orderByDesc(DomainRegistrationRequest::getCreateTime);
      int current = param.getCurrent() != null && param.getCurrent() > 0 ? param.getCurrent() : 1;
      int size = param.getSize() != null && param.getSize() > 0 ? Math.min(param.getSize(), 100) : 20;
      Page<DomainRegistrationRequest> page = new Page(current, size);
      return (Page<DomainRegistrationRequest>)this.adminDomainRegistrationRequestMapper.selectPage(page, qw);
   }
}

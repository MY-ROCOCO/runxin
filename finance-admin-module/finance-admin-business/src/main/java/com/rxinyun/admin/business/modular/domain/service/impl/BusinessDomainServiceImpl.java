package com.rxinyun.admin.business.modular.domain.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.admin.business.modular.domain.mapper.BusinessDomainMapper;
import com.rxinyun.admin.business.modular.domain.param.DomainPageParam;
import com.rxinyun.admin.business.modular.domain.service.BusinessDomainService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.production.api.DomainTemplateQueryApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BusinessDomainServiceImpl extends ServiceImpl<BusinessDomainMapper, BusinessDomain> implements BusinessDomainService {
   @Resource
   private DomainTemplateQueryApi domainTemplateQueryApi;

   @Override
   public Page<BusinessDomain> page(DomainPageParam pageParam) {
      QueryWrapper<BusinessDomain> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         String key = "%" + pageParam.getSearchKey() + "%";
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<BusinessDomain>)((LambdaQueryWrapper<BusinessDomain>)((LambdaQueryWrapper<BusinessDomain>)((LambdaQueryWrapper<BusinessDomain>)q.like(BusinessDomain::getDomainName, key)).or())
                        .like(BusinessDomain::getTld, key))
                     .or())
                  .like(BusinessDomain::getMemberId, key)
            );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberId())) {
         queryWrapper.lambda().eq(BusinessDomain::getMemberId, pageParam.getMemberId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getDomainName())) {
         queryWrapper.lambda().like(BusinessDomain::getDomainName, pageParam.getDomainName());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(BusinessDomain::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<BusinessDomain> result = (Page<BusinessDomain>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillTemplateNames(result.getRecords());
      return result;
   }

   private void fillTemplateNames(List<BusinessDomain> records) {
      if (records != null && !records.isEmpty()) {
         List<String> templateIds = records.stream()
            .map(BusinessDomain::getTemplateId)
            .filter(id -> id != null && !id.isBlank())
            .distinct()
            .collect(Collectors.toList());
         if (!templateIds.isEmpty()) {
            Map<String, String> nameMap = this.domainTemplateQueryApi.getTemplateNamesByIds(templateIds);

            for (BusinessDomain d : records) {
               if (d.getTemplateId() != null) {
                  d.setTemplateName(nameMap.get(d.getTemplateId()));
               }
            }
         }
      }
   }

   @Override
   public void updateRegistrationStatus(String domainName, String registrationStatus) {
      if (domainName != null) {
         LambdaUpdateWrapper<BusinessDomain> wrapper = (LambdaUpdateWrapper<BusinessDomain>)((LambdaUpdateWrapper<BusinessDomain>)new LambdaUpdateWrapper<BusinessDomain>()
               .eq(BusinessDomain::getDomainName, domainName))
            .set(BusinessDomain::getRegistrationStatus, registrationStatus);
         this.update(wrapper);
      }
   }
}

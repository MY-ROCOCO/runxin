package com.rxinyun.member.business.modular.domain.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.member.business.modular.domain.mapper.MemberBusinessDomainMapper;
import com.rxinyun.member.business.modular.domain.param.MemberDomainPageParam;
import com.rxinyun.member.business.modular.domain.service.MemberDomainService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.production.api.DomainTemplateQueryApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberDomainServiceImpl extends ServiceImpl<MemberBusinessDomainMapper, BusinessDomain> implements MemberDomainService {
   @Resource
   private DomainTemplateQueryApi domainTemplateQueryApi;

   @Override
   public Page<BusinessDomain> page(MemberDomainPageParam pageParam) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (loginUser != null && loginUser.getId() != null) {
         String memberId = String.valueOf(loginUser.getId());
         QueryWrapper<BusinessDomain> queryWrapper = new QueryWrapper().checkSqlInjection();
         queryWrapper.lambda().eq(BusinessDomain::getMemberId, memberId);
         if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
            String key = "%" + pageParam.getSearchKey() + "%";
            queryWrapper.lambda()
               .and(q -> ((LambdaQueryWrapper<BusinessDomain>)((LambdaQueryWrapper<BusinessDomain>)q.like(BusinessDomain::getDomainName, key)).or()).like(BusinessDomain::getTld, key));
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
      } else {
         throw new CommonException("请先登录", new Object[0]);
      }
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
   public void updateRegistrationStatus(String memberId, String domainName, String registrationStatus) {
      if (memberId != null && domainName != null) {
         LambdaUpdateWrapper<BusinessDomain> wrapper = (LambdaUpdateWrapper<BusinessDomain>)((LambdaUpdateWrapper<BusinessDomain>)((LambdaUpdateWrapper<BusinessDomain>)new LambdaUpdateWrapper<BusinessDomain>()
                  .eq(BusinessDomain::getMemberId, memberId))
               .eq(BusinessDomain::getDomainName, domainName))
            .set(BusinessDomain::getRegistrationStatus, registrationStatus);
         this.update(wrapper);
      }
   }

   @Override
   public void updateRegistrationStatusForCurrentUser(String domainName, String registrationStatus) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (loginUser != null && loginUser.getId() != null && domainName != null) {
         this.updateRegistrationStatus(String.valueOf(loginUser.getId()), domainName, registrationStatus);
      }
   }

   @Override
   public void assertOwnDomain(String domainName) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (loginUser == null || loginUser.getId() == null) {
         throw new CommonException("请先登录", new Object[0]);
      } else if (StrUtil.isBlank(domainName)) {
         throw new CommonException("域名不能为空", new Object[0]);
      } else {
         long count = this.count(
            (Wrapper<BusinessDomain>)((LambdaQueryWrapper<BusinessDomain>)new QueryWrapper<BusinessDomain>().lambda().eq(BusinessDomain::getMemberId, String.valueOf(loginUser.getId())))
               .eq(BusinessDomain::getDomainName, domainName.trim())
         );
         if (count < 1L) {
            throw new CommonException("域名不存在", new Object[0]);
         }
      }
   }
}

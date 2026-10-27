package com.rxinyun.admin.business.modular.dns.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.dns.entity.DnsDomain;
import com.rxinyun.admin.business.modular.dns.mapper.DnsDomainMapper;
import com.rxinyun.admin.business.modular.dns.mapper.DnsRecordMapper;
import com.rxinyun.admin.business.modular.dns.param.DnsDomainParam;
import com.rxinyun.admin.business.modular.dns.service.DnsDomainService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.DnsNsRecordResult;
import com.rxinyun.common.util.DnsLookupUtil;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsZoneUpsertParam;
import jakarta.annotation.Resource;
import java.net.IDN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DnsDomainServiceImpl extends ServiceImpl<DnsDomainMapper, DnsDomain> implements DnsDomainService {
   private static final Logger log = LoggerFactory.getLogger(DnsDomainServiceImpl.class);
   private static final int ENABLED = 1;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private DnsRecordMapper dnsRecordMapper;

   @Override
   public Page<DnsDomain> page(DnsDomainParam.PageParam pageParam) {
      QueryWrapper<DnsDomain> wrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DnsDomain>)((LambdaQueryWrapper<DnsDomain>)((LambdaQueryWrapper<DnsDomain>)((LambdaQueryWrapper<DnsDomain>)q.like(
                              DnsDomain::getDomainName, pageParam.getSearchKey()
                           ))
                           .or())
                        .like(DnsDomain::getMemberId, pageParam.getSearchKey()))
                     .or())
                  .like(DnsDomain::getPackageInstanceId, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberId())) {
         wrapper.lambda().eq(DnsDomain::getMemberId, pageParam.getMemberId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getPackageInstanceId())) {
         wrapper.lambda().eq(DnsDomain::getPackageInstanceId, pageParam.getPackageInstanceId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(DnsDomain::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<DnsDomain> page = (Page<DnsDomain>)this.page(CommonPageRequest.defaultPage(), wrapper);
      this.fillPackageInstanceNames(page.getRecords());
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(DnsDomainParam.AddParam addParam) {
      String domainName = this.normalizeDomain(addParam.getDomainName());
      this.validateUniqueDomain(domainName, null);
      DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getActiveInstance(addParam.getPackageInstanceId());
      this.validateMember(instance, addParam.getMemberId());
      this.dnsPackageInstanceApi.occupyQuota(instance.getId(), addParam.getMemberId());
      DnsDomain domain = (DnsDomain)BeanUtil.toBean(addParam, DnsDomain.class);
      domain.setId(IdUtil.getSnowflakeNextIdStr());
      domain.setDomainName(domainName);
      domain.setStatus((Integer)ObjectUtil.defaultIfNull(domain.getStatus(), 1));
      this.save(domain);
      this.safeDnsAutomation("DNS托管域名新增任务创建失败：" + domain.getId(), () -> this.dnsPackageInstanceApi.upsertZone(this.buildZoneUpsertParam(domain)));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(DnsDomainParam.EditParam editParam) {
      DnsDomain domain = this.queryEntity(editParam.getId());
      String domainName = this.normalizeDomain(editParam.getDomainName());
      this.validateUniqueDomain(domainName, editParam.getId());
      DnsPackageInstanceVO newInstance = this.dnsPackageInstanceApi.getActiveInstance(editParam.getPackageInstanceId());
      this.validateMember(newInstance, domain.getMemberId());
      if (!Objects.equals(domain.getPackageInstanceId(), editParam.getPackageInstanceId())) {
         this.dnsPackageInstanceApi.releaseQuota(domain.getPackageInstanceId());
         this.dnsPackageInstanceApi.occupyQuota(editParam.getPackageInstanceId(), domain.getMemberId());
         domain.setPackageInstanceId(editParam.getPackageInstanceId());
      }

      domain.setDomainName(domainName);
      domain.setEmail(editParam.getEmail());
      domain.setStatus((Integer)ObjectUtil.defaultIfNull(editParam.getStatus(), domain.getStatus()));
      domain.setRemark(editParam.getRemark());
      this.updateById(domain);
      this.safeDnsAutomation("DNS托管域名编辑任务创建失败：" + domain.getId(), () -> this.dnsPackageInstanceApi.upsertZone(this.buildZoneUpsertParam(domain)));
   }

   @Override
   public DnsDomain detail(DnsDomainParam.IdParam idParam) {
      DnsDomain domain = this.queryEntity(idParam.getId());
      this.fillPackageInstanceName(domain, new HashMap<>());
      return domain;
   }

   @Override
   public List<DnsNsRecordResult> nsRecord(DnsDomainParam.NsRecordParam nsRecordParam) {
      return DnsLookupUtil.queryNsRecords(this.resolveNsRecordDomains(nsRecordParam), nsRecordParam.getDnsServer());
   }

   @Override
   public void updateStatus(DnsDomainParam.StatusParam statusParam) {
      DnsDomain domain = this.queryEntity(statusParam.getId());
      domain.setStatus(statusParam.getStatus());
      this.updateById(domain);
      this.safeDnsAutomation("DNS托管域名状态任务创建失败：" + domain.getId(), () -> this.dnsPackageInstanceApi.upsertZone(this.buildZoneUpsertParam(domain)));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DnsDomainParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, DnsDomainParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            DnsDomain domain = this.queryEntity(id);
            this.safeDnsAutomation("DNS托管域名删除任务创建失败：" + domain.getId(), () -> this.dnsPackageInstanceApi.deleteZone(this.buildZoneUpsertParam(domain)));
            this.dnsPackageInstanceApi.releaseQuota(domain.getPackageInstanceId());
         }

         this.dnsRecordMapper.physicalDeleteByZoneIds(ids);
         ((DnsDomainMapper)this.baseMapper).physicalDeleteByIds(ids);
      }
   }

   private DnsDomain queryEntity(String id) {
      DnsDomain domain = (DnsDomain)this.getById(id);
      if (domain == null) {
         throw new CommonException("DNS托管域名不存在：{}", new Object[]{id});
      } else {
         return domain;
      }
   }

   private void validateMember(DnsPackageInstanceVO instance, String memberId) {
      if (!Objects.equals(instance.getMemberId(), memberId)) {
         throw new CommonException("DNS套餐实例不属于该会员", new Object[0]);
      }
   }

   private List<String> resolveNsRecordDomains(DnsDomainParam.NsRecordParam nsRecordParam) {
      List<String> domainNames = new ArrayList<>();
      if (CollUtil.isNotEmpty(nsRecordParam.getDomainNames())) {
         domainNames.addAll(nsRecordParam.getDomainNames());
      }

      if (CollUtil.isEmpty(domainNames)) {
         throw new CommonException("域名列表不能为空", new Object[0]);
      } else {
         return domainNames;
      }
   }

   private void fillPackageInstanceNames(List<DnsDomain> domains) {
      if (!CollUtil.isEmpty(domains)) {
         Map<String, DnsPackageInstanceVO> instanceMap = new HashMap<>();
         domains.forEach(domain -> this.fillPackageInstanceName(domain, instanceMap));
      }
   }

   private void fillPackageInstanceName(DnsDomain domain, Map<String, DnsPackageInstanceVO> instanceMap) {
      if (domain != null && !StrUtil.isBlank(domain.getPackageInstanceId())) {
         String instanceId = domain.getPackageInstanceId();
         if (instanceMap.containsKey(instanceId)) {
            this.fillPackageInstanceInfo(domain, instanceMap.get(instanceId));
         } else {
            try {
               DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getMemberInstance(instanceId, domain.getMemberId());
               instanceMap.put(instanceId, instance);
               this.fillPackageInstanceInfo(domain, instance);
            } catch (Exception var5) {
               instanceMap.put(instanceId, null);
            }
         }
      }
   }

   private void fillPackageInstanceInfo(DnsDomain domain, DnsPackageInstanceVO instance) {
      if (instance != null) {
         domain.setPackageInstanceName(instance.getPackageName());
         domain.setPrimaryNs(instance.getPrimaryNs());
         domain.setSecondaryNs(instance.getSecondaryNs());
         domain.setMinTtl(instance.getMinTtl());
      }
   }

   private void validateUniqueDomain(String domainName, String excludeId) {
      long count = this.count(
         ((LambdaQueryWrapper<DnsDomain>)new LambdaQueryWrapper<DnsDomain>().eq(DnsDomain::getDomainName, domainName)).ne(StrUtil.isNotBlank(excludeId), DnsDomain::getId, excludeId)
      );
      if (count > 0L) {
         throw new CommonException("DNS托管域名已存在：{}", new Object[]{domainName});
      }
   }

   private DnsZoneUpsertParam buildZoneUpsertParam(DnsDomain domain) {
      DnsZoneUpsertParam upsertParam = new DnsZoneUpsertParam();
      upsertParam.setZoneId(domain.getId());
      upsertParam.setPackageInstanceId(domain.getPackageInstanceId());
      upsertParam.setDomainName(domain.getDomainName());
      upsertParam.setAdminEmail(domain.getEmail());
      upsertParam.setEnabled(Objects.equals(domain.getStatus(), 1));
      return upsertParam;
   }

   private void safeDnsAutomation(String message, Runnable runnable) {
      try {
         runnable.run();
      } catch (Exception var4) {
         log.warn("{}，error={}", message, var4.getMessage());
      }
   }

   private String normalizeDomain(String domainName) {
      String value = StrUtil.trim(domainName).toLowerCase(Locale.ROOT);

      while (value.endsWith(".")) {
         value = value.substring(0, value.length() - 1);
      }

      if (!StrUtil.isBlank(value) && value.contains(".")) {
         try {
            String ascii = IDN.toASCII(value, 2).toLowerCase(Locale.ROOT);
            if (ascii.length() <= 253 && !ascii.startsWith(".") && !ascii.endsWith(".")) {
               return ascii;
            } else {
               throw new IllegalArgumentException();
            }
         } catch (Exception var4) {
            throw new CommonException("域名格式不正确：{}", new Object[]{domainName});
         }
      } else {
         throw new CommonException("域名格式不正确：{}", new Object[]{domainName});
      }
   }
}

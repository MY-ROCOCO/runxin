package com.rxinyun.member.business.modular.dns.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.DnsNsRecordResult;
import com.rxinyun.common.util.DnsLookupUtil;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsDomain;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsDomainMapper;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsRecordMapper;
import com.rxinyun.member.business.modular.dns.param.MemberDnsDomainParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsDomainService;
import com.rxinyun.production.api.CurrentMemberIdApi;
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
public class MemberDnsDomainServiceImpl extends ServiceImpl<MemberDnsDomainMapper, MemberDnsDomain> implements MemberDnsDomainService {
   private static final Logger log = LoggerFactory.getLogger(MemberDnsDomainServiceImpl.class);
   private static final int ENABLED = 1;
   @Resource
   private CurrentMemberIdApi currentMemberIdApi;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private MemberDnsRecordMapper memberDnsRecordMapper;

   @Override
   public Page<MemberDnsDomain> page(MemberDnsDomainParam.PageParam pageParam) {
      String memberId = this.getCurrentMemberId();
      QueryWrapper<MemberDnsDomain> wrapper = new QueryWrapper().checkSqlInjection();
      wrapper.lambda().eq(MemberDnsDomain::getMemberId, memberId);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<MemberDnsDomain>)((LambdaQueryWrapper<MemberDnsDomain>)q.like(MemberDnsDomain::getDomainName, pageParam.getSearchKey())).or())
                  .like(MemberDnsDomain::getEmail, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getPackageInstanceId())) {
         wrapper.lambda().eq(MemberDnsDomain::getPackageInstanceId, pageParam.getPackageInstanceId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(MemberDnsDomain::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<MemberDnsDomain> page = (Page<MemberDnsDomain>)this.page(CommonPageRequest.defaultPage(), wrapper);
      this.fillPackageInstanceNames(page.getRecords(), memberId);
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberDnsDomainParam.AddParam addParam) {
      String memberId = this.getCurrentMemberId();
      String domainName = this.normalizeDomain(addParam.getDomainName());
      this.validateUniqueDomain(domainName, null);
      DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getActiveInstance(addParam.getPackageInstanceId());
      if (!Objects.equals(instance.getMemberId(), memberId)) {
         throw new CommonException("DNS套餐实例不属于当前会员", new Object[0]);
      } else {
         this.dnsPackageInstanceApi.occupyQuota(instance.getId(), memberId);
         MemberDnsDomain domain = new MemberDnsDomain();
         domain.setId(IdUtil.getSnowflakeNextIdStr());
         domain.setMemberId(memberId);
         domain.setPackageInstanceId(instance.getId());
         domain.setDomainName(domainName);
         domain.setEmail(addParam.getEmail());
         domain.setStatus(1);
         domain.setRemark(addParam.getRemark());
         this.save(domain);
         this.safeDnsAutomation("会员DNS托管域名新增任务创建失败：" + domain.getId(), () -> this.dnsPackageInstanceApi.upsertZone(this.buildZoneUpsertParam(domain)));
      }
   }

   @Override
   public MemberDnsDomain detail(MemberDnsDomainParam.IdParam idParam) {
      MemberDnsDomain domain = this.queryOwnEntity(idParam.getId());
      this.fillPackageInstanceName(domain, this.getCurrentMemberId(), new HashMap<>());
      return domain;
   }

   @Override
   public List<DnsNsRecordResult> nsRecord(MemberDnsDomainParam.NsRecordParam nsRecordParam) {
      return DnsLookupUtil.queryNsRecords(this.resolveNsRecordDomains(nsRecordParam), nsRecordParam.getDnsServer());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<MemberDnsDomainParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, MemberDnsDomainParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            MemberDnsDomain domain = this.queryOwnEntity(id);
            this.safeDnsAutomation("会员DNS托管域名删除任务创建失败：" + domain.getId(), () -> this.dnsPackageInstanceApi.deleteZone(this.buildZoneUpsertParam(domain)));
            this.dnsPackageInstanceApi.releaseQuota(domain.getPackageInstanceId());
         }

         this.memberDnsRecordMapper.physicalDeleteByZoneIds(ids);
         ((MemberDnsDomainMapper)this.baseMapper).physicalDeleteByIds(ids);
      }
   }

   private MemberDnsDomain queryOwnEntity(String id) {
      String memberId = this.getCurrentMemberId();
      MemberDnsDomain domain = (MemberDnsDomain)this.getOne(
         (Wrapper<MemberDnsDomain>)((LambdaQueryWrapper<MemberDnsDomain>)new LambdaQueryWrapper<MemberDnsDomain>().eq(MemberDnsDomain::getId, id)).eq(MemberDnsDomain::getMemberId, memberId)
      );
      if (domain == null) {
         throw new CommonException("DNS托管域名不存在：{}", new Object[]{id});
      } else {
         return domain;
      }
   }

   private void validateUniqueDomain(String domainName, String excludeId) {
      long count = this.count(
         ((LambdaQueryWrapper<MemberDnsDomain>)new LambdaQueryWrapper<MemberDnsDomain>().eq(MemberDnsDomain::getDomainName, domainName))
            .ne(StrUtil.isNotBlank(excludeId), MemberDnsDomain::getId, excludeId)
      );
      if (count > 0L) {
         throw new CommonException("DNS托管域名已存在：{}", new Object[]{domainName});
      }
   }

   private List<String> resolveNsRecordDomains(MemberDnsDomainParam.NsRecordParam nsRecordParam) {
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

   private void fillPackageInstanceNames(List<MemberDnsDomain> domains, String memberId) {
      if (!CollUtil.isEmpty(domains)) {
         Map<String, DnsPackageInstanceVO> instanceMap = new HashMap<>();
         domains.forEach(domain -> this.fillPackageInstanceName(domain, memberId, instanceMap));
      }
   }

   private void fillPackageInstanceName(MemberDnsDomain domain, String memberId, Map<String, DnsPackageInstanceVO> instanceMap) {
      if (domain != null && !StrUtil.isBlank(domain.getPackageInstanceId())) {
         String instanceId = domain.getPackageInstanceId();
         if (instanceMap.containsKey(instanceId)) {
            this.fillPackageInstanceInfo(domain, instanceMap.get(instanceId));
         } else {
            try {
               DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getMemberInstance(instanceId, memberId);
               instanceMap.put(instanceId, instance);
               this.fillPackageInstanceInfo(domain, instance);
            } catch (Exception var6) {
               instanceMap.put(instanceId, null);
            }
         }
      }
   }

   private void fillPackageInstanceInfo(MemberDnsDomain domain, DnsPackageInstanceVO instance) {
      if (instance != null) {
         domain.setPackageInstanceName(instance.getPackageName());
         domain.setPrimaryNs(instance.getPrimaryNs());
         domain.setSecondaryNs(instance.getSecondaryNs());
         domain.setMinTtl(instance.getMinTtl());
      }
   }

   private String getCurrentMemberId() {
      String memberId = this.currentMemberIdApi.getCurrentMemberId();
      if (StrUtil.isBlank(memberId)) {
         throw new CommonException("请先登录", new Object[0]);
      } else {
         return memberId;
      }
   }

   private DnsZoneUpsertParam buildZoneUpsertParam(MemberDnsDomain domain) {
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

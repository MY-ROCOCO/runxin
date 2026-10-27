package com.rxinyun.member.business.modular.dns.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsDomain;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsRecord;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsResolveLog;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsDomainMapper;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsRecordMapper;
import com.rxinyun.member.business.modular.dns.mapper.MemberDnsResolveLogMapper;
import com.rxinyun.member.business.modular.dns.param.MemberDnsRecordParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsRecordService;
import com.rxinyun.production.api.CurrentMemberIdApi;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsRecordCommandParam;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberDnsRecordServiceImpl extends ServiceImpl<MemberDnsRecordMapper, MemberDnsRecord> implements MemberDnsRecordService {
   private static final Logger log = LoggerFactory.getLogger(MemberDnsRecordServiceImpl.class);
   private static final int ENABLED = 1;
   private static final int DISABLED = 0;
   private static final String DEFAULT_LINE = "default";
   private static final Set<String> SUPPORTED_TYPES = Set.of("A", "AAAA", "CNAME", "MX", "NS", "PTR", "TXT");
   @Resource
   private CurrentMemberIdApi currentMemberIdApi;
   @Resource
   private MemberDnsDomainMapper memberDnsDomainMapper;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private MemberDnsResolveLogMapper memberDnsResolveLogMapper;

   @Override
   public Page<MemberDnsRecord> page(MemberDnsRecordParam.PageParam pageParam) {
      String memberId = this.getCurrentMemberId();
      List<String> ownZoneIds = this.ownZoneIds(memberId);
      if (CollUtil.isEmpty(ownZoneIds)) {
         return new Page();
      } else {
         QueryWrapper<MemberDnsRecord> wrapper = new QueryWrapper().checkSqlInjection();
         wrapper.lambda().in(MemberDnsRecord::getZoneId, ownZoneIds);
         if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
            wrapper.lambda()
               .and(
                  q -> ((LambdaQueryWrapper<MemberDnsRecord>)((LambdaQueryWrapper<MemberDnsRecord>)((LambdaQueryWrapper<MemberDnsRecord>)((LambdaQueryWrapper<MemberDnsRecord>)q.like(
                                 MemberDnsRecord::getHost, pageParam.getSearchKey()
                              ))
                              .or())
                           .like(MemberDnsRecord::getName, pageParam.getSearchKey()))
                        .or())
                     .like(MemberDnsRecord::getValue, pageParam.getSearchKey())
               );
         }

         if (ObjectUtil.isNotEmpty(pageParam.getZoneId())) {
            this.queryOwnZone(pageParam.getZoneId(), memberId);
            wrapper.lambda().eq(MemberDnsRecord::getZoneId, pageParam.getZoneId());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getType())) {
            wrapper.lambda().eq(MemberDnsRecord::getType, this.normalizeType(pageParam.getType()));
         }

         if (ObjectUtil.isNotEmpty(pageParam.getEnabled())) {
            wrapper.lambda().eq(MemberDnsRecord::getEnabled, pageParam.getEnabled());
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<MemberDnsRecord>)this.page(CommonPageRequest.defaultPage(), wrapper);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberDnsRecordParam.AddParam addParam) {
      String memberId = this.getCurrentMemberId();
      MemberDnsDomain zone = this.queryOwnZone(addParam.getZoneId(), memberId);
      DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getMemberInstance(zone.getPackageInstanceId(), memberId);
      MemberDnsRecord record = (MemberDnsRecord)BeanUtil.toBean(addParam, MemberDnsRecord.class);
      record.setId(IdUtil.getSnowflakeNextIdStr());
      this.fillRecord(record, zone, instance);
      this.save(record);
      this.saveResolveLog("ADD", memberId, zone, record, null, JSONUtil.toJsonStr(record));
      this.safeDnsAutomation("会员DNS解析记录新增任务创建失败：" + record.getId(), () -> this.dnsPackageInstanceApi.upsertRecord(this.buildRecordCommandParam(record, zone)));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(MemberDnsRecordParam.EditParam editParam) {
      String memberId = this.getCurrentMemberId();
      MemberDnsRecord record = this.queryOwnRecord(editParam.getId(), memberId);
      String beforeContent = JSONUtil.toJsonStr(record);
      MemberDnsDomain zone = this.queryOwnZone(editParam.getZoneId(), memberId);
      DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getMemberInstance(zone.getPackageInstanceId(), memberId);
      BeanUtil.copyProperties(editParam, record, new String[0]);
      this.fillRecord(record, zone, instance);
      this.updateById(record);
      this.saveResolveLog("EDIT", memberId, zone, record, beforeContent, JSONUtil.toJsonStr(record));
      this.safeDnsAutomation("会员DNS解析记录编辑任务创建失败：" + record.getId(), () -> this.dnsPackageInstanceApi.updateRecord(this.buildRecordCommandParam(record, zone)));
   }

   @Override
   public MemberDnsRecord detail(MemberDnsRecordParam.IdParam idParam) {
      return this.queryOwnRecord(idParam.getId(), this.getCurrentMemberId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateStatus(MemberDnsRecordParam.StatusParam statusParam) {
      String memberId = this.getCurrentMemberId();
      MemberDnsRecord record = this.queryOwnRecord(statusParam.getId(), memberId);
      String beforeContent = JSONUtil.toJsonStr(record);
      record.setEnabled(this.normalizeEnabled(statusParam.getEnabled()));
      this.updateById(record);
      MemberDnsDomain zone = this.queryOwnZone(record.getZoneId(), memberId);
      this.saveResolveLog("STATUS", memberId, zone, record, beforeContent, JSONUtil.toJsonStr(record));
      this.safeDnsAutomation(
         "会员DNS解析记录状态任务创建失败：" + record.getId(), () -> this.dnsPackageInstanceApi.setRecordEnabled(this.buildRecordCommandParam(record, zone))
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<MemberDnsRecordParam.IdParam> idParams) {
      String memberId = this.getCurrentMemberId();
      List<String> ids = CollStreamUtil.toList(idParams, MemberDnsRecordParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            MemberDnsRecord record = this.queryOwnRecord(id, memberId);
            MemberDnsDomain zone = this.queryOwnZone(record.getZoneId(), memberId);
            this.saveResolveLog("DELETE", memberId, zone, record, JSONUtil.toJsonStr(record), null);
            this.safeDnsAutomation(
               "会员DNS解析记录删除任务创建失败：" + record.getId(), () -> this.dnsPackageInstanceApi.deleteRecord(this.buildRecordCommandParam(record, zone))
            );
         }

         ((MemberDnsRecordMapper)this.baseMapper).physicalDeleteByIds(ids);
      }
   }

   private void fillRecord(MemberDnsRecord record, MemberDnsDomain zone, DnsPackageInstanceVO instance) {
      String host = this.normalizeHost(record.getHost());
      String type = this.normalizeType(record.getType());
      record.setZoneId(zone.getId());
      record.setHost(host);
      record.setName(this.buildFullName(host, zone.getDomainName()));
      record.setType(type);
      record.setValue(StrUtil.trim(record.getValue()));
      record.setLine(StrUtil.blankToDefault(StrUtil.trim(record.getLine()), "default"));
      record.setEnabled(this.normalizeEnabled((Integer)ObjectUtil.defaultIfNull(record.getEnabled(), 1)));
      record.setTtl(this.resolveTtl(record.getTtl(), instance));
      if (!"MX".equals(type)) {
         record.setPriority(null);
      }
   }

   private MemberDnsRecord queryOwnRecord(String id, String memberId) {
      MemberDnsRecord record = (MemberDnsRecord)this.getById(id);
      if (record == null) {
         throw new CommonException("DNS解析记录不存在：{}", new Object[]{id});
      } else {
         this.queryOwnZone(record.getZoneId(), memberId);
         return record;
      }
   }

   private MemberDnsDomain queryOwnZone(String zoneId, String memberId) {
      MemberDnsDomain zone = (MemberDnsDomain)this.memberDnsDomainMapper
         .selectOne((Wrapper<MemberDnsDomain>)((LambdaQueryWrapper<MemberDnsDomain>)new LambdaQueryWrapper<MemberDnsDomain>().eq(MemberDnsDomain::getId, zoneId)).eq(MemberDnsDomain::getMemberId, memberId));
      if (zone == null) {
         throw new CommonException("DNS托管域名不存在：{}", new Object[]{zoneId});
      } else {
         return zone;
      }
   }

   private List<String> ownZoneIds(String memberId) {
      List<MemberDnsDomain> zones = this.memberDnsDomainMapper.selectList((Wrapper<MemberDnsDomain>)new LambdaQueryWrapper<MemberDnsDomain>().eq(MemberDnsDomain::getMemberId, memberId));
      return CollStreamUtil.toList(zones, MemberDnsDomain::getId);
   }

   private String normalizeHost(String host) {
      String value = StrUtil.trim(host).toLowerCase(Locale.ROOT);
      if (StrUtil.isBlank(value)) {
         throw new CommonException("主机记录不能为空", new Object[0]);
      } else {
         return value;
      }
   }

   private String normalizeType(String type) {
      String value = StrUtil.trim(type).toUpperCase(Locale.ROOT);
      if (!SUPPORTED_TYPES.contains(value)) {
         throw new CommonException("解析记录类型不支持：{}", new Object[]{type});
      } else {
         return value;
      }
   }

   private String buildFullName(String host, String domainName) {
      return "@".equals(host) ? domainName : host + "." + domainName;
   }

   private Integer resolveTtl(Integer ttl, DnsPackageInstanceVO instance) {
      int minTtl = (Integer)ObjectUtil.defaultIfNull(instance.getMinTtl(), 60);
      int actualTtl = (Integer)ObjectUtil.defaultIfNull(ttl, minTtl);
      if (actualTtl < minTtl) {
         throw new CommonException("TTL不能低于当前套餐最低TTL：{}", new Object[]{minTtl});
      } else {
         return actualTtl;
      }
   }

   private Integer normalizeEnabled(Integer enabled) {
      if (!Integer.valueOf(1).equals(enabled) && !Integer.valueOf(0).equals(enabled)) {
         throw new CommonException("启用状态只能是1启用或0暂停", new Object[0]);
      } else {
         return enabled;
      }
   }

   private DnsRecordCommandParam buildRecordCommandParam(MemberDnsRecord record, MemberDnsDomain zone) {
      if (zone == null) {
         throw new CommonException("DNS托管域名不存在：{}", new Object[]{record.getZoneId()});
      } else {
         DnsRecordCommandParam param = new DnsRecordCommandParam();
         param.setRecordId(record.getId());
         param.setZoneId(record.getZoneId());
         param.setPackageInstanceId(zone.getPackageInstanceId());
         param.setName(record.getName());
         param.setType(record.getType());
         param.setValue(record.getValue());
         param.setTtl(record.getTtl());
         param.setPriority(record.getPriority());
         param.setEnabled(Integer.valueOf(1).equals(record.getEnabled()));
         param.setLine(record.getLine());
         param.setCountry(record.getCountry());
         param.setRegion(record.getRegion());
         param.setProvince(record.getProvince());
         param.setCity(record.getCity());
         param.setIsp(record.getIsp());
         return param;
      }
   }

   private void saveResolveLog(String operateType, String memberId, MemberDnsDomain zone, MemberDnsRecord record, String beforeContent, String afterContent) {
      MemberDnsResolveLog resolveLog = new MemberDnsResolveLog();
      resolveLog.setId(IdUtil.getSnowflakeNextIdStr());
      resolveLog.setMemberId(memberId);
      resolveLog.setZoneId(record.getZoneId());
      resolveLog.setRecordId(record.getId());
      resolveLog.setDomainName(zone.getDomainName());
      resolveLog.setRecordName(record.getName());
      resolveLog.setRecordType(record.getType());
      resolveLog.setRecordValue(record.getValue());
      resolveLog.setTtl(record.getTtl());
      resolveLog.setPriority(record.getPriority());
      resolveLog.setLine(record.getLine());
      resolveLog.setEnabled(record.getEnabled());
      resolveLog.setOperateType(operateType);
      resolveLog.setOperateSource("MEMBER");
      resolveLog.setOperateUser(memberId);
      resolveLog.setBeforeContent(beforeContent);
      resolveLog.setAfterContent(afterContent);
      resolveLog.setOperateTime(new Date());
      this.memberDnsResolveLogMapper.insert(resolveLog);
   }

   private void safeDnsAutomation(String message, Runnable runnable) {
      try {
         runnable.run();
      } catch (Exception var4) {
         log.warn("{}，error={}", message, var4.getMessage());
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
}

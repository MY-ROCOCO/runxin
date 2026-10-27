package com.rxinyun.member.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.DnsNsRecordResult;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsDomain;
import com.rxinyun.member.business.modular.dns.param.MemberDnsDomainParam;
import java.util.List;

public interface MemberDnsDomainService {
   Page<MemberDnsDomain> page(MemberDnsDomainParam.PageParam pageParam);

   void add(MemberDnsDomainParam.AddParam addParam);

   MemberDnsDomain detail(MemberDnsDomainParam.IdParam idParam);

   List<DnsNsRecordResult> nsRecord(MemberDnsDomainParam.NsRecordParam nsRecordParam);

   void delete(List<MemberDnsDomainParam.IdParam> idParams);
}

package com.rxinyun.member.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsRecord;
import com.rxinyun.member.business.modular.dns.param.MemberDnsRecordParam;
import java.util.List;

public interface MemberDnsRecordService {
   Page<MemberDnsRecord> page(MemberDnsRecordParam.PageParam pageParam);

   void add(MemberDnsRecordParam.AddParam addParam);

   void edit(MemberDnsRecordParam.EditParam editParam);

   MemberDnsRecord detail(MemberDnsRecordParam.IdParam idParam);

   void updateStatus(MemberDnsRecordParam.StatusParam statusParam);

   void delete(List<MemberDnsRecordParam.IdParam> idParams);
}

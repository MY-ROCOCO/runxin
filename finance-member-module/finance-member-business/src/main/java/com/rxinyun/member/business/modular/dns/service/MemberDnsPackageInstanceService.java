package com.rxinyun.member.business.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.business.modular.dns.param.MemberDnsPackageInstanceParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;

public interface MemberDnsPackageInstanceService {
   Page<DnsPackageInstanceVO> page(MemberDnsPackageInstanceParam.PageParam pageParam);
}

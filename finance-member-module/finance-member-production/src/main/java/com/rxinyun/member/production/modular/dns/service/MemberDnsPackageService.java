package com.rxinyun.member.production.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.dns.param.MemberDnsPackagePageParam;
import com.rxinyun.production.api.dto.DnsPackageVO;

public interface MemberDnsPackageService {
   Page<DnsPackageVO> page(MemberDnsPackagePageParam pageParam);
}

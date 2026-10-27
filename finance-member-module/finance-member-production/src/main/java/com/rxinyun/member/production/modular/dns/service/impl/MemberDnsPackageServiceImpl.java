package com.rxinyun.member.production.modular.dns.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.dns.param.MemberDnsPackagePageParam;
import com.rxinyun.member.production.modular.dns.service.MemberDnsPackageService;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstancePageParam;
import com.rxinyun.production.api.dto.DnsPackageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberDnsPackageServiceImpl implements MemberDnsPackageService {
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;

   @Override
   public Page<DnsPackageVO> page(MemberDnsPackagePageParam pageParam) {
      DnsPackageInstancePageParam apiParam = (DnsPackageInstancePageParam)BeanUtil.toBean(pageParam, DnsPackageInstancePageParam.class);
      return this.dnsPackageInstanceApi.activePackagePage(apiParam);
   }
}

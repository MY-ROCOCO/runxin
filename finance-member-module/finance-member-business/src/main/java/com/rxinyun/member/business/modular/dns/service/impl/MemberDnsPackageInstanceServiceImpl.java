package com.rxinyun.member.business.modular.dns.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.member.business.modular.dns.param.MemberDnsPackageInstanceParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsPackageInstanceService;
import com.rxinyun.production.api.CurrentMemberIdApi;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstancePageParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberDnsPackageInstanceServiceImpl implements MemberDnsPackageInstanceService {
   @Resource
   private CurrentMemberIdApi currentMemberIdApi;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;

   @Override
   public Page<DnsPackageInstanceVO> page(MemberDnsPackageInstanceParam.PageParam pageParam) {
      String memberId = this.currentMemberIdApi.getCurrentMemberId();
      if (StrUtil.isBlank(memberId)) {
         throw new CommonException("请先登录", new Object[0]);
      } else {
         DnsPackageInstancePageParam apiParam = (DnsPackageInstancePageParam)BeanUtil.toBean(pageParam, DnsPackageInstancePageParam.class);
         apiParam.setMemberId(memberId);
         return this.dnsPackageInstanceApi.memberInstancePage(apiParam);
      }
   }
}

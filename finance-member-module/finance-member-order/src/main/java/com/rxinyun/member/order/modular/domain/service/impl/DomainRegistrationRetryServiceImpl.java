package com.rxinyun.member.order.modular.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.business.api.BusinessDomainApi;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.order.modular.domain.entity.DomainRegistrationRequest;
import com.rxinyun.member.order.modular.domain.mapper.DomainRegistrationRequestMapper;
import com.rxinyun.member.order.modular.domain.service.DomainRegistrationRetryService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderMapper;
import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient;
import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient.DomainCreateResult;
import com.rxinyun.production.api.DomainRegistrationRetryApi;
import jakarta.annotation.Resource;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistrationRetryServiceImpl implements DomainRegistrationRetryService, DomainRegistrationRetryApi {
   private static final Logger log = LoggerFactory.getLogger(DomainRegistrationRetryServiceImpl.class);
   private static final String CONFIG_XINWANG_APP_ID = "DOMAIN_XINWANG_APP_ID";
   private static final String CONFIG_XINWANG_APP_KEY = "DOMAIN_XINWANG_APP_KEY";
   @Resource
   private DomainRegistrationRequestMapper domainRegistrationRequestMapper;
   @Resource
   private XinwangDomainApiClient xinwangDomainApiClient;
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private BusinessDomainApi businessDomainApi;
   @Resource
   private MemberOrderMapper memberOrderMapper;

   @Override
   public void retry(String requestId) {
      if (requestId != null && !requestId.isBlank()) {
         DomainRegistrationRequest req = (DomainRegistrationRequest)this.domainRegistrationRequestMapper.selectById(requestId);
         if (req == null) {
            throw new IllegalArgumentException("上游记录不存在");
         } else if (req.getStatus() != null && req.getStatus() != 2) {
            throw new IllegalArgumentException("仅失败记录可重新发起");
         } else if (StrUtil.isBlank(req.getDomainName())) {
            throw new IllegalArgumentException("请求参数不完整：域名为空");
         } else if (StrUtil.isBlank(req.getTemplateName())) {
            throw new IllegalArgumentException("请求参数不完整：模板名称为空，无法重试");
         } else {
            int years = req.getYears() != null ? req.getYears() : 1;
            String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
            String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");

            try {
               DomainCreateResult result = this.xinwangDomainApiClient.createDomain(appId, appKey, req.getDomainName(), req.getTemplateName(), years);
               req.setUpstreamRequestId(result.getRequestId());
               req.setUpstreamCode(result.getCode());
               req.setUpstreamMsg(result.getMsg());
               req.setStatus("0".equals(result.getCode()) ? 1 : 2);
               if (1 == req.getStatus()) {
                  MemberOrder order = (MemberOrder)this.memberOrderMapper
                     .selectOne((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, req.getOrderNo()));
                  if (order != null) {
                     this.businessDomainApi.addRegisteredDomain(String.valueOf(order.getMemberNo()), req.getDomainName(), "", years, null, "XINWANG");
                  }
               }
            } catch (Exception var8) {
               log.warn("重新发起域名注册失败 requestId={} domain={} err={}", new Object[]{requestId, req.getDomainName(), var8.getMessage()});
               req.setStatus(2);
               String msg = var8.getMessage();
               req.setUpstreamMsg(msg != null && msg.length() > 500 ? msg.substring(0, 500) : msg);
            }

            req.setUpdateTime(new Date());
            this.domainRegistrationRequestMapper.updateById(req);
         }
      } else {
         throw new IllegalArgumentException("记录 id 不能为空");
      }
   }
}

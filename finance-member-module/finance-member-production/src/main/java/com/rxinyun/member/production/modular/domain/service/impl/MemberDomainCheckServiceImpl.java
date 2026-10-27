package com.rxinyun.member.production.modular.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient;
import com.rxinyun.member.production.modular.domain.dto.DomainCheckItemVO;
import com.rxinyun.member.production.modular.domain.dto.DomainCheckResultVO;
import com.rxinyun.member.production.modular.domain.param.DomainCheckParam;
import com.rxinyun.member.production.modular.domain.service.MemberDomainCheckService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberDomainCheckServiceImpl implements MemberDomainCheckService {
   private static final Logger log = LoggerFactory.getLogger(MemberDomainCheckServiceImpl.class);
   private static final String CONFIG_PRIMARY_PROVIDER = "DOMAIN_REGISTER_PRIMARY_PROVIDER";
   private static final String CONFIG_XINWANG_APP_ID = "DOMAIN_XINWANG_APP_ID";
   private static final String CONFIG_XINWANG_APP_KEY = "DOMAIN_XINWANG_APP_KEY";
   private static final String PROVIDER_XINWANG = "XINWANG";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private XinwangDomainApiClient xinwangDomainApiClient;

   @Override
   public DomainCheckResultVO checkRegister(DomainCheckParam param) {
      String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
      if (!"XINWANG".equals(primaryProvider)) {
         throw new CommonException("当前首选通道不是新网，无法查询域名可注册。请先在后台将「域名注册首选接口商」配置为新网（XINWANG）", new Object[0]);
      } else {
         Map<String, Object> body = new HashMap<>();
         body.put("prefix", param.getPrefix());
         body.put("suffix", param.getSuffix());
         String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
         String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
         XinwangDomainApiClient.DomainCheckResult raw = this.xinwangDomainApiClient.checkRegister(appId, appKey, body);
         DomainCheckResultVO vo = this.mapToResultVO(raw);
         log.debug("域名可注册查询已映射为自建VO返回，data条数={}", vo.getData() != null ? vo.getData().size() : 0);
         return vo;
      }
   }

   private DomainCheckResultVO mapToResultVO(XinwangDomainApiClient.DomainCheckResult raw) {
      DomainCheckResultVO vo = new DomainCheckResultVO();
      vo.setCode(raw.getCode());
      vo.setMsg(raw.getMsg());
      vo.setRequestId(raw.getRequestId());
      List<DomainCheckItemVO> list = new ArrayList<>();
      if (raw.getData() != null) {
         for (XinwangDomainApiClient.DomainCheckItem item : raw.getData()) {
            DomainCheckItemVO d = new DomainCheckItemVO();
            d.setDomainName(item.getDomainName());
            d.setCanReg(parseCanReg(item.getCanReg()));
            d.setFeeLabel(item.getFeeLabel());
            d.setFeePrice(item.getFeePrice());
            d.setComment(item.getComment());
            list.add(d);
         }
      }

      vo.setData(list);
      return vo;
   }

   private static Integer parseCanReg(String canReg) {
      if (canReg != null && !canReg.isBlank()) {
         try {
            return Integer.parseInt(canReg.trim());
         } catch (NumberFormatException var2) {
            return null;
         }
      } else {
         return null;
      }
   }
}

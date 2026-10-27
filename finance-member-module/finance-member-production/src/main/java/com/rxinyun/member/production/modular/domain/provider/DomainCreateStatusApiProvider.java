package com.rxinyun.member.production.modular.domain.provider;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient;
import com.rxinyun.production.api.DomainCreateStatusApi;
import com.rxinyun.production.api.dto.DomainCreateStatusVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DomainCreateStatusApiProvider implements DomainCreateStatusApi {
   private static final Logger log = LoggerFactory.getLogger(DomainCreateStatusApiProvider.class);
   private static final String CONFIG_PRIMARY_PROVIDER = "DOMAIN_REGISTER_PRIMARY_PROVIDER";
   private static final String CONFIG_XINWANG_APP_ID = "DOMAIN_XINWANG_APP_ID";
   private static final String CONFIG_XINWANG_APP_KEY = "DOMAIN_XINWANG_APP_KEY";
   private static final String PROVIDER_XINWANG = "XINWANG";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private XinwangDomainApiClient xinwangDomainApiClient;

   public DomainCreateStatusVO getCreateStatus(String domainName) {
      if (StrUtil.isBlank(domainName)) {
         throw new IllegalArgumentException("域名不能为空");
      } else {
         String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
         if (!"XINWANG".equals(primaryProvider)) {
            throw new IllegalStateException("当前首选通道不是新网，无法查询注册状态。请先在后台将「域名注册首选接口商」配置为新网（XINWANG）");
         } else {
            String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
            String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
            XinwangDomainApiClient.DomainCreateStatusResult raw = this.xinwangDomainApiClient.createStatus(appId, appKey, domainName.trim());
            DomainCreateStatusVO vo = new DomainCreateStatusVO();
            vo.setCode(raw.getCode());
            vo.setMsg(raw.getMsg());
            vo.setRequestId(raw.getRequestId());
            if (raw.getData() != null) {
               vo.setDomainName(raw.getData().getDomainName());
               String state = raw.getData().getStatus();
               vo.setStatus(state);
               vo.setStatusLabel(mapStateToLabel(state));
               vo.setRemark(raw.getData().getRemark());
            } else {
               vo.setDomainName(domainName.trim());
            }

            return vo;
         }
      }
   }

   private static String mapStateToLabel(String state) {
      if (state == null) {
         return null;
      } else {
         switch (state) {
            case "create-waiting":
               return "注册中";
            case "create-success":
               return "注册成功";
            default:
               return state;
         }
      }
   }
}

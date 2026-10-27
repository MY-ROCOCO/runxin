package com.rxinyun.member.production.modular.domain.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XinwangDomainApiClient {
   private static final Logger log = LoggerFactory.getLogger(XinwangDomainApiClient.class);
   private static final String DOMAIN_CHECK_URL = "https://apiv2.xinnet.com/api/domain/check";
   private static final String DOMAIN_CREATE_URL = "https://apiv2.xinnet.com/api/domain/create";
   private static final String DOMAIN_CREATE_STATUS_URL = "https://apiv2.xinnet.com/api/domain/createStatus";
   private static final String DOMAIN_QUERY_MANAGEMENT_PWD_URL = "https://apiv2.xinnet.com/api/domain/queryManagementPwd";
   private static final String URL_PATH_CHECK = "/api/domain/check/";
   private static final String URL_PATH_CREATE = "/api/domain/create/";
   private static final String URL_PATH_CREATE_STATUS = "/api/domain/createStatus/";
   private static final String URL_PATH_QUERY_MANAGEMENT_PWD = "/api/domain/queryManagementPwd/";
   public static final String DNS1 = "ns11.xincache.com";
   public static final String DNS2 = "ns12.xincache.com";
   private static final DateTimeFormatter UTC_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

   public XinwangDomainApiClient.DomainCheckResult checkRegister(String appId, String appKey, Map<String, Object> body) {
      if (!StrUtil.isBlank(appId) && !StrUtil.isBlank(appKey)) {
         String bodyJson = JSONUtil.toJsonStr(body);
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/domain/check/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         log.info("新网域名可注册查询 url={} prefix={}", "https://apiv2.xinnet.com/api/domain/check", body.get("prefix"));
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/domain/check")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网域名可注册查询响应 code={} bodyLength={}（上游原始响应，已由服务层映射为自建VO返回前端）", response.getStatus(), responseBody != null ? responseBody.length() : 0);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangDomainApiClient.DomainCheckResult result = new XinwangDomainApiClient.DomainCheckResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            Object data = jo.get("data");
            if (data instanceof JSONArray) {
               List<XinwangDomainApiClient.DomainCheckItem> list = new ArrayList<>();
               ((JSONArray)data).forEach(e -> {
                  JSONObject o = (JSONObject)e;
                  XinwangDomainApiClient.DomainCheckItem item = new XinwangDomainApiClient.DomainCheckItem();
                  item.setDomainName(o.getStr("domainName"));
                  item.setCanReg(o.getStr("canReg"));
                  item.setFeeLabel(o.getStr("feeLabel"));
                  item.setFeePrice(o.getStr("feePrice"));
                  item.setComment(o.getStr("comment"));
                  list.add(item);
               });
               result.setData(list);
            }

            return result;
         }
      } else {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
      }
   }

   public XinwangDomainApiClient.DomainCreateResult createDomain(String appId, String appKey, String domainName, String templateName, int years) {
      if (!StrUtil.isBlank(appId) && !StrUtil.isBlank(appKey)) {
         Map<String, Object> body = new HashMap<>();
         body.put("domainName", domainName);
         body.put("templateName", templateName);
         body.put("year", years);
         body.put("price", null);
         body.put("registrar", "xinnet");
         body.put("dns1", "ns11.xincache.com");
         body.put("dns2", "ns12.xincache.com");
         body.put("dns3", "");
         body.put("dns4", "");
         body.put("dns5", "");
         body.put("dns6", "");
         body.put("dns7", "");
         body.put("dns8", "");
         String bodyJson = JSONUtil.toJsonStr(body);
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/domain/create/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         log.info("新网域名注册 url={} domainName={} templateName={}", new Object[]{"https://apiv2.xinnet.com/api/domain/create", domainName, templateName});
         log.info("注册域名请求上游接口商传参（供上游排查用） body={}", bodyJson);
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/domain/create")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网域名注册响应 code={} body={}", response.getStatus(), responseBody);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangDomainApiClient.DomainCreateResult result = new XinwangDomainApiClient.DomainCreateResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            return result;
         }
      } else {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
      }
   }

   public XinwangDomainApiClient.DomainCreateStatusResult createStatus(String appId, String appKey, String domainName) {
      if (StrUtil.isBlank(appId) || StrUtil.isBlank(appKey)) {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
      } else if (StrUtil.isBlank(domainName)) {
         throw new CommonException("域名不能为空", new Object[0]);
      } else {
         Map<String, Object> body = new HashMap<>();
         body.put("domainName", domainName.trim());
         String bodyJson = JSONUtil.toJsonStr(body);
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/domain/createStatus/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         log.info("新网域名注册状态查询 url={} domainName={}", "https://apiv2.xinnet.com/api/domain/createStatus", domainName);
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/domain/createStatus")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网域名注册状态查询响应 code={} body={}", response.getStatus(), responseBody);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangDomainApiClient.DomainCreateStatusResult result = new XinwangDomainApiClient.DomainCreateStatusResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            Object data = jo.get("data");
            if (data != null && data instanceof JSONObject dataObj) {
               XinwangDomainApiClient.DomainCreateStatusData statusData = new XinwangDomainApiClient.DomainCreateStatusData();
               statusData.setDomainName(dataObj.getStr("domainName"));
               statusData.setStatus(dataObj.getStr("state"));
               statusData.setRemark(dataObj.getStr("remark"));
               result.setData(statusData);
            }

            return result;
         }
      }
   }

   public XinwangDomainApiClient.DomainManagementPwdResult queryManagementPwd(String appId, String appKey, String domainName) {
      if (StrUtil.isBlank(appId) || StrUtil.isBlank(appKey)) {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
      } else if (StrUtil.isBlank(domainName)) {
         throw new CommonException("域名不能为空", new Object[0]);
      } else {
         Map<String, Object> body = new HashMap<>();
         body.put("domainName", domainName.trim());
         String bodyJson = JSONUtil.toJsonStr(body);
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/domain/queryManagementPwd/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         log.info("新网获取域名管理密码 url={} domainName={}", "https://apiv2.xinnet.com/api/domain/queryManagementPwd", domainName);
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/domain/queryManagementPwd")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网获取域名管理密码响应 code={} body={}", response.getStatus(), responseBody);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangDomainApiClient.DomainManagementPwdResult result = new XinwangDomainApiClient.DomainManagementPwdResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            Object data = jo.get("data");
            if (data != null && data instanceof JSONObject dataObj) {
               XinwangDomainApiClient.DomainManagementPwdData pwdData = new XinwangDomainApiClient.DomainManagementPwdData();
               pwdData.setDomainName(dataObj.getStr("domainName"));
               pwdData.setPassword(dataObj.getStr("password"));
               result.setData(pwdData);
            }

            return result;
         }
      }
   }

   private String buildSignature(String appKey, String timestamp, String urlPath, String bodyJson) {
      try {
         String stringToSign = "HMAC-SHA256\n" + timestamp + "\nPOST\n" + urlPath + "\n" + bodyJson;
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(new SecretKeySpec(appKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         byte[] hash = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
         StringBuilder hex = new StringBuilder(hash.length * 2);

         for (byte b : hash) {
            hex.append(String.format("%02x", b));
         }

         return hex.toString();
      } catch (Exception var13) {
         throw new CommonException("新网签名计算失败：{}", new Object[]{var13.getMessage()});
      }
   }

   public static class DomainCheckItem {
      private String domainName;
      private String canReg;
      private String feeLabel;
      private String feePrice;
      private String comment;

      public String getDomainName() {
         return this.domainName;
      }

      public String getCanReg() {
         return this.canReg;
      }

      public String getFeeLabel() {
         return this.feeLabel;
      }

      public String getFeePrice() {
         return this.feePrice;
      }

      public String getComment() {
         return this.comment;
      }

      public void setDomainName(final String domainName) {
         this.domainName = domainName;
      }

      public void setCanReg(final String canReg) {
         this.canReg = canReg;
      }

      public void setFeeLabel(final String feeLabel) {
         this.feeLabel = feeLabel;
      }

      public void setFeePrice(final String feePrice) {
         this.feePrice = feePrice;
      }

      public void setComment(final String comment) {
         this.comment = comment;
      }
   }

   public static class DomainCheckResult {
      private String code;
      private String msg;
      private String requestId;
      private List<XinwangDomainApiClient.DomainCheckItem> data;

      public String getCode() {
         return this.code;
      }

      public String getMsg() {
         return this.msg;
      }

      public String getRequestId() {
         return this.requestId;
      }

      public List<XinwangDomainApiClient.DomainCheckItem> getData() {
         return this.data;
      }

      public void setCode(final String code) {
         this.code = code;
      }

      public void setMsg(final String msg) {
         this.msg = msg;
      }

      public void setRequestId(final String requestId) {
         this.requestId = requestId;
      }

      public void setData(final List<XinwangDomainApiClient.DomainCheckItem> data) {
         this.data = data;
      }
   }

   public static class DomainCreateResult {
      private String code;
      private String msg;
      private String requestId;

      public String getCode() {
         return this.code;
      }

      public String getMsg() {
         return this.msg;
      }

      public String getRequestId() {
         return this.requestId;
      }

      public void setCode(final String code) {
         this.code = code;
      }

      public void setMsg(final String msg) {
         this.msg = msg;
      }

      public void setRequestId(final String requestId) {
         this.requestId = requestId;
      }
   }

   public static class DomainCreateStatusData {
      private String domainName;
      private String status;
      private String remark;

      public String getDomainName() {
         return this.domainName;
      }

      public String getStatus() {
         return this.status;
      }

      public String getRemark() {
         return this.remark;
      }

      public void setDomainName(final String domainName) {
         this.domainName = domainName;
      }

      public void setStatus(final String status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }
   }

   public static class DomainCreateStatusResult {
      private String code;
      private String msg;
      private String requestId;
      private XinwangDomainApiClient.DomainCreateStatusData data;

      public String getCode() {
         return this.code;
      }

      public String getMsg() {
         return this.msg;
      }

      public String getRequestId() {
         return this.requestId;
      }

      public XinwangDomainApiClient.DomainCreateStatusData getData() {
         return this.data;
      }

      public void setCode(final String code) {
         this.code = code;
      }

      public void setMsg(final String msg) {
         this.msg = msg;
      }

      public void setRequestId(final String requestId) {
         this.requestId = requestId;
      }

      public void setData(final XinwangDomainApiClient.DomainCreateStatusData data) {
         this.data = data;
      }
   }

   public static class DomainManagementPwdData {
      private String domainName;
      private String password;

      public String getDomainName() {
         return this.domainName;
      }

      public String getPassword() {
         return this.password;
      }

      public void setDomainName(final String domainName) {
         this.domainName = domainName;
      }

      public void setPassword(final String password) {
         this.password = password;
      }
   }

   public static class DomainManagementPwdResult {
      private String code;
      private String msg;
      private String requestId;
      private XinwangDomainApiClient.DomainManagementPwdData data;

      public String getCode() {
         return this.code;
      }

      public String getMsg() {
         return this.msg;
      }

      public String getRequestId() {
         return this.requestId;
      }

      public XinwangDomainApiClient.DomainManagementPwdData getData() {
         return this.data;
      }

      public void setCode(final String code) {
         this.code = code;
      }

      public void setMsg(final String msg) {
         this.msg = msg;
      }

      public void setRequestId(final String requestId) {
         this.requestId = requestId;
      }

      public void setData(final XinwangDomainApiClient.DomainManagementPwdData data) {
         this.data = data;
      }
   }
}

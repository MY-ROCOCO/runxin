package com.rxinyun.member.production.modular.domain.template.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XinwangTemplateApiClient {
   private static final Logger log = LoggerFactory.getLogger(XinwangTemplateApiClient.class);
   private static final String CREATE_TEMPLATE_URL = "https://apiv2.xinnet.com/api/template/create";
   private static final String QUERY_TEMPLATE_URL = "https://apiv2.xinnet.com/api/template/query";
   private static final String GET_CHINA_REGIONS_URL = "https://apiv2.xinnet.com/api/internationdomain/getChinaRegions";
   private static final String URL_PATH_CREATE = "/api/template/create/";
   private static final String URL_PATH_QUERY_TEMPLATE = "/api/template/query/";
   private static final String URL_PATH_GET_CHINA_REGIONS = "/api/internationdomain/getChinaRegions/";
   private static final DateTimeFormatter UTC_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

   public XinwangTemplateApiClient.XinwangTemplateCreateResult createTemplate(String appId, String appKey, Map<String, Object> body) {
      if (!StrUtil.isBlank(appId) && !StrUtil.isBlank(appKey)) {
         String bodyJson = JSONUtil.toJsonStr(body);
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/template/create/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         String logBody = bodyJson.length() > 2000 ? bodyJson.substring(0, 2000) + "...[后续省略,总长" + bodyJson.length() + "]" : bodyJson;
         log.info("新网创建模板请求 url={} 提交数据: {}", "https://apiv2.xinnet.com/api/template/create", logBody);
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/template/create")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网创建模板响应 code={} body={}", response.getStatus(), responseBody);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangTemplateApiClient.XinwangTemplateCreateResult result = new XinwangTemplateApiClient.XinwangTemplateCreateResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            result.setData(jo.get("data"));
            return result;
         }
      } else {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
      }
   }

   public XinwangTemplateApiClient.XinwangTemplateCreateResult queryTemplate(String appId, String appKey, String templateName) {
      if (!StrUtil.isBlank(appId) && !StrUtil.isBlank(appKey)) {
         String bodyJson = JSONUtil.toJsonStr(Map.of("templateName", StrUtil.nullToEmpty(templateName)));
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/template/query/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         log.info("新网查询模板 url={} templateName={}", "https://apiv2.xinnet.com/api/template/query", templateName);
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/template/query")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网查询模板响应 code={} bodyLength={}", response.getStatus(), responseBody != null ? responseBody.length() : 0);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangTemplateApiClient.XinwangTemplateCreateResult result = new XinwangTemplateApiClient.XinwangTemplateCreateResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            result.setData(jo.get("data"));
            return result;
         }
      } else {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
      }
   }

   public XinwangTemplateApiClient.XinwangTemplateCreateResult getChinaRegions(String appId, String appKey) {
      if (!StrUtil.isBlank(appId) && !StrUtil.isBlank(appKey)) {
         String bodyJson = "{}";
         String timestamp = UTC_TIMESTAMP_FORMAT.format(Instant.now());
         String signature = this.buildSignature(appKey, timestamp, "/api/internationdomain/getChinaRegions/", bodyJson);
         String authorization = "HMAC-SHA256 Access=" + appId + ", Signature=" + signature;
         log.info("新网国内省市级联 url={}", "https://apiv2.xinnet.com/api/internationdomain/getChinaRegions");
         HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post("https://apiv2.xinnet.com/api/internationdomain/getChinaRegions")
                  .body(bodyJson)
                  .contentType("application/json")
                  .header("timestamp", timestamp))
               .header("authorization", authorization))
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         log.info("新网国内省市级联响应 code={} bodyLength={}", response.getStatus(), responseBody != null ? responseBody.length() : 0);
         if (!response.isOk()) {
            throw new CommonException("新网接口请求异常：HTTP {}", new Object[]{response.getStatus()});
         } else {
            JSONObject jo = JSONUtil.parseObj(responseBody);
            XinwangTemplateApiClient.XinwangTemplateCreateResult result = new XinwangTemplateApiClient.XinwangTemplateCreateResult();
            result.setCode(jo.getStr("code"));
            result.setMsg(jo.getStr("msg"));
            result.setRequestId(jo.getStr("requestId"));
            result.setData(jo.get("data"));
            return result;
         }
      } else {
         throw new CommonException("新网接口未配置：请配置 DOMAIN_XINWANG_APP_ID 与 DOMAIN_XINWANG_APP_KEY", new Object[0]);
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

   public static class XinwangTemplateCreateResult {
      private String code;
      private String msg;
      private String requestId;
      private Object data;

      public String getCode() {
         return this.code;
      }

      public String getMsg() {
         return this.msg;
      }

      public String getRequestId() {
         return this.requestId;
      }

      public Object getData() {
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

      public void setData(final Object data) {
         this.data = data;
      }
   }
}

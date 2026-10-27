package com.rxinyun.admin.business.modular.esc.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourcePoolUpstreamClient {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolUpstreamClient.class);
   @Resource
   private ResourcePoolApi resourcePoolApi;

   public boolean isResourcePoolServer(Server server) {
      return server != null && Integer.valueOf(1).equals(server.getType());
   }

   public CommonResult<?> callOrderServerApi(Server server, String serverPathSegment, JSONObject body) {
      if (server != null && !StrUtil.isBlank(server.getResourcePoolId())) {
         BaseResourcePool pool = this.resourcePoolApi.getById(server.getResourcePoolId());
         if (pool == null) {
            return CommonResult.error("资源池不存在");
         } else if (pool.getStatus() != null && pool.getStatus() == 0) {
            return CommonResult.error("资源池已禁用");
         } else {
            String apiUrl = pool.getApiUrl();
            if (StrUtil.isBlank(apiUrl)) {
               return CommonResult.error("资源池API地址未配置");
            } else {
               if (!apiUrl.endsWith("/")) {
                  apiUrl = apiUrl + "/";
               }

               String url = apiUrl + "api/order/server/" + serverPathSegment;
               JSONObject req = body != null ? body : new JSONObject();
               String poolSid = StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId());
               req.put("upstreamServerId", poolSid);
               String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
               log.info("资源池上游调用: {} body={}", url, req.toJSONString());
               HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(url).header("Content-Type", "application/json"))
                        .header("X-API-Key", pool.getApiKey()))
                     .header("X-API-Secret", apiSecret))
                  .body(req.toJSONString())
                  .timeout(120000)
                  .execute();
               String respBody = response.body();
               return !response.isOk() ? CommonResult.error("上游请求失败 HTTP " + response.getStatus() + "：" + respBody) : this.parseUpstreamCommonResult(respBody);
            }
         }
      } else {
         return CommonResult.error("资源池ID未配置，无法调用上游");
      }
   }

   public CommonResult<?> parseUpstreamCommonResult(String respBody) {
      try {
         JSONObject json = JSON.parseObject(respBody);
         Integer code = json.getInteger("code");
         String msg = json.getString("msg");
         Object data = json.get("data");
         if (code == null || code != 200) {
            return CommonResult.error(msg != null ? msg : "上游业务失败");
         } else {
            return data != null ? CommonResult.data(data) : CommonResult.ok(msg != null ? msg : "操作成功");
         }
      } catch (Exception var6) {
         log.error("解析上游响应失败: {}", respBody, var6);
         return CommonResult.error("解析上游响应失败：" + var6.getMessage());
      }
   }
}

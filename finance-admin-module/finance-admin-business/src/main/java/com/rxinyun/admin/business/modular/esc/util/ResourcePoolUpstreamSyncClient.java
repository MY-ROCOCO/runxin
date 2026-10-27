package com.rxinyun.admin.business.modular.esc.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourcePoolUpstreamSyncClient {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolUpstreamSyncClient.class);
   @Resource
   private ResourcePoolApi resourcePoolApi;

   public JSONObject queryServerDetail(String resourcePoolId, String upstreamServerId, String poolSyncTarget) {
      if (StrUtil.isBlank(resourcePoolId)) {
         throw new CommonException("资源池ID未配置，无法同步上游数据", new Object[0]);
      } else if (StrUtil.isBlank(upstreamServerId)) {
         throw new CommonException("上游服务器ID为空，无法同步上游数据", new Object[0]);
      } else {
         BaseResourcePool pool = this.resourcePoolApi.getById(resourcePoolId);
         if (pool == null) {
            throw new CommonException("资源池不存在", new Object[0]);
         } else if (pool.getStatus() != null && pool.getStatus() == 0) {
            throw new CommonException("资源池已禁用", new Object[0]);
         } else if (StrUtil.isBlank(pool.getApiUrl())) {
            throw new CommonException("资源池API地址未配置", new Object[0]);
         } else {
            String requestUrl = this.buildUrl(pool.getApiUrl(), "api/resource-pool/queryServerDetail");
            JSONObject params = new JSONObject();
            params.put("upstreamServerId", upstreamServerId);
            if (StrUtil.isNotBlank(poolSyncTarget)) {
               params.put("poolSyncTarget", poolSyncTarget);
            }

            String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
            log.info("手动同步资源池上游服务器详情 url={} body={}", requestUrl, params.toJSONString());
            HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(requestUrl).header("Content-Type", "application/json"))
                     .header("X-API-Key", pool.getApiKey()))
                  .header("X-API-Secret", apiSecret))
               .body(params.toJSONString())
               .timeout(30000)
               .execute();
            if (!response.isOk()) {
               throw new CommonException("查询上游服务器详情失败，HTTP {}：{}", new Object[]{response.getStatus(), response.body()});
            } else {
               JSONObject responseJson = JSON.parseObject(response.body());
               Integer code = responseJson.getInteger("code");
               if (code != null && code == 200) {
                  JSONObject data = responseJson.getJSONObject("data");
                  if (data != null && !data.isEmpty()) {
                     if (StrUtil.isBlank(data.getString("upstreamServerId"))) {
                        data.put("upstreamServerId", upstreamServerId);
                     }

                     return data;
                  } else {
                     throw new CommonException("上游服务器详情为空", new Object[0]);
                  }
               } else {
                  throw new CommonException("查询上游服务器详情失败：{}", new Object[]{responseJson.getString("msg")});
               }
            }
         }
      }
   }

   private String buildUrl(String baseUrl, String path) {
      if (!baseUrl.endsWith("/")) {
         baseUrl = baseUrl + "/";
      }

      return baseUrl + path;
   }
}

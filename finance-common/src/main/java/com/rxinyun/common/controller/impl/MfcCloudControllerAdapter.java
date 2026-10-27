package com.rxinyun.common.controller.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.DiskSpace;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.common.qzsystem.entity.HostStrategy;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import com.rxinyun.common.qzsystem.entity.MasterStatusParam;
import com.rxinyun.common.qzsystem.entity.MfcCreateResult;
import com.rxinyun.common.qzsystem.entity.MfcIpItem;
import com.rxinyun.common.qzsystem.entity.SystemInfo;
import com.rxinyun.common.qzsystem.result.HostMonitorResult;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.OkHttpClient.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MfcCloudControllerAdapter implements CloudControllerAdapter {
   private static final Logger log = LoggerFactory.getLogger(MfcCloudControllerAdapter.class);
   private static final OkHttpClient HTTP_CLIENT = new Builder()
      .connectTimeout(30L, TimeUnit.SECONDS)
      .readTimeout(30L, TimeUnit.SECONDS)
      .writeTimeout(30L, TimeUnit.SECONDS)
      .build();
   private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

   @Override
   public String getGuardVersion() {
      return "2";
   }

   @Override
   public CommonResult getMasterStatus(MasterStatusParam param) {
      if (param == null || param.getMfcNodeId() == null || param.getMfcNodeId().isEmpty()) {
         return CommonResult.error("魔方云节点ID为空");
      } else if (param.getMfcLoginAccount() != null && param.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(param.getIp(), param.getApiPort());

         try {
            String accessToken = this.login(baseUrl, param.getMfcLoginAccount(), param.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String nodeUrl = baseUrl + "/v1/nodes/" + param.getMfcNodeId() + "/status";
               Request request = new okhttp3.Request.Builder().url(nodeUrl).get().addHeader("access-token", accessToken).build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var11;
               label78: {
                  CommonResult json;
                  try {
                     if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonx = JSON.parseObject(body);
                        Integer status = jsonx.getInteger("status");
                        if (status == null) {
                           status = 0;
                        }

                        SystemInfo info = new SystemInfo();
                        info.setStatus(status);
                        var11 = CommonResult.data(info);
                        break label78;
                     }

                     SystemInfo failInfo = new SystemInfo();
                     failInfo.setStatus(0);
                     json = CommonResult.data(failInfo);
                  } catch (Throwable var13) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var12) {
                           var13.addSuppressed(var12);
                        }
                     }

                     throw var13;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return json;
               }

               if (response != null) {
                  response.close();
               }

               return var11;
            } else {
               SystemInfo failInfo = new SystemInfo();
               failInfo.setStatus(0);
               return CommonResult.data(failInfo);
            }
         } catch (Exception var14) {
            log.warn("魔方云节点详情请求失败: baseUrl={}, e={}", baseUrl, var14.getMessage());
            SystemInfo failInfo = new SystemInfo();
            failInfo.setStatus(0);
            return CommonResult.data(failInfo);
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult getCompanyInfo(MasterStatusParam param) {
      if (param == null || param.getMfcNodeId() == null || param.getMfcNodeId().isEmpty()) {
         return CommonResult.error("魔方云节点ID为空");
      } else if (param.getMfcLoginAccount() != null && param.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(param.getIp(), param.getApiPort());

         try {
            String accessToken = this.login(baseUrl, param.getMfcLoginAccount(), param.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String nodeUrl = baseUrl + "/v1/nodes/" + param.getMfcNodeId() + "/status";
               Request request = new okhttp3.Request.Builder().url(nodeUrl).get().addHeader("access-token", accessToken).build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var10;
               label81: {
                  CommonResult body;
                  try {
                     if (response.isSuccessful() && response.body() != null) {
                        String bodyx = response.body().string();
                        JSONObject json = JSON.parseObject(bodyx);
                        SystemInfo info = this.mapMfcStatusToSystemInfo(json);
                        var10 = CommonResult.data(info);
                        break label81;
                     }

                     body = CommonResult.error("魔方云节点信息请求失败");
                  } catch (Throwable var12) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var11) {
                           var12.addSuppressed(var11);
                        }
                     }

                     throw var12;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return body;
               }

               if (response != null) {
                  response.close();
               }

               return var10;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var13) {
            log.warn("魔方云公司信息请求失败: baseUrl={}, e={}", baseUrl, var13.getMessage());
            return CommonResult.error(var13.getMessage() != null ? var13.getMessage() : "魔方云公司信息请求失败");
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult<List<MasterMonitoringItem>> getNetworkMonitoring(MasterStatusParam param) {
      if (param == null || param.getMfcNodeId() == null || param.getMfcNodeId().isEmpty()) {
         return CommonResult.error("魔方云节点ID为空");
      } else if (param.getMfcLoginAccount() != null && param.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(param.getIp(), param.getApiPort());

         try {
            String accessToken = this.login(baseUrl, param.getMfcLoginAccount(), param.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds?orderby=1&sort=desc&searchtype=0&node=" + param.getMfcNodeId() + "&list_type=all";
               Request request = new okhttp3.Request.Builder().url(url).get().addHeader("access-token", accessToken).build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var22;
               label134: {
                  CommonResult body;
                  try {
                     if (response.isSuccessful() && response.body() != null) {
                        String bodyx = response.body().string();
                        JSONArray dataArr = this.parseCloudsDataArray(bodyx);
                        List<MasterMonitoringItem> list = new ArrayList<>();
                        if (dataArr != null && !dataArr.isEmpty()) {
                           for (int i = 0; i < dataArr.size(); i++) {
                              if (dataArr.get(i) instanceof JSONObject item) {
                                 String instanceName = item.getString("hostname");
                                 String downloadBw = getStringFromItem(item, "current_in_bw", "currentInBw");
                                 if (downloadBw == null || downloadBw.isEmpty()) {
                                    Object inBw = item.get("in_bw");
                                    downloadBw = inBw != null ? String.valueOf(inBw) : "0bps";
                                 }

                                 String uploadBw = getStringFromItem(item, "current_out_bw", "currentOutBw");
                                 if (uploadBw == null || uploadBw.isEmpty()) {
                                    Object outBw = item.get("out_bw");
                                    uploadBw = outBw != null ? String.valueOf(outBw) : "0bps";
                                 }

                                 list.add(new MasterMonitoringItem(instanceName != null ? instanceName : "", uploadBw, downloadBw));
                              }
                           }
                        }

                        var22 = CommonResult.data(list);
                        break label134;
                     }

                     body = CommonResult.error("魔方云云主机列表请求失败");
                  } catch (Throwable var19) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var18) {
                           var19.addSuppressed(var18);
                        }
                     }

                     throw var19;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return body;
               }

               if (response != null) {
                  response.close();
               }

               return var22;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var20) {
            log.warn("魔方云云主机监控请求失败: baseUrl={}, e={}", baseUrl, var20.getMessage());
            return CommonResult.error(var20.getMessage() != null ? var20.getMessage() : "魔方云云主机监控请求失败");
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   private JSONArray parseCloudsDataArray(String body) {
      if (body != null && !body.isEmpty()) {
         String trimmed = body.trim();

         try {
            if (trimmed.startsWith("[")) {
               return JSON.parseArray(body);
            }

            Object parsed = JSON.parse(body);
            if (parsed instanceof JSONObject) {
               Object data = ((JSONObject)parsed).get("data");
               if (data instanceof JSONArray) {
                  return (JSONArray)data;
               }
            }
         } catch (Exception var5) {
            log.debug("parseCloudsDataArray 解析异常: {}", var5.getMessage());
         }

         return null;
      } else {
         return null;
      }
   }

   private SystemInfo mapMfcStatusToSystemInfo(JSONObject json) {
      SystemInfo info = new SystemInfo();
      Integer status = json.getInteger("status");
      info.setStatus(status != null ? status : 0);
      JSONObject cpuInfo = json.getJSONObject("cpu_info");
      if (cpuInfo != null) {
         Double usePercent = getDoubleFromItem(cpuInfo, "use_percent");
         if (usePercent != null) {
            info.setCpuUsage(round2(usePercent));
         }
      }

      JSONObject memoryObj = json.getJSONObject("memory");
      if (memoryObj != null) {
         Double totalGb = parseSizeToGb(memoryObj.getString("total_memory"));
         Double useGb = parseSizeToGb(memoryObj.getString("use_memory"));
         if (totalGb != null) {
            info.setMemory(round2(totalGb));
         }

         if (totalGb != null && useGb != null) {
            info.setFreeMemory(round2(Math.max(0.0, totalGb - useGb)));
         }
      }

      Object diskVal = json.get("disk");
      if (diskVal instanceof JSONArray diskArr) {
         if (!diskArr.isEmpty()) {
            info.setSpace(this.parseDiskList(diskArr));
         }
      } else if (diskVal instanceof JSONObject) {
         List<DiskSpace> list = new ArrayList<>();
         list.add(this.parseDiskItem((JSONObject)diskVal, 0));
         info.setSpace(list);
      }

      return info;
   }

   private List<DiskSpace> parseDiskList(JSONArray diskArr) {
      List<DiskSpace> list = new ArrayList<>();

      for (int i = 0; i < diskArr.size(); i++) {
         Object el = diskArr.get(i);
         if (el instanceof JSONObject) {
            list.add(this.parseDiskItem((JSONObject)el, i));
         }
      }

      return list;
   }

   private DiskSpace parseDiskItem(JSONObject item, int index) {
      DiskSpace ds = new DiskSpace();
      ds.setName(getStringFromItem(item, "name") != null ? getStringFromItem(item, "name") : "disk" + index);
      Double total = parseSizeToGb(item.getString("disk_total"));
      Double free = parseSizeToGb(item.getString("disk_free"));
      if (total != null) {
         ds.setSize(round2(total));
      }

      if (free != null) {
         ds.setFreeSpace(round2(free));
      }

      return ds;
   }

   private static Double round2(Double v) {
      return v == null ? null : Math.round(v * 100.0) / 100.0;
   }

   private static Double parseSizeToGb(String s) {
      if (s != null && !s.isEmpty()) {
         s = s.trim();
         Double num = parseDoubleFromString(s);
         if (num == null) {
            return null;
         } else {
            String lower = s.toLowerCase();
            if (lower.contains("mb")) {
               return num / 1024.0;
            } else {
               return lower.contains("tb") ? num * 1024.0 : num;
            }
         }
      } else {
         return null;
      }
   }

   private static Double parseDoubleFromString(String s) {
      if (s != null && !s.isEmpty()) {
         s = s.trim();

         try {
            return Double.parseDouble(s);
         } catch (NumberFormatException var6) {
            StringBuilder num = new StringBuilder();

            for (int i = 0; i < s.length(); i++) {
               char c = s.charAt(i);
               if (!Character.isDigit(c) && c != '.') {
                  if (num.length() > 0) {
                     break;
                  }
               } else {
                  num.append(c);
               }
            }

            if (num.length() > 0) {
               try {
                  return Double.parseDouble(num.toString());
               } catch (NumberFormatException var5) {
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private String buildBaseUrl(String ip, Integer apiPort) {
      if (ip != null && !ip.isEmpty()) {
         String trimmed = ip.trim();
         int displaySep = trimmed.indexOf(" | 节点:");
         if (displaySep > 0) {
            trimmed = trimmed.substring(0, displaySep).trim();
         }

         if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            int port = apiPort != null ? apiPort : 80;
            return "http://" + trimmed + ":" + port;
         } else {
            return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
         }
      } else {
         return "http://localhost:" + (apiPort != null ? apiPort : 80);
      }
   }

   private static Double getDoubleFromItem(JSONObject item, String... keys) {
      for (String k : keys) {
         Object v = item.get(k);
         if (v != null) {
            if (v instanceof Number) {
               return ((Number)v).doubleValue();
            }

            try {
               return Double.parseDouble(String.valueOf(v));
            } catch (Exception var8) {
            }
         }
      }

      return null;
   }

   private static String getStringFromItem(JSONObject item, String... keys) {
      for (String k : keys) {
         String v = item.getString(k);
         if (v != null && !v.isEmpty()) {
            return v;
         }
      }

      return null;
   }

   private String parseAccessToken(String respBody) {
      if (respBody != null && !respBody.isEmpty()) {
         String trimmed = respBody.trim();
         if (trimmed.startsWith("{")) {
            try {
               if (JSON.parse(trimmed) instanceof JSONObject json) {
                  String token = json.getString("access-token");
                  if (token == null) {
                     token = json.getString("access_token");
                  }

                  if (token == null) {
                     JSONObject data = json.getJSONObject("data");
                     if (data != null) {
                        token = data.getString("access-token");
                        if (token == null) {
                           token = data.getString("access_token");
                        }
                     }
                  }

                  if (token == null) {
                     token = json.getString("data");
                  }

                  return token;
               }
            } catch (Exception var8) {
               log.debug("魔方云登录响应解析为JSON对象失败: {}", var8.getMessage());
            }
         }

         if (trimmed.startsWith("\"")) {
            try {
               Object parsed = JSON.parse(trimmed);
               if (parsed instanceof String) {
                  return (String)parsed;
               }
            } catch (Exception var7) {
               log.debug("魔方云登录响应解析为字符串失败: {}", var7.getMessage());
            }
         }

         log.warn("魔方云登录响应格式无法解析为 token，响应前缀: {}", trimmed.length() > 100 ? trimmed.substring(0, 100) + "..." : trimmed);
         return null;
      } else {
         return null;
      }
   }

   private String toLoginBaseUrl(String baseUrl) {
      if (baseUrl != null && !baseUrl.isEmpty()) {
         String s = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
         int last = s.lastIndexOf(47);
         if (last <= 0) {
            return baseUrl;
         } else {
            String lastSegment = s.substring(last + 1);
            return lastSegment != null && lastSegment.matches("\\d+") ? s.substring(0, last) : baseUrl;
         }
      } else {
         return baseUrl;
      }
   }

   private String login(String baseUrl, String username, String password) {
      String loginBase = this.toLoginBaseUrl(baseUrl);
      String url = loginBase + "/v1/login";
      JSONObject body = new JSONObject();
      body.put("username", username);
      body.put("password", password);

      try {
         Request request = new okhttp3.Request.Builder().url(url).post(RequestBody.create(body.toJSONString(), JSON_TYPE)).build();
         Response response = HTTP_CLIENT.newCall(request).execute();

         String var11;
         label67: {
            String token;
            label68: {
               String respBody;
               try {
                  if (response.isSuccessful() && response.body() != null) {
                     respBody = response.body().string();
                     if (respBody != null && !respBody.isEmpty()) {
                        token = this.parseAccessToken(respBody);
                        var11 = token;
                        break label67;
                     }

                     token = null;
                     break label68;
                  }

                  log.warn("魔方云登录失败: url={}, code={}", url, response.code());
                  respBody = null;
               } catch (Throwable var13) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                     }
                  }

                  throw var13;
               }

               if (response != null) {
                  response.close();
               }

               return respBody;
            }

            if (response != null) {
               response.close();
            }

            return token;
         }

         if (response != null) {
            response.close();
         }

         return var11;
      } catch (Exception var14) {
         log.warn("魔方云登录请求异常: url={}, e={}", url, var14.getMessage());
         return null;
      }
   }

   @Override
   public CommonResult openHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("创建参数为空");
      } else if (hostParam.getMfcNodeId() == null || hostParam.getMfcNodeId().isEmpty()) {
         return CommonResult.error("魔方云节点ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               JSONObject body = this.buildCreateCloudBody(hostParam);
               String url = apiBase + "/v1/clouds";
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var22;
               label205: {
                  CommonResult var23;
                  label206: {
                     CommonResult var25;
                     label207: {
                        CommonResult var28;
                        label208: {
                           CommonResult var26;
                           try {
                              String respBody = response.body() != null ? response.body().string() : "";
                              if (!response.isSuccessful()) {
                                 log.warn("魔方云创建机器失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                                 var22 = CommonResult.error("魔方云创建失败: " + respBody);
                                 break label205;
                              }

                              JSONObject json = JSON.parseObject(respBody);
                              String apiError = json.getString("error");
                              if (apiError != null && !apiError.isEmpty()) {
                                 var23 = CommonResult.error("魔方云创建失败: " + apiError);
                                 break label206;
                              }

                              Integer code = json.getInteger("code");
                              if (code != null && code != 200) {
                                 String msg = json.getString("msg");
                                 var25 = CommonResult.error(msg != null ? msg : "创建失败");
                                 break label207;
                              }

                              String instanceId = null;
                              Object dataObj = json.get("data");
                              if (dataObj != null) {
                                 if (dataObj instanceof JSONObject) {
                                    Object idObj = ((JSONObject)dataObj).get("id");
                                    if (idObj != null) {
                                       instanceId = String.valueOf(idObj);
                                    }
                                 } else {
                                    instanceId = String.valueOf(dataObj);
                                 }
                              }

                              if ((instanceId == null || instanceId.isEmpty() || "null".equals(instanceId)) && json.get("id") != null) {
                                 instanceId = String.valueOf(json.get("id"));
                              }

                              if (instanceId != null && !instanceId.isEmpty() && !"null".equals(instanceId)) {
                                 MfcCreateResult createResult = null;

                                 for (int retry = 0; retry < 3; retry++) {
                                    createResult = this.getCloudDetail(apiBase, accessToken, instanceId);
                                    if (createResult != null) {
                                       createResult.setInstanceId(instanceId);
                                       boolean hasPublicIp = createResult.getMainip() != null && !createResult.getMainip().isEmpty()
                                          || createResult.getIpList() != null && !createResult.getIpList().isEmpty();
                                       if (hasPublicIp || retry >= 2) {
                                          break;
                                       }
                                    }

                                    if (retry < 2) {
                                       try {
                                          Thread.sleep(2000L);
                                       } catch (InterruptedException var19) {
                                          Thread.currentThread().interrupt();
                                          break;
                                       }
                                    }
                                 }

                                 if (createResult == null) {
                                    createResult = new MfcCreateResult(instanceId, null, null, null, null);
                                 }

                                 var28 = CommonResult.data(createResult);
                                 break label208;
                              }

                              var26 = CommonResult.error("魔方云创建返回无实例 id");
                           } catch (Throwable var20) {
                              if (response != null) {
                                 try {
                                    response.close();
                                 } catch (Throwable var18) {
                                    var20.addSuppressed(var18);
                                 }
                              }

                              throw var20;
                           }

                           if (response != null) {
                              response.close();
                           }

                           return var26;
                        }

                        if (response != null) {
                           response.close();
                        }

                        return var28;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var25;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var23;
               }

               if (response != null) {
                  response.close();
               }

               return var22;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var21) {
            log.warn("魔方云创建机器异常: baseUrl={}, e={}", baseUrl, var21.getMessage());
            return CommonResult.error("魔方云创建异常: " + var21.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   private MfcCreateResult getCloudDetail(String baseUrl, String accessToken, String instanceId) {
      String url = baseUrl + "/v1/clouds/" + instanceId;

      try {
         Request request = new okhttp3.Request.Builder().url(url).get().addHeader("access-token", accessToken).build();
         Response response = HTTP_CLIENT.newCall(request).execute();

         Object var28;
         label274: {
            MfcCreateResult var36;
            label275: {
               Object body;
               try {
                  if (response.isSuccessful() && response.body() != null) {
                     String bodyx = response.body().string();
                     log.info("魔方云实例详情原始响应 instanceId={}, body={}", instanceId, bodyx);
                     JSONObject json = JSON.parseObject(bodyx);
                     Integer code = json.getInteger("code");
                     if (code != null && code != 200) {
                        var28 = null;
                        break label274;
                     }

                     JSONObject data = json.getJSONObject("data");
                     if (data == null) {
                        data = json;
                     }

                     String mainip = data.getString("mainip");
                     if (mainip == null || mainip.isEmpty()) {
                        mainip = data.getString("main_ip");
                     }

                     if (mainip == null || mainip.isEmpty()) {
                        mainip = data.getString("public_ip");
                     }

                     if (mainip == null || mainip.isEmpty()) {
                        mainip = data.getString("ip");
                     }

                     String gateway = data.getString("gateway");
                     if (gateway == null) {
                        gateway = data.getString("public_gateway");
                     }

                     String subnetMask = data.getString("subnet_mask");
                     if (subnetMask == null) {
                        subnetMask = data.getString("subnetMask");
                     }

                     if (subnetMask == null) {
                        subnetMask = data.getString("public_netmask");
                     }

                     if (gateway == null || gateway.isEmpty() || subnetMask == null || subnetMask.isEmpty()) {
                        JSONObject bwGroup = data.getJSONObject("default_bw_group");
                        if (bwGroup != null) {
                           JSONArray bwIpArr = bwGroup.getJSONArray("ip");
                           if (bwIpArr != null && !bwIpArr.isEmpty()) {
                              JSONObject first = bwIpArr.getJSONObject(0);
                              if (gateway == null || gateway.isEmpty()) {
                                 gateway = first.getString("gateway");
                              }

                              if (subnetMask == null || subnetMask.isEmpty()) {
                                 subnetMask = first.getString("subnet_mask");
                              }

                              if (subnetMask == null) {
                                 subnetMask = first.getString("subnetMask");
                              }
                           }
                        }
                     }

                     if (gateway == null || gateway.isEmpty() || subnetMask == null || subnetMask.isEmpty()) {
                        JSONArray netArr = data.getJSONArray("network");
                        if (netArr != null && !netArr.isEmpty()) {
                           JSONObject net0 = netArr.getJSONObject(0);
                           JSONArray netIpArr = net0 != null ? net0.getJSONArray("ipaddress") : null;
                           if (netIpArr != null && !netIpArr.isEmpty()) {
                              JSONObject firstx = netIpArr.getJSONObject(0);
                              if (gateway == null || gateway.isEmpty()) {
                                 gateway = firstx.getString("gateway");
                              }

                              if (subnetMask == null || subnetMask.isEmpty()) {
                                 subnetMask = firstx.getString("subnet_mask");
                              }

                              if (subnetMask == null) {
                                 subnetMask = firstx.getString("subnetMask");
                              }
                           }
                        }
                     }

                     String fallbackGw = gateway != null && !gateway.isEmpty() ? gateway : "";
                     String fallbackSm = subnetMask != null && !subnetMask.isEmpty() ? subnetMask : "";
                     List<MfcIpItem> ipList = new ArrayList<>();
                     JSONArray ipArr = data.getJSONArray("ipaddress");
                     if (ipArr == null) {
                        ipArr = data.getJSONArray("ip");
                     }

                     if (ipArr == null) {
                        ipArr = data.getJSONArray("ips");
                     }

                     if (ipArr != null) {
                        for (int i = 0; i < ipArr.size(); i++) {
                           JSONObject item = ipArr.getJSONObject(i);
                           if (item != null) {
                              String ipaddress = item.getString("ipaddress");
                              if (ipaddress == null) {
                                 ipaddress = item.getString("ip");
                              }

                              String gw = item.getString("gateway");
                              String sm = item.getString("subnet_mask");
                              if (sm == null) {
                                 sm = item.getString("subnetMask");
                              }

                              Integer id = item.getInteger("id");
                              ipList.add(
                                 new MfcIpItem(id, ipaddress, gw != null && !gw.isEmpty() ? gw : fallbackGw, sm != null && !sm.isEmpty() ? sm : fallbackSm)
                              );
                           }
                        }
                     }

                     if (mainip != null && !mainip.isEmpty() && ipList.isEmpty()) {
                        ipList.add(new MfcIpItem(null, mainip, fallbackGw, fallbackSm));
                     }

                     if (mainip == null || mainip.isEmpty()) {
                        log.debug("魔方云实例详情无公网IP: instanceId={}, 可能仍在分配中", instanceId);
                     }

                     var36 = new MfcCreateResult(instanceId, mainip, fallbackGw, fallbackSm, ipList);
                     break label275;
                  }

                  log.warn("魔方云实例详情请求失败: url={}, code={}", url, response.code());
                  body = null;
               } catch (Throwable var25) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var24) {
                        var25.addSuppressed(var24);
                     }
                  }

                  throw var25;
               }

               if (response != null) {
                  response.close();
               }

               return (MfcCreateResult)body;
            }

            if (response != null) {
               response.close();
            }

            return var36;
         }

         if (response != null) {
            response.close();
         }

         return (MfcCreateResult)var28;
      } catch (Exception var26) {
         log.warn("魔方云实例详情请求异常: url={}, e={}", url, var26.getMessage());
         return null;
      }
   }

   private JSONObject buildCreateCloudBody(HostParam p) {
      JSONObject body = new JSONObject();
      body.put("node", p.getMfcNodeId());
      body.put("client", 1);
      body.put("os", p.getMfcSystemId() != null ? p.getMfcSystemId() : "");
      body.put("rootpass", p.getSystemPassword() != null ? p.getSystemPassword() : "");
      body.put("hostname", p.getHostName() != null ? p.getHostName() : "");
      body.put("cpu", p.getCpu() != null ? p.getCpu() : 1);
      int memoryMb = 512;
      if (p.getRam() != null) {
         memoryMb = p.getRam() < 128 ? p.getRam() * 1024 : p.getRam();
      }

      body.put("memory", memoryMb);
      int systemDiskG = p.getOsDisk() != null && p.getOsDisk() > 0 ? p.getOsDisk() : 40;
      if (systemDiskG < 27) {
         systemDiskG = 27;
      }

      body.put("system_disk_size", systemDiskG);
      body.put("network_type", "normal");
      body.put("in_bw", p.getBandwidthIn() != null ? p.getBandwidthIn() : 0);
      body.put("out_bw", p.getBandwidth() != null ? p.getBandwidth() : 0);
      body.put("ip_num", p.getIpNum() != null && p.getIpNum() > 0 ? p.getIpNum() : 1);
      body.put("snap_num", 0);
      body.put("backup_num", 0);
      body.put("system_disk_io_limit", p.getOsMaxIops() != null ? p.getOsMaxIops() : 0);
      body.put("data_disk_io_limit", p.getDisksMaxIops() != null ? p.getDisksMaxIops() : 0);
      body.put("cpu_limit", p.getCpuLimit() != null ? p.getCpuLimit() : 0);
      body.put("port", p.getPort() != null ? p.getPort() : 22);
      boolean multiDisk = p.getData1DiskSize() != null && p.getData1DiskSize() > 0
         || p.getData2DiskSize() != null && p.getData2DiskSize() > 0
         || p.getData3DiskSize() != null && p.getData3DiskSize() > 0;
      if (multiDisk) {
         if (p.getData1DiskSize() != null && p.getData1DiskSize() > 0) {
            body.put("data1_disk_size", p.getData1DiskSize());
         }

         if (p.getData2DiskSize() != null && p.getData2DiskSize() > 0) {
            body.put("data2_disk_size", p.getData2DiskSize());
         }

         if (p.getData3DiskSize() != null && p.getData3DiskSize() > 0) {
            body.put("data3_disk_size", p.getData3DiskSize());
         }
      } else {
         body.put("data_disk_size", p.getHardDisks() != null ? p.getHardDisks() : 0);
      }

      return body;
   }

   @Override
   public CommonResult deleteHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "?force=0&delete_data_only=0";
               Request request = new okhttp3.Request.Builder().url(url).delete().addHeader("access-token", accessToken).build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var15;
               label104: {
                  CommonResult var17;
                  label105: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云删除实例失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var15 = CommonResult.error("魔方云删除失败: " + respBody);
                           break label104;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var12) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var17 = CommonResult.error("魔方云删除失败: " + apiError);
                           break label105;
                        }

                        var17 = CommonResult.ok();
                     } catch (Throwable var13) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var11) {
                              var13.addSuppressed(var11);
                           }
                        }

                        throw var13;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var17;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var17;
               }

               if (response != null) {
                  response.close();
               }

               return var15;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var14) {
            log.warn("魔方云删除实例异常: baseUrl={}, e={}", baseUrl, var14.getMessage());
            return CommonResult.error("魔方云删除异常: " + var14.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult startHost(HostParam hostParam) {
      return this.mfcCloudAction(hostParam, "on", "开机");
   }

   @Override
   public CommonResult closeHost(HostParam hostParam) {
      return this.mfcCloudAction(hostParam, "off", "关机");
   }

   @Override
   public CommonResult restartHost(HostParam hostParam) {
      return this.mfcCloudAction(hostParam, "reboot", "重启");
   }

   private CommonResult mfcCloudAction(HostParam hostParam, String action, String actionName) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String path = "on".equals(action) ? "on" : ("off".equals(action) ? "off" : "reboot");
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/" + path;
               RequestBody body = "reboot".equals(action) ? RequestBody.create("{}", JSON_TYPE) : RequestBody.create("{}", JSON_TYPE);
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .post(body)
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var19;
               label117: {
                  CommonResult var21;
                  label118: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云{}失败: url={}, code={}, body={}", new Object[]{actionName, url, response.code(), respBody});
                           var19 = CommonResult.error("魔方云" + actionName + "失败: " + respBody);
                           break label117;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var16) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var21 = CommonResult.error("魔方云" + actionName + "失败: " + apiError);
                           break label118;
                        }

                        var21 = CommonResult.ok();
                     } catch (Throwable var17) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var15) {
                              var17.addSuppressed(var15);
                           }
                        }

                        throw var17;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var21;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var21;
               }

               if (response != null) {
                  response.close();
               }

               return var19;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var18) {
            log.warn("魔方云{}异常: baseUrl={}, e={}", new Object[]{actionName, baseUrl, var18.getMessage()});
            return CommonResult.error("魔方云" + actionName + "异常: " + var18.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult reinstallHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() == null || hostParam.getMfcLoginPassword() == null) {
         return CommonResult.error("魔方云登录账号或密码为空");
      } else if (hostParam.getMfcSystemId() == null || hostParam.getMfcSystemId().isEmpty()) {
         return CommonResult.error("魔方云系统ID(镜像)为空，请选择支持魔方云的操作系统版本");
      } else if (hostParam.getSystemPassword() != null && !hostParam.getSystemPassword().isEmpty()) {
         int port = hostParam.getPort() != null && hostParam.getPort() > 0 ? hostParam.getPort() : 22;
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/reinstall";
               JSONObject body = new JSONObject();
               body.put("os", hostParam.getMfcSystemId());
               body.put("password", hostParam.getSystemPassword());
               body.put("port", port);
               body.put("format_data_disk", this.normalizeFormatDataDisk(hostParam.getFormatDataDisk()));
               body.put("password_type", 0);
               body.put("ssh_key", 0);
               body.put("image_client_hidden", 0);
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .put(RequestBody.create(body.toJSONString(), JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var17;
               label126: {
                  CommonResult var19;
                  label127: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云重装系统失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var17 = CommonResult.error("魔方云重装系统失败: " + respBody);
                           break label126;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var14) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var19 = CommonResult.error("魔方云重装系统失败: " + apiError);
                           break label127;
                        }

                        var19 = CommonResult.ok();
                     } catch (Throwable var15) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var13) {
                              var15.addSuppressed(var13);
                           }
                        }

                        throw var15;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var19;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var19;
               }

               if (response != null) {
                  response.close();
               }

               return var17;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var16) {
            log.warn("魔方云重装系统异常: baseUrl={}, e={}", baseUrl, var16.getMessage());
            return CommonResult.error("魔方云重装系统异常: " + var16.getMessage());
         }
      } else {
         return CommonResult.error("系统密码为空");
      }
   }

   private int normalizeFormatDataDisk(Integer formatDataDisk) {
      return Integer.valueOf(1).equals(formatDataDisk) ? 1 : 0;
   }

   @Override
   public CommonResult rebuildHost(HostParam hostParam) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 rebuildHost");
   }

   @Override
   public CommonResult updatePasswordHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() == null || hostParam.getMfcLoginPassword() == null) {
         return CommonResult.error("魔方云登录账号或密码为空");
      } else if (hostParam.getSystemPassword() != null && !hostParam.getSystemPassword().isEmpty()) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/password";
               JSONObject body = new JSONObject();
               body.put("password", hostParam.getSystemPassword());
               body.put("password_type", 0);
               body.put("ssh_key", 0);
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .put(RequestBody.create(body.toJSONString(), JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var16;
               label110: {
                  CommonResult var18;
                  label111: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云修改密码失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var16 = CommonResult.error("魔方云修改密码失败: " + respBody);
                           break label110;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var13) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var18 = CommonResult.error("魔方云修改密码失败: " + apiError);
                           break label111;
                        }

                        var18 = CommonResult.ok();
                     } catch (Throwable var14) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var12) {
                              var14.addSuppressed(var12);
                           }
                        }

                        throw var14;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var18;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var18;
               }

               if (response != null) {
                  response.close();
               }

               return var16;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var15) {
            log.warn("魔方云修改密码异常: baseUrl={}, e={}", baseUrl, var15.getMessage());
            return CommonResult.error("魔方云修改密码异常: " + var15.getMessage());
         }
      } else {
         return CommonResult.error("新密码为空");
      }
   }

   @Override
   public CommonResult setNetworkStateHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getDisconnect() != null && hostParam.getDisconnect() == 2) {
         return CommonResult.error("魔方云断网暂未实现");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/internet";
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .post(RequestBody.create("{}", JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var15;
               label110: {
                  CommonResult var17;
                  label111: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云恢复网络失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var15 = CommonResult.error("魔方云恢复网络失败: " + respBody);
                           break label110;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var12) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var17 = CommonResult.error("魔方云恢复网络失败: " + apiError);
                           break label111;
                        }

                        var17 = CommonResult.ok();
                     } catch (Throwable var13) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var11) {
                              var13.addSuppressed(var11);
                           }
                        }

                        throw var13;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var17;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var17;
               }

               if (response != null) {
                  response.close();
               }

               return var15;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var14) {
            log.warn("魔方云恢复网络异常: baseUrl={}, e={}", baseUrl, var14.getMessage());
            return CommonResult.error("魔方云恢复网络异常: " + var14.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult powerHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/hardoff";
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .post(RequestBody.create("{}", JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var15;
               label104: {
                  CommonResult var17;
                  label105: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云断开电源失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var15 = CommonResult.error("魔方云断开电源失败: " + respBody);
                           break label104;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var12) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var17 = CommonResult.error("魔方云断开电源失败: " + apiError);
                           break label105;
                        }

                        var17 = CommonResult.ok();
                     } catch (Throwable var13) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var11) {
                              var13.addSuppressed(var11);
                           }
                        }

                        throw var13;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var17;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var17;
               }

               if (response != null) {
                  response.close();
               }

               return var15;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var14) {
            log.warn("魔方云断开电源异常: baseUrl={}, e={}", baseUrl, var14.getMessage());
            return CommonResult.error("魔方云断开电源异常: " + var14.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult updateBandwidthHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         Integer downBw = hostParam.getBandwidthIn();
         Integer upBw = hostParam.getBandwidth();
         if (downBw != null && downBw > 0 || upBw != null && upBw > 0) {
            String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

            try {
               String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
               if (accessToken != null && !accessToken.isEmpty()) {
                  String apiBase = this.toLoginBaseUrl(baseUrl);
                  String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/bw";
                  JSONObject body = new JSONObject();
                  body.put("in_bw", downBw != null ? downBw : 0);
                  body.put("out_bw", upBw != null ? upBw : 0);
                  Request request = new okhttp3.Request.Builder()
                     .url(url)
                     .put(RequestBody.create(body.toJSONString(), JSON_TYPE))
                     .addHeader("access-token", accessToken)
                     .addHeader("Content-Type", "application/json")
                     .build();
                  Response response = HTTP_CLIENT.newCall(request).execute();

                  CommonResult var18;
                  label132: {
                     CommonResult var20;
                     label133: {
                        try {
                           String respBody = response.body() != null ? response.body().string() : "";
                           if (!response.isSuccessful()) {
                              log.warn("魔方云升级带宽失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                              var18 = CommonResult.error("魔方云升级带宽失败: " + respBody);
                              break label132;
                           }

                           String apiError = null;

                           try {
                              JSONObject json = JSON.parseObject(respBody);
                              apiError = json.getString("error");
                           } catch (Exception var15) {
                           }

                           if (apiError != null && !apiError.isEmpty()) {
                              var20 = CommonResult.error("魔方云升级带宽失败: " + apiError);
                              break label133;
                           }

                           var20 = CommonResult.ok();
                        } catch (Throwable var16) {
                           if (response != null) {
                              try {
                                 response.close();
                              } catch (Throwable var14) {
                                 var16.addSuppressed(var14);
                              }
                           }

                           throw var16;
                        }

                        if (response != null) {
                           response.close();
                        }

                        return var20;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var20;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var18;
               } else {
                  return CommonResult.error("魔方云登录失败");
               }
            } catch (Exception var17) {
               log.warn("魔方云升级带宽异常: baseUrl={}, e={}", baseUrl, var17.getMessage());
               return CommonResult.error("魔方云升级带宽异常: " + var17.getMessage());
            }
         } else {
            return CommonResult.error("带宽参数为空");
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult updateHardDiskHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         Integer sizeG = hostParam.getHardDisks() != null ? hostParam.getHardDisks() : hostParam.getOsDisk();
         if (sizeG != null && sizeG >= 1 && sizeG <= 1048576) {
            String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

            try {
               String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
               if (accessToken != null && !accessToken.isEmpty()) {
                  String apiBase = this.toLoginBaseUrl(baseUrl);
                  String url = apiBase + "/v1/disks/" + hostParam.getMfcCloudId();
                  JSONObject body = new JSONObject();
                  body.put("size", sizeG);
                  Request request = new okhttp3.Request.Builder()
                     .url(url)
                     .put(RequestBody.create(body.toJSONString(), JSON_TYPE))
                     .addHeader("access-token", accessToken)
                     .addHeader("Content-Type", "application/json")
                     .build();
                  Response response = HTTP_CLIENT.newCall(request).execute();

                  CommonResult var17;
                  label115: {
                     CommonResult var18;
                     label116: {
                        try {
                           String respBody = response.body() != null ? response.body().string() : "";
                           if (!response.isSuccessful()) {
                              log.warn("魔方云升级磁盘失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                              var17 = CommonResult.error("魔方云升级磁盘失败: " + respBody);
                              break label115;
                           }

                           JSONObject json = JSON.parseObject(respBody);
                           String apiError = json != null ? json.getString("error") : null;
                           if (apiError != null && !apiError.isEmpty()) {
                              var18 = CommonResult.error("魔方云升级磁盘失败: " + apiError);
                              break label116;
                           }

                           var18 = CommonResult.ok();
                        } catch (Throwable var15) {
                           if (response != null) {
                              try {
                                 response.close();
                              } catch (Throwable var14) {
                                 var15.addSuppressed(var14);
                              }
                           }

                           throw var15;
                        }

                        if (response != null) {
                           response.close();
                        }

                        return var18;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var18;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var17;
               } else {
                  return CommonResult.error("魔方云登录失败");
               }
            } catch (Exception var16) {
               log.warn("魔方云升级磁盘异常: baseUrl={}, e={}", baseUrl, var16.getMessage());
               return CommonResult.error("魔方云升级磁盘异常: " + var16.getMessage());
            }
         } else {
            return CommonResult.error("磁盘大小无效，需在 1-1048576(GB) 范围内");
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult updateCpuRamHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() == null || hostParam.getMfcLoginPassword() == null) {
         return CommonResult.error("魔方云登录账号或密码为空");
      } else if (hostParam.getCpu() != null && hostParam.getRam() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               int memoryMb = hostParam.getRam() < 128 ? hostParam.getRam() * 1024 : hostParam.getRam();
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId();
               JSONObject body = new JSONObject();
               body.put("cpu", hostParam.getCpu());
               body.put("memory", memoryMb);
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .put(RequestBody.create(body.toJSONString(), JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var17;
               label115: {
                  CommonResult var19;
                  label116: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云升级配置失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var17 = CommonResult.error("魔方云升级配置失败: " + respBody);
                           break label115;
                        }

                        String apiError = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                        } catch (Exception var14) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var19 = CommonResult.error("魔方云升级配置失败: " + apiError);
                           break label116;
                        }

                        var19 = CommonResult.ok();
                     } catch (Throwable var15) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var13) {
                              var15.addSuppressed(var13);
                           }
                        }

                        throw var15;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var19;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var19;
               }

               if (response != null) {
                  response.close();
               }

               return var17;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var16) {
            log.warn("魔方云升级配置异常: baseUrl={}, e={}", baseUrl, var16.getMessage());
            return CommonResult.error("魔方云升级配置异常: " + var16.getMessage());
         }
      } else {
         return CommonResult.error("CPU或内存参数为空");
      }
   }

   @Override
   public CommonResult monitorHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/real_data";
               JSONObject body = new JSONObject();
               JSONArray idArr = new JSONArray();
               idArr.add(hostParam.getMfcCloudId());
               body.put("id", idArr);
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var30;
               label235: {
                  CommonResult var32;
                  label236: {
                     CommonResult var23;
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        log.info(
                           "魔方云 real_data 原始响应: mfcCloudId={}, body={}",
                           hostParam.getMfcCloudId(),
                           respBody != null && respBody.length() > 500 ? respBody.substring(0, 500) + "..." : respBody
                        );
                        if (!response.isSuccessful()) {
                           log.warn("魔方云获取监控数据失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var30 = CommonResult.error("魔方云获取监控数据失败: " + respBody);
                           break label235;
                        }

                        String apiError = null;
                        JSONArray list = null;

                        try {
                           String trimmed = respBody != null ? respBody.trim() : "";
                           if (trimmed.startsWith("[")) {
                              list = JSON.parseArray(respBody);
                           } else {
                              JSONObject json = JSON.parseObject(respBody);
                              apiError = json.getString("error");
                              if (apiError == null || apiError.isEmpty()) {
                                 list = json.getJSONArray("data");
                                 if (list == null) {
                                    list = json.getJSONArray("list");
                                 }

                                 if (list == null && json.containsKey("id")) {
                                    list = new JSONArray().fluentAdd(json);
                                 }
                              }
                           }
                        } catch (Exception var27) {
                           log.warn("魔方云 real_data 解析异常: respBody={}, e={}", respBody, var27.getMessage());
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var32 = CommonResult.error("魔方云获取监控数据失败: " + apiError);
                           break label236;
                        }

                        JSONObject item = null;
                        String mfcIdStr = String.valueOf(hostParam.getMfcCloudId());
                        if (list != null && !list.isEmpty()) {
                           for (int i = 0; i < list.size(); i++) {
                              JSONObject o = list.getJSONObject(i);
                              if (o != null) {
                                 Object idObj = o.get("id");
                                 String id = idObj != null ? String.valueOf(idObj) : null;
                                 if (mfcIdStr.equals(id != null ? id : "")) {
                                    item = o;
                                    if (o == null) {
                                       item = list.getJSONObject(0);
                                    }
                                    break;
                                 }
                              }
                           }
                        }

                        Double cpuUsage = item != null ? getDoubleFromItem(item, "cpu_usage", "cpuUsage") : null;
                        Number memoryUsage = null;
                        if (item != null) {
                           Object memObj = item.get("memory_usage");
                           if (memObj == null) {
                              memObj = item.get("memoryUsage");
                           }

                           if (memObj instanceof Number) {
                              memoryUsage = (Number)memObj;
                           } else if (memObj != null) {
                              try {
                                 memoryUsage = Double.parseDouble(String.valueOf(memObj));
                              } catch (Exception var26) {
                              }
                           }
                        }

                        String currentInBw = item != null ? getStringFromItem(item, "current_in_bw", "currentInBw") : null;
                        String currentOutBw = item != null ? getStringFromItem(item, "current_out_bw", "currentOutBw") : null;
                        int inBw = 0;
                        int outBw = 0;

                        try {
                           if (currentInBw != null && !currentInBw.isEmpty()) {
                              inBw = (int)Double.parseDouble(currentInBw.replaceAll("[^0-9.\\-]", ""));
                           }

                           if (currentOutBw != null && !currentOutBw.isEmpty()) {
                              outBw = (int)Double.parseDouble(currentOutBw.replaceAll("[^0-9.\\-]", ""));
                           }
                        } catch (Exception var25) {
                        }

                        HostMonitorResult vo = new HostMonitorResult();
                        vo.setCpuStats(cpuUsage != null ? cpuUsage : 0.0);
                        vo.setMemoryStats(memoryUsage != null ? memoryUsage.intValue() : 0);
                        HostMonitorResult.NetworkStats networkStats = new HostMonitorResult.NetworkStats();
                        networkStats.setBytesSentPersec(outBw);
                        networkStats.setBytesReceivedPersec(inBw);
                        vo.setNetworkStats(networkStats);
                        var23 = CommonResult.data(vo);
                     } catch (Throwable var28) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var24) {
                              var28.addSuppressed(var24);
                           }
                        }

                        throw var28;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var23;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var32;
               }

               if (response != null) {
                  response.close();
               }

               return var30;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var29) {
            log.warn("魔方云获取监控数据异常: baseUrl={}, e={}", baseUrl, var29.getMessage());
            return CommonResult.error("魔方云获取监控数据异常: " + var29.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult thumbnailHost(HostParam hostParam) {
      String placeholderSvg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"400\" height=\"260\"><rect fill=\"#e8e8e8\" width=\"400\" height=\"260\"/><text x=\"200\" y=\"125\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#999\" font-size=\"14\" font-family=\"sans-serif\">暂无图片</text></svg>";
      String dataUri = "data:image/svg+xml," + URLEncoder.encode(placeholderSvg, StandardCharsets.UTF_8).replace("+", "%20");
      Map<String, Object> data = new HashMap<>();
      data.put("url", dataUri);
      data.put("image", dataUri);
      return CommonResult.data(data);
   }

   @Override
   public CommonResult getStateHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/status";
               Request request = new okhttp3.Request.Builder().url(url).get().addHeader("access-token", accessToken).build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var18;
               label136: {
                  CommonResult var20;
                  label137: {
                     CommonResult var23;
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("魔方云获取实例状态失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                           var18 = CommonResult.error("魔方云获取实例状态失败: " + respBody);
                           break label136;
                        }

                        String apiError = null;
                        String status = null;

                        try {
                           JSONObject json = JSON.parseObject(respBody);
                           apiError = json.getString("error");
                           if (apiError == null || apiError.isEmpty()) {
                              JSONObject data = json.getJSONObject("data");
                              if (data == null) {
                                 data = json;
                              }

                              status = data.getString("status");
                           }
                        } catch (Exception var15) {
                        }

                        if (apiError != null && !apiError.isEmpty()) {
                           var20 = CommonResult.error("魔方云获取实例状态失败: " + apiError);
                           break label137;
                        }

                        String state = "Unknown";
                        if (status != null) {
                           String var21 = status.toLowerCase();
                           switch (var21) {
                              case "on":
                                 state = "Running";
                                 break;
                              case "off":
                                 state = "Stopped";
                                 break;
                              case "unknown":
                              default:
                                 state = "Unknown";
                           }
                        }

                        Map<String, Object> data = new HashMap<>();
                        data.put("state", state);
                        var23 = CommonResult.data(data);
                     } catch (Throwable var16) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var14) {
                              var16.addSuppressed(var14);
                           }
                        }

                        throw var16;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var23;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var20;
               }

               if (response != null) {
                  response.close();
               }

               return var18;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var17) {
            log.warn("魔方云获取实例状态异常: baseUrl={}, e={}", baseUrl, var17.getMessage());
            return CommonResult.error("魔方云获取实例状态异常: " + var17.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult guidHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("参数为空");
      } else if (hostParam.getMfcCloudId() == null || hostParam.getMfcCloudId().isEmpty()) {
         return CommonResult.error("魔方云实例ID为空");
      } else if (hostParam.getMfcLoginAccount() != null && hostParam.getMfcLoginPassword() != null) {
         String baseUrl = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort());

         try {
            String accessToken = this.login(baseUrl, hostParam.getMfcLoginAccount(), hostParam.getMfcLoginPassword());
            if (accessToken != null && !accessToken.isEmpty()) {
               String apiBase = this.toLoginBaseUrl(baseUrl);
               String url = apiBase + "/v1/clouds/" + hostParam.getMfcCloudId() + "/vnc";
               Request request = new okhttp3.Request.Builder()
                  .url(url)
                  .post(RequestBody.create("{}", JSON_TYPE))
                  .addHeader("access-token", accessToken)
                  .addHeader("Content-Type", "application/json")
                  .build();
               Response response = HTTP_CLIENT.newCall(request).execute();

               CommonResult var17;
               label130: {
                  CommonResult var20;
                  label131: {
                     CommonResult var21;
                     label132: {
                        try {
                           String respBody = response.body() != null ? response.body().string() : "";
                           if (!response.isSuccessful()) {
                              log.warn("魔方云获取VNC失败: url={}, code={}, body={}", new Object[]{url, response.code(), respBody});
                              var17 = CommonResult.error("魔方云获取VNC失败: " + respBody);
                              break label130;
                           }

                           String apiError = null;
                           String vncUrl = null;

                           try {
                              JSONObject json = JSON.parseObject(respBody);
                              apiError = json.getString("error");
                              if (apiError == null || apiError.isEmpty()) {
                                 JSONObject data = json.getJSONObject("data");
                                 if (data == null) {
                                    data = json;
                                 }

                                 vncUrl = data.getString("vnc_url");
                              }
                           } catch (Exception var14) {
                           }

                           if (apiError != null && !apiError.isEmpty()) {
                              var20 = CommonResult.error("魔方云获取VNC失败: " + apiError);
                              break label131;
                           }

                           if (vncUrl != null && !vncUrl.isEmpty()) {
                              Map<String, Object> data = new HashMap<>();
                              data.put("url", vncUrl);
                              var21 = CommonResult.data(data);
                              break label132;
                           }

                           var20 = CommonResult.error("魔方云未返回VNC地址");
                        } catch (Throwable var15) {
                           if (response != null) {
                              try {
                                 response.close();
                              } catch (Throwable var13) {
                                 var15.addSuppressed(var13);
                              }
                           }

                           throw var15;
                        }

                        if (response != null) {
                           response.close();
                        }

                        return var20;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var21;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var20;
               }

               if (response != null) {
                  response.close();
               }

               return var17;
            } else {
               return CommonResult.error("魔方云登录失败");
            }
         } catch (Exception var16) {
            log.warn("魔方云获取VNC异常: baseUrl={}, e={}", baseUrl, var16.getMessage());
            return CommonResult.error("魔方云获取VNC异常: " + var16.getMessage());
         }
      } else {
         return CommonResult.error("魔方云登录账号或密码为空");
      }
   }

   @Override
   public CommonResult batchSetIpHost(HostParam hostParam) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 batchSetIpHost");
   }

   @Override
   public CommonResult resetNetworkHost(HostParam hostParam) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 resetNetworkHost");
   }

   @Override
   public CommonResult resetIpHost(HostParam hostParam) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 resetIpHost");
   }

   @Override
   public CommonResult getNetworkHistory(HostParam hostParam) {
      return CommonResult.data(new ArrayList());
   }

   @Override
   public CommonResult getCpuHistory(HostParam hostParam) {
      return CommonResult.data(new ArrayList());
   }

   @Override
   public CommonResult createBackupHost(HostBackupParam hostBackupParam) {
      return CommonResult.error("魔方云暂不支持创建备份");
   }

   @Override
   public CommonResult restoreBackupHost(HostBackupParam hostBackupParam) {
      return CommonResult.error("魔方云暂不支持还原备份");
   }

   @Override
   public CommonResult removeBackupHost(HostBackupParam hostBackupParam) {
      return CommonResult.error("魔方云暂不支持删除备份");
   }

   @Override
   public CommonResult addFirewallHost(HostStrategy hostStrategy) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 addFirewallHost");
   }

   @Override
   public CommonResult removeFirewallHost(HostStrategy hostStrategy) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 removeFirewallHost");
   }

   @Override
   public CommonResult addForwardPort(HostPort hostPort) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 addForwardPort");
   }

   @Override
   public CommonResult removeForwardPort(HostPort hostPort) {
      throw new UnsupportedOperationException("魔方云受控暂未实现 removeForwardPort");
   }
}

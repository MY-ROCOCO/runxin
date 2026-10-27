package com.rxinyun.common.qzsystem.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostIp;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.common.qzsystem.entity.HostStrategy;
import com.rxinyun.common.qzsystem.entity.SystemInfo;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostOpenerUtil {
   private static final Logger log = LoggerFactory.getLogger(HostOpenerUtil.class);
   private static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(20L, TimeUnit.MINUTES).build();

   private static String resolveHypervOtherIp(HostParam hostParam) {
      if (StrUtil.isNotBlank(hostParam.getIpStr())) {
         return hostParam.getIpStr();
      } else {
         return hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()
            ? hostParam.getAttachIps().stream().map(HostIp::getIp).filter(CharSequenceUtil::isNotBlank).collect(Collectors.joining(","))
            : "";
      }
   }

   private static int resolveMinMemoryMb(HostParam hostParam) {
      if (hostParam != null && Integer.valueOf(1).equals(hostParam.getMemoryDynamic())) {
         Integer ramStart = hostParam.getRamStart();
         if (ramStart != null && ramStart > 0) {
            return ramStart >= 128 ? ramStart : ramStart * 1024;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public static CommonResult openHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         JSONObject post_data = new JSONObject();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("name", hostParam.getHostName());
         post_data.put("password", hostParam.getSystemPassword());
         post_data.put("template_path", hostParam.getImagePath());
         post_data.put("vhd_path", hostParam.getDisksPath());
         post_data.put("vcpus", hostParam.getCpu());
         post_data.put("cpu_limit", hostParam.getCpuLimit() != null ? hostParam.getCpuLimit().toString() : "100");
         post_data.put("os_type", hostParam.getOsClass());
         if (hostParam.getOsPath() != null) {
            post_data.put("os_vhd_path", hostParam.getOsPath());
         }

         post_data.put("max_memory_mb", hostParam.getRam() * 1024);
         post_data.put("min_memory_mb", resolveMinMemoryMb(hostParam));
         post_data.put("os_name", hostParam.getImageDirectoryName() + ".vhdx");
         post_data.put("os_size", hostParam.getOsDisk());
         post_data.put("os_min_iops", String.valueOf(hostParam.getOsMaxIops()));
         post_data.put("os_max_iops", String.valueOf(hostParam.getOsMaxIops()));
         post_data.put("max_iops", hostParam.getDisksMaxIops());
         post_data.put("min_iops", hostParam.getDisksMaxIops());
         post_data.put("data_size", hostParam.getHardDisks());
         if (hostParam.getRebuildFlag() == 1) {
            post_data.put("re_create", hostParam.getRebuildFlag());
         }

         post_data.put("vir_extensions", false);
         JSONObject network1 = new JSONObject();
         JSONObject network2 = new JSONObject();
         if (hostParam.getShareIp() != 1) {
            network1.put("ip", hostParam.getPublicIp());
            network1.put("gateway", hostParam.getPublicGateway());
            network1.put("netmask", hostParam.getPublicNetmask());
            network1.put("dns1", "114.114.114.114");
            network1.put("dns2", "8.8.8.8");
            network1.put("band_width", hostParam.getBandwidth());
            network1.put("mac", hostParam.getPublicMac());
            network1.put("switch_name", hostParam.getPublicSwitchName());
            network1.put("vlanid", hostParam.getPublicVlanId());
            network1.put("inlimit", hostParam.getBandwidthIn());
            if (hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()) {
               JSONArray attachIpsArray = new JSONArray();

               for (HostIp hostIp : hostParam.getAttachIps()) {
                  JSONObject attachIpObj = new JSONObject();
                  attachIpObj.put("ip", hostIp.getIp());
                  attachIpObj.put("mask", hostIp.getMask());
                  attachIpsArray.put(attachIpObj);
               }

               network1.put("attachip", attachIpsArray);
            } else {
               network1.put("attachip", new JSONArray());
            }

            network2.put("ip", hostParam.getPrivateIp());
            network2.put("gateway", "");
            network2.put("netmask", hostParam.getPrivateNetmask());
            network2.put("band_width", 0);
            network2.put("mac", hostParam.getPrivateMac());
            network2.put("switch_name", hostParam.getPrivateSwitchName());
            network2.put("vlanid", hostParam.getPrivateVlanId());
         } else {
            network1.put("ip", hostParam.getNatIp());
            network1.put("gateway", hostParam.getNatGateway());
            network1.put("netmask", hostParam.getNatNetmask());
            network1.put("dns1", "114.114.114.114");
            network1.put("dns2", "8.8.8.8");
            network1.put("band_width", hostParam.getBandwidth());
            network1.put("mac", hostParam.getNatMac());
            network1.put("switch_name", hostParam.getNatSwitchName());
            network1.put("vlanid", hostParam.getNatVlanId());
            network1.put("inlimit", hostParam.getBandwidthIn());
            if (hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()) {
               JSONArray attachIpsArray = new JSONArray();

               for (HostIp hostIp : hostParam.getAttachIps()) {
                  JSONObject attachIpObj = new JSONObject();
                  attachIpObj.put("ip", hostIp.getIp());
                  attachIpObj.put("mask", hostIp.getMask());
                  attachIpsArray.put(attachIpObj);
               }

               network1.put("attachip", attachIpsArray);
            } else {
               network1.put("attachip", new JSONArray());
            }

            network2.put("ip", hostParam.getPrivateIp());
            network2.put("gateway", StrUtil.blankToDefault(hostParam.getPrivateGateway(), ""));
            network2.put("netmask", hostParam.getPrivateNetmask());
            network2.put("band_width", 0);
            network2.put("mac", hostParam.getPrivateMac());
            network2.put("switch_name", hostParam.getPrivateSwitchName());
            network2.put("vlanid", hostParam.getPrivateVlanId());
         }

         JSONArray networks = new JSONArray();
         networks.put(network1);
         networks.put(network2);
         post_data.put("network", networks);
         post_data.put("otherip", resolveHypervOtherIp(hostParam));

         try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1200L)).build();
            log.info("创建请求参数：{}", post_data);
            JSONObject requestBody = new JSONObject();
            requestBody.put("data", post_data);
            requestBody.put("action", "create_vps");
            requestBody.put("callback_param", hostParam.getServerId());
            requestBody.put("state", "1");
            HttpRequest request = HttpRequest.newBuilder()
               .uri(new URI("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask"))
               .header("Content-Type", "application/json")
               .header("apikey", hostParam.getApiKey())
               .POST(BodyPublishers.ofString(requestBody.toString()))
               .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JSONObject result = JSON.parseObject(response.body());
            if (response.statusCode() != 200) {
               throw new CommonException(result.getString("msg"));
            } else {
               return CommonResult.data("创建命令已发送");
            }
         } catch (Exception var10) {
            log.error("请求失败:{}", var10);
            throw new CommonException("创建云服务器请求失败");
         }
      }
   }

   public static CommonResult updateHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("vcpus", hostParam.getCpu());
         post_data.put("cpu_limit", hostParam.getCpuLimit());
         post_data.put("max_memory_mb", hostParam.getRam() * 1024);
         post_data.put("min_memory_mb", resolveMinMemoryMb(hostParam));
         post_data.put("os_size", hostParam.getOsDisk());
         post_data.put("max_iops", hostParam.getDisksMaxIops());
         post_data.put("min_iops", hostParam.getDisksMaxIops());
         post_data.put("data_size", hostParam.getHardDisks());
         post_data.put("data_path", hostParam.getImageDirectoryName() + "_data1.vhdx");
         post_data.put("band_width", hostParam.getBandwidth());
         post_data.put("inlimit", hostParam.getBandwidthIn());
         post_data.put("os_min_iops", hostParam.getOsMaxIops());
         post_data.put("os_max_iops", hostParam.getOsMaxIops());
         post_data.put("vir_extensions", false);
         post_data.put("otherip", resolveHypervOtherIp(hostParam));

         try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("data", post_data);
            json.put("action", "update_vps");
            json.put("callback_param", hostParam.getServerId());
            json.put("state", "1");
            RequestBody body = RequestBody.create(JSON.toJSONString(json), MediaType.get("application/json; charset=utf-8"));
            Request request = new Builder()
               .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
               .header("Content-Type", "application/json")
               .header("apikey", hostParam.getApiKey())
               .post(body)
               .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
               return CommonResult.error("请求失败");
            } else {
               JSONObject result = JSON.parseObject(response.body().string());
               return result.getIntValue("code") != 200 ? CommonResult.ok(result.getString("msg")) : CommonResult.ok("升级命令已发送");
            }
         } catch (IOException var8) {
            log.info("请求失败:{}", var8);
            return CommonResult.error(var8.getMessage());
         }
      }
   }

   public static CommonResult deleteHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("back_dir", hostParam.getBackupPath());
         OkHttpClient client = new okhttp3.OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(120L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .build();
         JSONObject json = new JSONObject();
         json.put("action", "remove_vps");
         json.put("callback_param", hostParam.getServerId());
         json.put("state", "1");
         json.put("data", post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var16;
            label77: {
               CommonResult var17;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var16 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var17 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var17 = CommonResult.ok();
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

            return var16;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         } catch (Exception var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         }
      }
   }

   public static CommonResult monitorHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/MonitorHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label83: {
               CommonResult var18;
               label84: {
                  CommonResult var19;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label83;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label84;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     if (data != null && !data.containsKey("DiskStats")) {
                        Map<String, Object> diskStats = new HashMap<>();
                        diskStats.put("DiskUsedBytes", 0L);
                        diskStats.put("DiskUsedGB", 0.0);
                        diskStats.put("ReadBytes", 0L);
                        diskStats.put("WriteBytes", 0L);
                        diskStats.put("GetTime", "");
                        data.put("DiskStats", diskStats);
                     }

                     var19 = CommonResult.data(data);
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

                  return var19;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult thumbnailHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("width", 400);
         post_data.put("height", 260);
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/GetThumbnailImageHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  CommonResult var11;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var11 = CommonResult.data(data);
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

                  return var11;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult updateHardDiskHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("data_size", hostParam.getHardDisks());
         post_data.put("data_path", hostParam.getHostName() + "_data1.vhdx");
         post_data.put("os_size", hostParam.getOsDisk());

         try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("data", post_data);
            json.put("action", "update_vps");
            json.put("callback_param", hostParam.getServerId());
            json.put("state", "1");
            RequestBody body = RequestBody.create(JSON.toJSONString(json), MediaType.get("application/json; charset=utf-8"));
            Request request = new Builder()
               .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
               .header("Content-Type", "application/json")
               .header("apikey", hostParam.getApiKey())
               .post(body)
               .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
               return CommonResult.error("请求失败");
            } else {
               JSONObject result = JSON.parseObject(response.body().string());
               return result.getIntValue("code") != 200 ? CommonResult.ok(result.getString("msg")) : CommonResult.ok("升级命令已发送");
            }
         } catch (IOException var8) {
            log.info("请求失败:{}", var8);
            return CommonResult.error(var8.getMessage());
         }
      }
   }

   public static CommonResult updateBandwidthHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("band_width", hostParam.getBandwidth());

         try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("data", post_data);
            json.put("action", "update_vps");
            json.put("callback_param", hostParam.getServerId());
            json.put("state", "1");
            RequestBody body = RequestBody.create(JSON.toJSONString(json), MediaType.get("application/json; charset=utf-8"));
            Request request = new Builder()
               .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
               .header("Content-Type", "application/json")
               .header("apikey", hostParam.getApiKey())
               .post(body)
               .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
               return CommonResult.error("请求失败");
            } else {
               JSONObject result = JSON.parseObject(response.body().string());
               return result.getIntValue("code") != 200 ? CommonResult.ok(result.getString("msg")) : CommonResult.ok("升级命令已发送");
            }
         } catch (IOException var8) {
            log.info("请求失败:{}", var8);
            return CommonResult.error(var8.getMessage());
         }
      }
   }

   public static CommonResult updateCpuRamHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("vcpus", hostParam.getCpu());
         post_data.put("max_memory_mb", hostParam.getRam() * 1024);

         try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("data", post_data);
            json.put("action", "update_vps");
            json.put("callback_param", hostParam.getServerId());
            json.put("state", "1");
            RequestBody body = RequestBody.create(JSON.toJSONString(json), MediaType.get("application/json; charset=utf-8"));
            Request request = new Builder()
               .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
               .header("Content-Type", "application/json")
               .header("apikey", hostParam.getApiKey())
               .post(body)
               .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
               return CommonResult.error("请求失败");
            } else {
               JSONObject result = JSON.parseObject(response.body().string());
               return result.getIntValue("code") != 200 ? CommonResult.ok(result.getString("msg")) : CommonResult.ok("升级命令已发送");
            }
         } catch (IOException var8) {
            log.info("请求失败:{}", var8);
            return CommonResult.error(var8.getMessage());
         }
      }
   }

   public CommonResult setBiosHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetVmBootOrderHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var18;
            label77: {
               CommonResult var19;
               label78: {
                  CommonResult var12;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var18 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var19 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var12 = CommonResult.data(data);
                  } catch (Throwable var14) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var12;
               }

               if (response != null) {
                  response.close();
               }

               return var19;
            }

            if (response != null) {
               response.close();
            }

            return var18;
         } catch (IOException var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         } catch (Exception var16) {
            log.info("请求失败:{}", var16);
            return CommonResult.error(var16.getMessage());
         }
      }
   }

   public static CommonResult startHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("state", 2);
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetStateHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  CommonResult var11;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var11 = CommonResult.data(data);
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

                  return var11;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult closeHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("state", 4);
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetStateHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();
         log.info("关机请求：{}", request);

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  CommonResult var11;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var11 = CommonResult.data(data);
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

                  return var11;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult powerHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("state", 3);
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetStateHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  CommonResult var11;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var11 = CommonResult.data(data);
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

                  return var11;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult restartHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         CommonResult commonResult = getStateHost(hostParam);
         if (!commonResult.getCode().equals(200)) {
            return commonResult;
         } else if ("Stopped".equals(commonResult.getData())) {
            return startHost(hostParam);
         } else {
            Map<String, Object> post_data = new HashMap<>();
            post_data.put("vm_name", hostParam.getHostName());
            post_data.put("state", 11);
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.putAll(post_data);
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
            Request request = new Builder()
               .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetStateHyperV")
               .header("Content-Type", "application/json")
               .header("apikey", hostParam.getApiKey())
               .post(body)
               .build();

            try {
               Response response = client.newCall(request).execute();

               CommonResult var18;
               label85: {
                  CommonResult var19;
                  label86: {
                     CommonResult var12;
                     try {
                        int statusCode = response.code();
                        if (statusCode != 200) {
                           String responseString = response.body().string();
                           var18 = CommonResult.error(responseString);
                           break label85;
                        }

                        String responseString = response.body().string();
                        JSONObject result = JSON.parseObject(responseString);
                        if (result.getIntValue("code") != 200) {
                           var19 = CommonResult.error(result.getString("msg"));
                           break label86;
                        }

                        Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                        var12 = CommonResult.data(data);
                     } catch (Throwable var14) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var13) {
                              var14.addSuppressed(var13);
                           }
                        }

                        throw var14;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var12;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var19;
               }

               if (response != null) {
                  response.close();
               }

               return var18;
            } catch (IOException var15) {
               log.info("请求失败:{}", var15);
               return CommonResult.error(var15.getMessage());
            } catch (Exception var16) {
               log.info("请求失败:{}", var16);
               return CommonResult.error(var16.getMessage());
            }
         }
      }
   }

   public static CommonResult guidHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/GetVmGuidHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var16;
            label77: {
               CommonResult var17;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var16 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var17 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var17 = CommonResult.data(result.getJSONObject("data"));
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

            return var16;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         } catch (Exception var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         }
      }
   }

   public static CommonResult getStateHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
         Map<String, String> postData = Map.of("vm_name", hostParam.getHostName());
         String json = new Gson().toJson(postData);
         RequestBody body = RequestBody.create(mediaType, json);
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/GetVmState")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("apikey", hostParam.getApiKey())
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var14;
            label72: {
               CommonResult var15;
               label73: {
                  CommonResult var10;
                  try {
                     if (!response.isSuccessful()) {
                        var14 = CommonResult.error(response.body().string());
                        break label72;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var15 = CommonResult.error(result.getString("msg"));
                        break label73;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var10 = CommonResult.data(data);
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

                  return var10;
               }

               if (response != null) {
                  response.close();
               }

               return var15;
            }

            if (response != null) {
               response.close();
            }

            return var14;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         }
      }
   }

   public static CommonResult reinstallHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("name", hostParam.getHostName());
         post_data.put("password", hostParam.getSystemPassword());
         post_data.put("template_path", hostParam.getImagePath());
         post_data.put("vhd_path", hostParam.getDisksPath());
         post_data.put("os_type", hostParam.getOsClass());
         JSONObject network1 = new JSONObject();
         JSONObject network2 = new JSONObject();
         if (hostParam.getShareIp() != 1) {
            network1.put("ip", hostParam.getPublicIp());
            network1.put("gateway", hostParam.getPublicGateway());
            network1.put("netmask", hostParam.getPublicNetmask());
            network1.put("dns1", "114.114.114.114");
            network1.put("dns2", "8.8.8.8");
            network1.put("band_width", hostParam.getBandwidth());
            network1.put("mac", hostParam.getPublicMac());
            network1.put("switch_name", hostParam.getPublicSwitchName());
            network1.put("vlanid", hostParam.getPublicVlanId());
            network1.put("inlimit", hostParam.getBandwidthIn());
            if (hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()) {
               JSONArray attachIpsArray = new JSONArray();

               for (HostIp hostIp : hostParam.getAttachIps()) {
                  JSONObject attachIpObj = new JSONObject();
                  attachIpObj.put("ip", hostIp.getIp());
                  attachIpObj.put("mask", hostIp.getMask());
                  attachIpsArray.put(attachIpObj);
               }

               network1.put("attachip", attachIpsArray);
            } else {
               network1.put("attachip", new JSONArray());
            }

            network2.put("ip", hostParam.getPrivateIp());
            network2.put("gateway", "");
            network2.put("netmask", hostParam.getPrivateNetmask());
            network2.put("band_width", 0);
            network2.put("mac", hostParam.getPrivateMac());
            network2.put("switch_name", hostParam.getPrivateSwitchName());
         } else {
            network1.put("ip", hostParam.getNatIp());
            network1.put("gateway", hostParam.getNatGateway());
            network1.put("netmask", hostParam.getNatNetmask());
            network1.put("dns1", "114.114.114.114");
            network1.put("dns2", "8.8.8.8");
            network1.put("band_width", hostParam.getBandwidth());
            network1.put("mac", hostParam.getNatMac());
            network1.put("switch_name", hostParam.getNatSwitchName());
            network1.put("vlanid", hostParam.getNatVlanId());
            network1.put("inlimit", hostParam.getBandwidthIn());
            if (hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()) {
               JSONArray attachIpsArray = new JSONArray();

               for (HostIp hostIp : hostParam.getAttachIps()) {
                  JSONObject attachIpObj = new JSONObject();
                  attachIpObj.put("ip", hostIp.getIp());
                  attachIpObj.put("mask", hostIp.getMask());
                  attachIpsArray.put(attachIpObj);
               }

               network1.put("attachip", attachIpsArray);
            } else {
               network1.put("attachip", new JSONArray());
            }

            network2.put("ip", hostParam.getPrivateIp());
            network2.put("gateway", StrUtil.blankToDefault(hostParam.getPrivateGateway(), ""));
            network2.put("netmask", hostParam.getPrivateNetmask());
            network2.put("band_width", 0);
            network2.put("mac", hostParam.getPrivateMac());
            network2.put("switch_name", hostParam.getPrivateSwitchName());
            network2.put("vlanid", hostParam.getPrivateVlanId());
         }

         post_data.put("os_name", hostParam.getImageDirectoryName() + ".vhdx");
         JSONArray networks = new JSONArray();
         networks.put(network1);
         networks.put(network2);
         post_data.put("network", networks);
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.put("data", post_data);
         json.put("action", "reinstall");
         json.put("callback_param", hostParam.getServerId());
         json.put("state", "1");
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var27;
            label120: {
               CommonResult var28;
               label121: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var27 = CommonResult.error(responseString);
                        break label120;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var28 = CommonResult.error(result.getString("msg"));
                        break label121;
                     }

                     var28 = CommonResult.ok();
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

                  return var28;
               }

               if (response != null) {
                  response.close();
               }

               return var28;
            }

            if (response != null) {
               response.close();
            }

            return var27;
         } catch (IOException var16) {
            log.info("请求失败:{}", var16);
            return CommonResult.error(var16.getMessage());
         } catch (Exception var17) {
            log.info("请求失败:{}", var17);
            return CommonResult.error(var17.getMessage());
         }
      }
   }

   public CommonResult createSnapshotHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("name", hostParam.getHostName());
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         requestBody.put("action", "create_snapshot");
         requestBody.put("callback_param", hostParam.getServerId());
         requestBody.put("state", "1");
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var18;
            label77: {
               CommonResult var19;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var18 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var19 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var19 = CommonResult.ok();
                  } catch (Throwable var14) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
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

            return var18;
         } catch (IOException var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         } catch (Exception var16) {
            log.info("请求失败:{}", var16);
            return CommonResult.error(var16.getMessage());
         }
      }
   }

   public CommonResult restoreSnapshotHost(HostParam hostParam, int taskid) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("name", hostParam.getHostName());
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         requestBody.put("action", "restore_snapshot");
         requestBody.put("callback_param", taskid);
         requestBody.put("state", "1");
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var19;
            label77: {
               CommonResult var20;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var19 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var20 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var20 = CommonResult.ok("命令已发送");
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

            return var19;
         } catch (IOException var16) {
            log.info("请求失败:{}", var16);
            return CommonResult.error(var16.getMessage());
         } catch (Exception var17) {
            log.info("请求失败:{}", var17);
            return CommonResult.error(var17.getMessage());
         }
      }
   }

   public CommonResult removeSnapshotHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("name", hostParam.getHostName());
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         requestBody.put("action", "remove_snapshot");
         requestBody.put("callback_param", hostParam.getServerId());
         requestBody.put("state", "1");
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var18;
            label77: {
               CommonResult var19;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var18 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var19 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var19 = CommonResult.ok("命令已发送");
                  } catch (Throwable var14) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
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

            return var18;
         } catch (IOException var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         } catch (Exception var16) {
            log.info("请求失败:{}", var16);
            return CommonResult.error(var16.getMessage());
         }
      }
   }

   public static CommonResult createBackupHost(HostBackupParam hostBackupParam) {
      if (hostBackupParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostBackupParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostBackupParam.getHostName());
         post_data.put("name", hostBackupParam.getBackupName());
         post_data.put("back_dir", hostBackupParam.getBackupPath());
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         requestBody.put("action", "create_backup");
         requestBody.put("callback_param", hostBackupParam.getServerId());
         requestBody.put("state", "1");
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostBackupParam.getMasterIp() + ":" + hostBackupParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostBackupParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var18 = CommonResult.ok("命令已发送");
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult restoreBackupHost(HostBackupParam hostBackupParam) {
      if (hostBackupParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostBackupParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostBackupParam.getHostName());
         post_data.put("name", hostBackupParam.getBackupName());
         post_data.put("back_dir", hostBackupParam.getBackupPath());
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         requestBody.put("action", "restore_backup");
         requestBody.put("callback_param", hostBackupParam.getServerId());
         requestBody.put("state", "1");
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostBackupParam.getMasterIp() + ":" + hostBackupParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostBackupParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var18 = CommonResult.ok("命令已发送");
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult removeBackupHost(HostBackupParam hostBackupParam) {
      if (hostBackupParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostBackupParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostBackupParam.getHostName());
         post_data.put("name", hostBackupParam.getBackupName());
         post_data.put("back_dir", hostBackupParam.getBackupPath());
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         requestBody.put("action", "remove_backup");
         requestBody.put("callback_param", hostBackupParam.getServerId());
         requestBody.put("state", "1");
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostBackupParam.getMasterIp() + ":" + hostBackupParam.getApiPort() + "/api/qzcloud/CreateHyperVTask")
            .header("Content-Type", "application/json")
            .header("apikey", hostBackupParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var18 = CommonResult.ok("命令已发送");
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult addFirewallHost(HostStrategy hostStrategy) {
      log.info("请求策略：addFirewallHost:" + JSON.toJSONString(hostStrategy));
      if (hostStrategy.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostStrategy.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostStrategy.getHostName());
         post_data.put("name", hostStrategy.getStrategyName());
         post_data.put("action", hostStrategy.getAction());
         post_data.put("direction", hostStrategy.getDirection());
         post_data.put("remoteIPAddress", hostStrategy.getIp());
         post_data.put("localPort", hostStrategy.getPort());
         post_data.put("protocol", hostStrategy.getProtocol());
         post_data.put("weight", hostStrategy.getWeight());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostStrategy.getMasterIp() + ":" + hostStrategy.getApiPort() + "/api/qzcloud/AddAclExtendedHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostStrategy.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var18;
            label77: {
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        JSONObject result = JSON.parseObject(responseString);
                        var18 = CommonResult.error(result.getString("msg"));
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var18 = CommonResult.data(result.getJSONObject("data"));
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

            return var18;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         } catch (Exception var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         }
      }
   }

   public static CommonResult removeFirewallHost(HostStrategy hostStrategy) {
      if (hostStrategy.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostStrategy.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("vm_name", hostStrategy.getHostName());
         post_data.put("name", hostStrategy.getStrategyName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostStrategy.getMasterIp() + ":" + hostStrategy.getApiPort() + "/api/qzcloud/RemoveAclExtendedHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostStrategy.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var16;
            label77: {
               CommonResult var17;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var16 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var17 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var17 = CommonResult.data(result.getJSONObject("data"));
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

            return var16;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         } catch (Exception var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         }
      }
   }

   public static CommonResult addForwardPort(HostPort hostPort) {
      if (hostPort.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostPort.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("dport", String.valueOf(hostPort.getPrivatePort()));
         post_data.put("sport", String.valueOf(hostPort.getPublicPort()));
         String dip = StrUtil.trim(hostPort.getPublicIp());
         if (StrUtil.isBlank(dip)) {
            return CommonResult.error("端口映射失败：云主机NAT IP为空");
         } else {
            post_data.put("dip", dip);
            post_data.put("vm_name", hostPort.getHostName());
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject(post_data);
            log.info("添加端口映射:{}", json.toJSONString());
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
            Request request = new Builder()
               .url("http://" + hostPort.getMasterIp() + ":" + hostPort.getApiPort() + "/api/qzcloud/addPort")
               .header("Content-Type", "application/json")
               .header("apikey", hostPort.getApiKey())
               .post(body)
               .build();

            try {
               Response response = client.newCall(request).execute();

               CommonResult var17;
               label81: {
                  CommonResult var18;
                  label82: {
                     try {
                        int statusCode = response.code();
                        if (statusCode != 200) {
                           String responseString = response.body().string();
                           var17 = CommonResult.error(responseString);
                           break label81;
                        }

                        String responseString = response.body().string();
                        JSONObject result = JSON.parseObject(responseString);
                        if (result.getIntValue("code") != 200) {
                           var18 = CommonResult.error(result.getString("msg"));
                           break label82;
                        }

                        var18 = CommonResult.ok();
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
            } catch (IOException var14) {
               log.info("请求失败:{}", var14);
               return CommonResult.error(var14.getMessage());
            } catch (Exception var15) {
               log.info("请求失败:{}", var15);
               return CommonResult.error(var15.getMessage());
            }
         }
      }
   }

   public static CommonResult removeForwardPort(HostPort hostPort) {
      if (hostPort.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostPort.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("dport", String.valueOf(hostPort.getPrivatePort()));
         post_data.put("sport", String.valueOf(hostPort.getPublicPort()));
         String dip = StrUtil.trim(hostPort.getPublicIp());
         if (StrUtil.isBlank(dip)) {
            return CommonResult.error("删除端口映射失败：云主机NAT IP为空");
         } else {
            post_data.put("dip", dip);
            post_data.put("vm_name", hostPort.getHostName());
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject(post_data);
            log.info("删除端口映射:{}", json.toJSONString());
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
            Request request = new Builder()
               .url("http://" + hostPort.getMasterIp() + ":" + hostPort.getApiPort() + "/api/qzcloud/delPort")
               .header("Content-Type", "application/json")
               .header("apikey", hostPort.getApiKey())
               .post(body)
               .build();

            try {
               Response response = client.newCall(request).execute();

               CommonResult var17;
               label81: {
                  CommonResult var18;
                  label82: {
                     try {
                        int statusCode = response.code();
                        if (statusCode != 200) {
                           String responseString = response.body().string();
                           var17 = CommonResult.error(responseString);
                           break label81;
                        }

                        String responseString = response.body().string();
                        JSONObject result = JSON.parseObject(responseString);
                        if (result.getIntValue("code") != 200) {
                           var18 = CommonResult.error(result.getString("msg"));
                           break label82;
                        }

                        var18 = CommonResult.data(result.getJSONObject("data"));
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
            } catch (IOException var14) {
               log.info("请求失败:{}", var14);
               return CommonResult.error(var14.getMessage());
            } catch (Exception var15) {
               log.info("请求失败:{}", var15);
               return CommonResult.error(var15.getMessage());
            }
         }
      }
   }

   public static CommonResult updatePasswordHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new HashMap<>();
         post_data.put("password", hostParam.getSystemPassword());
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetPasswordHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var16;
            label77: {
               CommonResult var17;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var16 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var17 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var17 = CommonResult.data(result.getJSONObject("data"));
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

            return var16;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         } catch (Exception var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         }
      }
   }

   public CommonResult syncTimeHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SyncTimeHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var18;
            label77: {
               CommonResult var19;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var18 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var19 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var19 = CommonResult.data(result.getJSONObject("data"));
                  } catch (Throwable var14) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
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

            return var18;
         } catch (IOException var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         } catch (Exception var16) {
            log.info("请求失败:{}", var16);
            return CommonResult.error(var16.getMessage());
         }
      }
   }

   public static CommonResult getNetworkMonitoring(String nodeUrl, String apikey, Integer port) {
      if (apikey == null || apikey.isEmpty()) {
         throw new RuntimeException("通讯密钥错误");
      } else if (nodeUrl != null && !nodeUrl.isEmpty()) {
         new HashMap();
         OkHttpClient client = new OkHttpClient();
         RequestBody requestBody = RequestBody.create(new Gson().toJson(new HashMap()), MediaType.get("application/json; charset=utf-8"));
         Request request = new Builder()
            .url("http://" + nodeUrl + ":" + port + "/api/qzcloud/GetNetworkMonitoring")
            .header("Content-Type", "application/json")
            .header("apikey", apikey)
            .post(requestBody)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label80: {
               CommonResult var18;
               label81: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label80;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label81;
                     }

                     var18 = CommonResult.data(result.get("data"));
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      } else {
         throw new RuntimeException("节点通讯地址为空");
      }
   }

   public static CommonResult setNetworkStateHost(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("state", String.valueOf(hostParam.getDisconnect()));
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("vlanid", "");
         OkHttpClient client = new OkHttpClient();
         Map<String, Object> requestBody = new HashMap<>();
         requestBody.put("data", post_data);
         JSONObject json = new JSONObject(requestBody);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/SetVmNicsStateHyperV")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var18 = CommonResult.data(result.getJSONObject("data"));
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult batsetip(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         throw new CommonException("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         throw new CommonException("节点通讯地址为空");
      } else {
         Map<String, Object> post_data = new LinkedHashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("ip", hostParam.getIpStr().split(","));
         JSONObject json = new JSONObject(true);
         json.putAll(post_data);
         String requestUrl = "http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/UpdateOrAddIpHyperVBat";
         log.info("轻舟Hyper-V接口 /api/qzcloud/UpdateOrAddIpHyperVBat 开始请求, url:{}", requestUrl);
         log.info("轻舟Hyper-V接口 /api/qzcloud/UpdateOrAddIpHyperVBat 请求体:{}", json.toJSONString());
         RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toJSONString());
         Request request = new Builder().url(requestUrl).header("Content-Type", "application/json").header("apikey", hostParam.getApiKey()).post(body).build();
         log.info("轻舟Hyper-V接口 /api/qzcloud/UpdateOrAddIpHyperVBat 请求头: Content-Type={}, apikey={}", request.header("Content-Type"), request.header("apikey"));

         try {
            Response response = client.newCall(request).execute();

            CommonResult var16;
            label77: {
               label78: {
                  try {
                     int statusCode = response.code();
                     String responseString = response.body().string();
                     log.info("轻舟Hyper-V接口 /api/qzcloud/UpdateOrAddIpHyperVBat HTTP状态码:{}, 原始响应体:{}", statusCode, responseString);
                     JSONObject result = JSON.parseObject(responseString);
                     log.info("轻舟Hyper-V接口 /api/qzcloud/UpdateOrAddIpHyperVBat 响应体:{}", result.toJSONString());
                     if (statusCode != 200) {
                        var16 = CommonResult.error(result.getString("msg"));
                        break label77;
                     }

                     if (result.getIntValue("code") != 200) {
                        var16 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     var16 = CommonResult.data(result.getJSONObject("data"));
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

                  return var16;
               }

               if (response != null) {
                  response.close();
               }

               return var16;
            }

            if (response != null) {
               response.close();
            }

            return var16;
         } catch (IOException var13) {
            log.info("请求失败:{}", var13);
            return CommonResult.error(var13.getMessage());
         } catch (Exception var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         }
      }
   }

   public static CommonResult<SystemInfo> monitorCompany(String nodeUrl, String apikey, Integer port) {
      if (apikey == null || apikey.isEmpty()) {
         throw new RuntimeException("通讯密钥错误");
      } else if (nodeUrl != null && !nodeUrl.isEmpty()) {
         OkHttpClient client = new okhttp3.OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .build();
         RequestBody requestBody = RequestBody.create(new Gson().toJson(new HashMap()), MediaType.get("application/json; charset=utf-8"));
         Request request = new Builder()
            .url("http://" + nodeUrl + ":" + port + "/api/qzcloud/GetCompanyInfo")
            .header("Content-Type", "application/json")
            .header("apikey", apikey)
            .post(requestBody)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var9;
            try {
               if (!response.isSuccessful()) {
                  throw new RuntimeException("请求失败");
               }

               String responseBody = response.body().string();
               CommonResult<SystemInfo> commonResult = (CommonResult<SystemInfo>)new Gson()
                  .fromJson(responseBody, (new TypeToken<CommonResult<SystemInfo>>() {}).getType());
               var9 = commonResult;
            } catch (Throwable var11) {
               if (response != null) {
                  try {
                     response.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (response != null) {
               response.close();
            }

            return var9;
         } catch (Exception var12) {
            throw new RuntimeException(var12);
         }
      } else {
         throw new RuntimeException("节点通讯地址为空");
      }
   }

   public static CommonResult resetNetwork(HostParam hostParam) {
      if (hostParam.getApiKey() == null || hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp() != null && !hostParam.getMasterIp().isEmpty()) {
         Map<String, Object> post_data = new LinkedHashMap<>();
         JSONObject network1 = new JSONObject(true);
         JSONObject network2 = new JSONObject(true);
         if (hostParam.getShareIp() != 1) {
            network1.put("ip", hostParam.getPublicIp());
            if (hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()) {
               JSONArray attachIpsArray = new JSONArray();
               List<HostIp> sortedAttachIps = new ArrayList<>(hostParam.getAttachIps());
               sortedAttachIps.sort(Comparator.comparing(HostIp::getIp, Comparator.nullsLast(String::compareTo)));

               for (HostIp hostIp : sortedAttachIps) {
                  JSONObject attachIpObj = new JSONObject(true);
                  attachIpObj.put("ip", hostIp.getIp());
                  attachIpObj.put("mask", hostIp.getMask());
                  attachIpsArray.put(attachIpObj);
               }

               network1.put("attachip", attachIpsArray);
            } else {
               network1.put("attachip", new JSONArray());
            }

            network1.put("gateway", hostParam.getPublicGateway());
            network1.put("netmask", hostParam.getPublicNetmask());
            network1.put("dns1", "114.114.114.114");
            network1.put("dns2", "8.8.8.8");
            network1.put("mac", hostParam.getPublicMac());
            network2.put("ip", hostParam.getPrivateIp());
            network2.put("gateway", "");
            network2.put("netmask", hostParam.getPrivateNetmask());
            network2.put("mac", hostParam.getPrivateMac());
         } else {
            network1.put("ip", hostParam.getNatIp());
            if (hostParam.getAttachIps() != null && !hostParam.getAttachIps().isEmpty()) {
               JSONArray attachIpsArray = new JSONArray();
               List<HostIp> sortedAttachIps = new ArrayList<>(hostParam.getAttachIps());
               sortedAttachIps.sort(Comparator.comparing(HostIp::getIp, Comparator.nullsLast(String::compareTo)));

               for (HostIp hostIp : sortedAttachIps) {
                  JSONObject attachIpObj = new JSONObject(true);
                  attachIpObj.put("ip", hostIp.getIp());
                  attachIpObj.put("mask", hostIp.getMask());
                  attachIpsArray.put(attachIpObj);
               }

               network1.put("attachip", attachIpsArray);
            } else {
               network1.put("attachip", new JSONArray());
            }

            network1.put("gateway", hostParam.getNatGateway());
            network1.put("netmask", hostParam.getNatNetmask());
            network1.put("dns1", "114.114.114.114");
            network1.put("dns2", "8.8.8.8");
            network1.put("mac", hostParam.getNatMac());
            network2.put("ip", hostParam.getPrivateIp());
            network2.put("gateway", StrUtil.blankToDefault(hostParam.getPrivateGateway(), ""));
            network2.put("netmask", hostParam.getPrivateNetmask());
            network2.put("mac", hostParam.getPrivateMac());
         }

         JSONArray networks = new JSONArray();
         networks.put(network1);
         networks.put(network2);
         post_data.put("vm_name", hostParam.getHostName());
         post_data.put("network", networks);
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject(true);
         json.putAll(post_data);
         log.info("轻舟Hyper-V接口 /api/qzcloud/ResetNetwork 请求体:{}", json.toJSONString());
         RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/ResetNetwork")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var30;
            label124: {
               CommonResult var31;
               label125: {
                  CommonResult var14;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var30 = CommonResult.error(responseString);
                        break label124;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var31 = CommonResult.error(result.getString("msg"));
                        break label125;
                     }

                     Map<String, Object> data = (Map<String, Object>)result.getObject("data", Map.class);
                     var14 = CommonResult.data(data);
                  } catch (Throwable var16) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var15) {
                           var16.addSuppressed(var15);
                        }
                     }

                     throw var16;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var14;
               }

               if (response != null) {
                  response.close();
               }

               return var31;
            }

            if (response != null) {
               response.close();
            }

            return var30;
         } catch (IOException var17) {
            log.error("请求失败:{}", var17);
            return CommonResult.error(var17.getMessage());
         } catch (Exception var18) {
            log.error("请求失败:{}", var18);
            return CommonResult.error(var18.getMessage());
         }
      } else {
         return CommonResult.error("节点通讯地址为空");
      }
   }

   public static CommonResult getNetworkHistory(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/GetNetworkHistory")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  CommonResult var11;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Object data = result.get("data");
                     var11 = CommonResult.data(data);
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

                  return var11;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }

   public static CommonResult getCpuHistory(HostParam hostParam) {
      if (hostParam.getApiKey().isEmpty()) {
         return CommonResult.error("通讯密钥错误");
      } else if (hostParam.getMasterIp().isEmpty()) {
         return CommonResult.error("节点通讯地址为空");
      } else {
         Map<String, String> post_data = new HashMap<>();
         post_data.put("vm_name", hostParam.getHostName());
         OkHttpClient client = new OkHttpClient();
         JSONObject json = new JSONObject();
         json.putAll(post_data);
         RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString());
         Request request = new Builder()
            .url("http://" + hostParam.getMasterIp() + ":" + hostParam.getApiPort() + "/api/qzcloud/GetCpuHistory")
            .header("Content-Type", "application/json")
            .header("apikey", hostParam.getApiKey())
            .post(body)
            .build();

         try {
            Response response = client.newCall(request).execute();

            CommonResult var17;
            label77: {
               CommonResult var18;
               label78: {
                  CommonResult var11;
                  try {
                     int statusCode = response.code();
                     if (statusCode != 200) {
                        String responseString = response.body().string();
                        var17 = CommonResult.error(responseString);
                        break label77;
                     }

                     String responseString = response.body().string();
                     JSONObject result = JSON.parseObject(responseString);
                     if (result.getIntValue("code") != 200) {
                        var18 = CommonResult.error(result.getString("msg"));
                        break label78;
                     }

                     Object data = result.get("data");
                     var11 = CommonResult.data(data);
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

                  return var11;
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
         } catch (IOException var14) {
            log.info("请求失败:{}", var14);
            return CommonResult.error(var14.getMessage());
         } catch (Exception var15) {
            log.info("请求失败:{}", var15);
            return CommonResult.error(var15.getMessage());
         }
      }
   }
}

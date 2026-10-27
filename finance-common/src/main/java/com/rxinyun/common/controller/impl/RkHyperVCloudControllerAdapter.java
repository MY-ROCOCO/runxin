package com.rxinyun.common.controller.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.DiskSpace;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostIp;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.common.qzsystem.entity.HostStrategy;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import com.rxinyun.common.qzsystem.entity.MasterStatusParam;
import com.rxinyun.common.qzsystem.entity.SystemInfo;
import com.rxinyun.common.qzsystem.result.HostMonitorResult;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.OkHttpClient.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RkHyperVCloudControllerAdapter implements CloudControllerAdapter {
   private static final Logger log = LoggerFactory.getLogger(RkHyperVCloudControllerAdapter.class);
   private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
   private static final String SIGN_PARAM = "_Sign";
   private static final String DES_KEY = "ByhyperAPI";
   private static final String DES_IV = "QzSysCom";
   private static final OkHttpClient HTTP_CLIENT = new Builder()
      .connectTimeout(30L, TimeUnit.SECONDS)
      .readTimeout(30L, TimeUnit.SECONDS)
      .writeTimeout(30L, TimeUnit.SECONDS)
      .build();

   @Override
   public String getGuardVersion() {
      return "3";
   }

   @Override
   public CommonResult openHost(HostParam hostParam) {
      if (hostParam == null) {
         return CommonResult.error("创建参数为空");
      } else {
         String validateMsg = this.validateCreateParam(hostParam);
         if (validateMsg != null) {
            return CommonResult.error(validateMsg);
         } else {
            String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/create";
            JSONObject body = this.buildCreateBody(hostParam);

            try {
               this.appendSignIfPresent(body, hostParam.getApiKey());
               log.info("RkHyper-v创建云主机请求参数: {}", this.buildSafeLogBody(body).toJSONString());
               okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                  .url(url)
                  .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
                  .addHeader("Content-Type", "application/json");
               if (this.isNotBlank(hostParam.getApiKey())) {
                  requestBuilder.addHeader("apikey", hostParam.getApiKey());
               }

               Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute();

               CommonResult var8;
               try {
                  String respBody = response.body() != null ? response.body().string() : "";
                  var8 = this.parseRkResponse(response, respBody, "创建云主机", "创建命令已发送");
               } catch (Throwable var10) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                     }
                  }

                  throw var10;
               }

               if (response != null) {
                  response.close();
               }

               return var8;
            } catch (Exception var11) {
               log.warn("RkHyper-v创建云主机异常: url={}, e={}", url, var11.getMessage());
               return CommonResult.error("RkHyper-v创建异常: " + var11.getMessage());
            }
         }
      }
   }

   private CommonResult postJson(String url, JSONObject body, String apiKey, String actionName) {
      try {
         this.appendSignIfPresent(body, apiKey);
         log.info("RkHyper-v{}请求参数: {}", actionName, this.buildSafeLogBody(body).toJSONString());
         okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
            .url(url)
            .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
            .addHeader("Content-Type", "application/json");
         if (this.isNotBlank(apiKey)) {
            requestBuilder.addHeader("apikey", apiKey);
         }

         Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute();

         CommonResult var8;
         try {
            String respBody = response.body() != null ? response.body().string() : "";
            log.info("RkHyper-v{}原始响应: httpCode={}, body={}", new Object[]{actionName, response.code(), respBody});
            var8 = this.parseRkResponse(response, respBody, actionName, "success");
         } catch (Throwable var10) {
            if (response != null) {
               try {
                  response.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (response != null) {
            response.close();
         }

         return var8;
      } catch (Exception var11) {
         log.warn("RkHyper-v{}异常: url={}, e={}", new Object[]{actionName, url, var11.getMessage()});
         return CommonResult.error("RkHyper-v" + actionName + "异常: " + var11.getMessage());
      }
   }

   private CommonResult parseRkResponse(Response response, String respBody, String actionName, String defaultSuccessMsg) {
      JSONObject json = null;
      if (this.isNotBlank(respBody)) {
         try {
            json = JSON.parseObject(respBody);
         } catch (Exception var10) {
            log.warn("RkHyper-v{}响应不是JSON: httpCode={}, body={}", new Object[]{actionName, response.code(), respBody});
         }
      }

      if (json != null) {
         Boolean state = json.getBoolean("state");
         Integer code = json.getInteger("code");
         String msg = json.getString("msg");
         String queueId = this.firstNotBlank(
            json.getString("AnsyQueueId"),
            json.getString("AsyncQueueId"),
            json.getString("QueueID"),
            json.getString("QueueId"),
            json.getString("_QueueId"),
            json.getString("queueId")
         );
         log.info("RkHyper-v{}响应解析: state={}, code={}, msg={}, queueId={}", new Object[]{actionName, state, code, msg, queueId});
         if (code != null) {
            if (code == 0) {
               return new CommonResult<>(200, this.isNotBlank(msg) ? msg : defaultSuccessMsg, json);
            }

            return CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v" + actionName + "失败，错误码：" + code);
         }

         if (Boolean.TRUE.equals(state)) {
            return new CommonResult<>(200, this.isNotBlank(msg) ? msg : defaultSuccessMsg, json);
         }

         if (Boolean.FALSE.equals(state)) {
            return CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v" + actionName + "失败");
         }
      }

      if (!response.isSuccessful()) {
         log.warn("RkHyper-v{}失败: httpCode={}, body={}", new Object[]{actionName, response.code(), respBody});
         return CommonResult.error("RkHyper-v" + actionName + "失败: " + respBody);
      } else {
         return CommonResult.ok(defaultSuccessMsg);
      }
   }

   private JSONObject buildSafeLogBody(JSONObject body) {
      JSONObject safe = new JSONObject();
      safe.putAll(body);
      if (safe.containsKey("ObjPassword")) {
         safe.put("ObjPassword", "***");
      }

      if (safe.containsKey("Password")) {
         safe.put("Password", "***");
      }

      return safe;
   }

   private String validateCreateParam(HostParam p) {
      if (!this.isNotBlank(p.getMasterIp())) {
         return "节点通讯地址为空";
      } else if (!this.isNotBlank(p.getHostName())) {
         return "云主机名称为空";
      } else if (!this.isNotBlank(p.getVmConfigPath())) {
         return "虚拟机配置保存目录为空";
      } else if (!this.isNotBlank(p.getOsPath())) {
         return "系统盘目录为空";
      } else if (!this.isNotBlank(p.getDisksPath())) {
         return "数据盘目录为空";
      } else if (!this.isNotBlank(this.resolveImageFile(p))) {
         return "系统镜像路径为空";
      } else if (!this.isNotBlank(this.resolveExtranetSwitchName(p))) {
         return "外网交换机名称为空";
      } else if (!this.isNotBlank(this.resolveExtranetIp(p))) {
         return "外网IP为空";
      } else if (p.getAutoDns() == null || p.getAutoDns().isEmpty()) {
         return "线路客户机DNS为空";
      } else if (p.getRam() != null && p.getRam() > 0) {
         if (!Integer.valueOf(1).equals(p.getMemoryDynamic()) || p.getRamStart() != null && p.getRamStart() > 0) {
            return p.getCpu() != null && p.getCpu() > 0 ? null : "CPU数量无效";
         } else {
            return "动态内存最小内存无效";
         }
      } else {
         return "内存大小无效";
      }
   }

   private String validateVmActionParam(HostParam p) {
      if (p == null) {
         return "请求参数为空";
      } else if (!this.isNotBlank(p.getMasterIp())) {
         return "节点通讯地址为空";
      } else {
         return !this.isNotBlank(p.getHostName()) ? "云主机名称为空" : null;
      }
   }

   private JSONObject buildCreateBody(HostParam p) {
      JSONObject body = new JSONObject();
      body.put("_BindId", p.getServerId());
      body.put("_IsAsyn", 1);
      body.put("_QueueId", this.buildQueueId(p));
      body.put("VMName", p.getHostName());
      body.put("Generation", 1);
      body.put("ExtranetSwitchName", this.resolveExtranetSwitchName(p));
      body.put("ExpDate", this.formatExpDate(p));
      body.put("VMSavePath", p.getVmConfigPath());
      body.put("VHDSystemSavePath", p.getOsPath());
      body.put("VHDDataSavePath", p.getDisksPath());
      body.put("ImageFile", this.resolveImageFile(p));
      body.put("Disks", this.buildDisks(p).toJSONString());
      body.put("MemoryStartupBytes", this.resolveMemoryStartupGb(p));
      body.put("MemoryMaximumBytes", p.getRam());
      body.put("MemoryBuffer", this.valueOrDefault(p.getMemoryBufferPercentage(), 20));
      body.put("MemoryPriority", this.valueOrDefault(p.getMemoryPriority(), 50));
      body.put("Processor", p.getCpu());
      body.put("ProcessorMaximum", this.valueOrDefault(p.getCpuLimit(), 100));
      body.put("ProcessorReserve", this.valueOrDefault(p.getCpuReservePercentage(), 0));
      body.put("ProcessorRelativeWeight", this.valueOrDefault(p.getCpuRelativeWeight(), 100));
      body.put("ProcessorVirtualizationEnabled", 0);
      body.put("ExtranetVlanId", 0);
      body.put("ExtranetBandWidth", this.valueOrDefault(p.getBandwidth(), 0));
      body.put("ExtranetInboundBandWidth", this.valueOrDefault(p.getBandwidthIn(), this.valueOrDefault(p.getBandwidth(), 0)));
      body.put("ExtranetMACAddress", this.blankToDefault(this.resolveExtranetMac(p), ""));
      body.put("ExtranetIP", this.buildExtranetIp(p).toJSONString());
      body.put("ExtranetIPV6", "[]");
      body.put("ExtranetDNS", String.join(",", p.getAutoDns()));
      body.put("ExtranetDNSV6", "[]");
      body.put("IntranetVlanId", this.valueOrDefault(p.getPrivateVlanId(), 0));
      body.put("IntranetBandWidth", 1000);
      body.put("IntranetMACAddress", this.blankToDefault(p.getPrivateMac(), ""));
      body.put("IntranetIPAddress", this.blankToDefault(p.getPrivateIp(), ""));
      body.put("IntranetDNS", String.join(",", p.getAutoDns()));
      body.put("ObjPassword", p.getSystemPassword());
      if (p.getPort() != null && p.getPort() > 0) {
         body.put("RemotePort", p.getPort());
      }

      body.put("SecurityGroupRules", "[]");
      this.putIops(body, p);
      return body;
   }

   private String buildQueueId(HostParam p) {
      if (p != null && this.isNotBlank(p.getQueueId())) {
         return p.getQueueId();
      } else {
         String timePart = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
         if (p != null && this.isNotBlank(p.getServerId())) {
            String serverId = p.getServerId();
            return timePart + serverId.substring(Math.max(0, serverId.length() - 6));
         } else {
            return timePart;
         }
      }
   }

   private String buildQueueId(HostStrategy p) {
      String timePart = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
      if (p != null && this.isNotBlank(p.getServerId())) {
         String serverId = p.getServerId();
         return timePart + serverId.substring(Math.max(0, serverId.length() - 6));
      } else {
         return timePart;
      }
   }

   private void appendSignIfPresent(JSONObject body, String encryptedSecret) {
      if (this.isNotBlank(encryptedSecret)) {
         String secret = this.resolveSignSecret(encryptedSecret);
         if (!this.isNotBlank(secret)) {
            log.warn("RkHyper-v签名密钥解密失败，已跳过_Sign生成");
         } else {
            body.put("_Sign", this.buildSign(body, secret));
         }
      }
   }

   private String buildSign(JSONObject params, String secret) {
      StringBuilder signContent = new StringBuilder();
      List<String> keys = new ArrayList<>(params.keySet());
      keys.removeIf("_Sign"::equals);
      keys.sort(this.rkSignKeyComparator());

      for (String key : keys) {
         if (signContent.length() > 0) {
            signContent.append("&");
         }

         Object value = params.get(key);
         signContent.append(key).append("=").append(this.csharpUrlEncode(value == null ? "" : String.valueOf(value)));
      }

      signContent.append("&").append(secret);
      return this.md5Hex(signContent.toString());
   }

   private String resolveSignSecret(String configuredSecret) {
      String secret = this.decryptSecretKey(configuredSecret);
      if (this.isNotBlank(secret)) {
         return secret;
      } else {
         log.warn("RkHyper-v SecretKey不是可DES解密密文，按明文SecretKey参与_Sign生成");
         return configuredSecret.trim();
      }
   }

   private Comparator<String> rkSignKeyComparator() {
      return (left, right) -> {
         boolean leftUnderline = left != null && left.startsWith("_");
         boolean rightUnderline = right != null && right.startsWith("_");
         if (leftUnderline != rightUnderline) {
            return leftUnderline ? -1 : 1;
         } else {
            return String.valueOf(left).compareTo(String.valueOf(right));
         }
      };
   }

   private String csharpUrlEncode(String value) {
      StringBuilder encoded = new StringBuilder();
      byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
      char[] hex = "0123456789ABCDEF".toCharArray();

      for (byte b : bytes) {
         int v = b & 255;
         char c = (char)v;
         if ((c < 'A' || c > 'Z')
            && (c < 'a' || c > 'z')
            && (c < '0' || c > '9')
            && c != '-'
            && c != '_'
            && c != '.'
            && c != '!'
            && c != '*'
            && c != '('
            && c != ')') {
            if (c == ' ') {
               encoded.append('+');
            } else {
               encoded.append('%').append(hex[v >> 4 & 15]).append(hex[v & 15]);
            }
         } else {
            encoded.append(c);
         }
      }

      return encoded.toString();
   }

   private String decryptSecretKey(String encryptedSecret) {
      try {
         byte[] keyBytes = this.normalizeDesBytes("ByhyperAPI");
         byte[] ivBytes = this.normalizeDesBytes("QzSysCom");
         Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
         cipher.init(2, new SecretKeySpec(keyBytes, "DES"), new IvParameterSpec(ivBytes));
         byte[] encryptedBytes = this.decodeEncryptedSecret(encryptedSecret);
         byte[] plainBytes = cipher.doFinal(encryptedBytes);
         return new String(plainBytes, StandardCharsets.UTF_8);
      } catch (Exception var7) {
         log.warn("RkHyper-v SecretKey DES解密失败: {}", var7.getMessage());
         return null;
      }
   }

   private byte[] normalizeDesBytes(String value) {
      byte[] source = value.getBytes(StandardCharsets.UTF_8);
      byte[] result = new byte[8];
      System.arraycopy(source, 0, result, 0, Math.min(source.length, result.length));
      return result;
   }

   private byte[] decodeEncryptedSecret(String encryptedSecret) {
      String value = encryptedSecret.trim();

      try {
         return Base64.getDecoder().decode(value);
      } catch (IllegalArgumentException var4) {
         return this.hexToBytes(value);
      }
   }

   private byte[] hexToBytes(String hex) {
      String value = hex.trim();
      if (value.length() % 2 != 0) {
         throw new IllegalArgumentException("无效HEX密文长度");
      } else {
         byte[] bytes = new byte[value.length() / 2];

         for (int i = 0; i < value.length(); i += 2) {
            bytes[i / 2] = (byte)Integer.parseInt(value.substring(i, i + 2), 16);
         }

         return bytes;
      }
   }

   private String md5Hex(String value) {
      try {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
         StringBuilder hex = new StringBuilder();

         for (byte b : bytes) {
            String s = Integer.toHexString(b & 255);
            if (s.length() == 1) {
               hex.append('0');
            }

            hex.append(s);
         }

         return hex.toString().toUpperCase();
      } catch (Exception var10) {
         throw new IllegalStateException("MD5签名生成失败", var10);
      }
   }

   private JSONArray buildDisks(HostParam p) {
      JSONArray disks = new JSONArray();
      JSONObject systemDisk = new JSONObject();
      systemDisk.put("Id", p.getHostName() + "_system");
      systemDisk.put("IsSystem", 1);
      systemDisk.put("Size", this.valueOrDefault(p.getOsDisk(), 40));
      disks.add(systemDisk);
      List<Integer> dataDisks = new ArrayList<>();
      if (p.getHardDisks() != null && p.getHardDisks() > 0) {
         dataDisks.add(p.getHardDisks());
      }

      if (p.getData1DiskSize() != null && p.getData1DiskSize() > 0) {
         dataDisks.add(p.getData1DiskSize());
      }

      if (p.getData2DiskSize() != null && p.getData2DiskSize() > 0) {
         dataDisks.add(p.getData2DiskSize());
      }

      if (p.getData3DiskSize() != null && p.getData3DiskSize() > 0) {
         dataDisks.add(p.getData3DiskSize());
      }

      for (int i = 0; i < dataDisks.size(); i++) {
         JSONObject dataDisk = new JSONObject();
         dataDisk.put("Id", p.getHostName() + "_data" + (i + 1));
         dataDisk.put("IsSystem", 0);
         dataDisk.put("Size", dataDisks.get(i));
         disks.add(dataDisk);
      }

      return disks;
   }

   private JSONArray buildResizeDisks(HostParam p) {
      JSONArray disks = new JSONArray();
      if (p.getOsDisk() != null && p.getOsDisk() > 0) {
         JSONObject systemDisk = new JSONObject();
         systemDisk.put("Id", p.getHostName() + "_system");
         systemDisk.put("IsSystem", 1);
         systemDisk.put("Size", p.getOsDisk());
         disks.add(systemDisk);
      }

      List<Integer> dataDisks = p.getDataDisks();
      if ((dataDisks == null || dataDisks.isEmpty()) && p.getHardDisks() != null && p.getHardDisks() > 0) {
         dataDisks = new ArrayList<>();
         dataDisks.add(p.getHardDisks());
      }

      if (dataDisks == null) {
         return disks;
      } else {
         for (int i = 0; i < dataDisks.size(); i++) {
            Integer size = dataDisks.get(i);
            if (size != null && size > 0) {
               JSONObject disk = new JSONObject();
               disk.put("Id", p.getHostName() + "_data" + (i + 1));
               disk.put("IsSystem", 0);
               disk.put("Size", size);
               disks.add(disk);
            }
         }

         return disks;
      }
   }

   private JSONArray buildExtranetIp(HostParam p) {
      JSONArray ips = new JSONArray();
      JSONObject mainIp = new JSONObject();
      mainIp.put("ip", this.resolveExtranetIp(p));
      mainIp.put("mask", this.resolveExtranetMask(p));
      mainIp.put("gateway", this.resolveExtranetGateway(p));
      ips.add(mainIp);
      if (p.getAttachIps() != null) {
         for (HostIp attachIp : p.getAttachIps()) {
            if (attachIp != null && this.isNotBlank(attachIp.getIp())) {
               JSONObject item = new JSONObject();
               item.put("ip", attachIp.getIp());
               item.put("mask", this.blankToDefault(attachIp.getMask(), this.resolveExtranetMask(p)));
               item.put("gateway", this.resolveExtranetGateway(p));
               ips.add(item);
            }
         }
      }

      return ips;
   }

   private void putIops(JSONObject body, HostParam p) {
      if (p.getMinimumIops() != null && p.getMinimumIops() > 0L) {
         body.put("MinimumIOPS", p.getMinimumIops());
      }

      if (p.getMaximumIops() != null && p.getMaximumIops() > 0L) {
         body.put("MaximumIOPS", p.getMaximumIops());
      }
   }

   private Object resolveMemoryStartupGb(HostParam p) {
      return Integer.valueOf(1).equals(p.getMemoryDynamic()) && p.getRamStart() != null && p.getRamStart() > 0
         ? this.normalizeMemoryGb(p.getRamStart())
         : p.getRam();
   }

   private Object normalizeMemoryGb(Integer memory) {
      if (memory == null) {
         return null;
      } else if (memory >= 128) {
         double gb = memory.intValue() / 1024.0;
         return Math.round(gb * 100.0) / 100.0;
      } else {
         return memory;
      }
   }

   private String resolveImageFile(HostParam p) {
      if (!this.isNotBlank(p.getImagePath())) {
         return "";
      } else {
         String imagePath = p.getImagePath().trim();
         String lower = imagePath.toLowerCase();
         if (lower.endsWith(".vhd") || lower.endsWith(".vhdx")) {
            return imagePath;
         } else if (!this.isNotBlank(p.getImageDirectoryName())) {
            return imagePath;
         } else {
            String separator = !imagePath.endsWith("\\") && !imagePath.endsWith("/") ? "\\" : "";
            String imageName = p.getImageDirectoryName();
            String imageLower = imageName.toLowerCase();
            if (!imageLower.endsWith(".vhd") && !imageLower.endsWith(".vhdx")) {
               imageName = imageName + ".vhdx";
            }

            return imagePath + separator + imageName;
         }
      }
   }

   private String formatExpDate(HostParam p) {
      return p.getEndTime() == null ? "20991231" : new SimpleDateFormat("yyyyMMdd").format(p.getEndTime());
   }

   private String buildBaseUrl(String masterIp, Integer apiPort) {
      String host = masterIp.trim();
      if (!host.startsWith("http://") && !host.startsWith("https://")) {
         return "http://" + host + ":" + (apiPort != null ? apiPort : 80);
      } else {
         return host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
      }
   }

   private String resolveExtranetSwitchName(HostParam p) {
      return Integer.valueOf(1).equals(p.getShareIp()) ? p.getNatSwitchName() : p.getPublicSwitchName();
   }

   private String resolveExtranetIp(HostParam p) {
      return Integer.valueOf(1).equals(p.getShareIp()) ? p.getNatIp() : p.getPublicIp();
   }

   private String resolveExtranetGateway(HostParam p) {
      return Integer.valueOf(1).equals(p.getShareIp()) ? p.getNatGateway() : p.getPublicGateway();
   }

   private String resolveExtranetMask(HostParam p) {
      return Integer.valueOf(1).equals(p.getShareIp()) ? p.getNatNetmask() : p.getPublicNetmask();
   }

   private String resolveExtranetMac(HostParam p) {
      return Integer.valueOf(1).equals(p.getShareIp()) ? p.getNatMac() : p.getPublicMac();
   }

   private Integer valueOrDefault(Integer value, Integer defaultValue) {
      return value != null ? value : defaultValue;
   }

   private String blankToDefault(String value, String defaultValue) {
      return this.isNotBlank(value) ? value : defaultValue;
   }

   private boolean isNotBlank(String value) {
      return value != null && !value.trim().isEmpty();
   }

   private String firstNotBlank(String... values) {
      if (values == null) {
         return null;
      } else {
         for (String value : values) {
            if (this.isNotBlank(value)) {
               return value;
            }
         }

         return null;
      }
   }

   private String validateMasterParam(MasterStatusParam param) {
      if (param == null) {
         return "请求参数为空";
      } else {
         return !this.isNotBlank(param.getIp()) ? "节点通讯地址为空" : null;
      }
   }

   private JSONObject buildMasterBody(MasterStatusParam param) {
      JSONObject body = new JSONObject();
      if (this.isNotBlank(param.getBindId())) {
         body.put("_BindId", param.getBindId());
      }

      body.put("_IsAsyn", 0);
      body.put("_QueueId", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
      return body;
   }

   private CommonResult<SystemInfo> requestHostInfo(MasterStatusParam param) {
      String validateMsg = this.validateMasterParam(param);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(param.getIp(), param.getApiPort()) + "/api/cvm/host-info";
         JSONObject body = this.buildMasterBody(param);

         try {
            this.appendSignIfPresent(body, param.getApiPassword());
            log.info("RkHyper-v宿主机信息请求参数: {}", this.buildSafeLogBody(body).toJSONString());
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
               .url(url)
               .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
               .addHeader("Content-Type", "application/json");
            if (this.isNotBlank(param.getApiPassword())) {
               requestBuilder.addHeader("apikey", param.getApiPassword());
            }

            Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute();

            CommonResult var17;
            label102: {
               CommonResult var19;
               label103: {
                  label104: {
                     CommonResult var13;
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("RkHyper-v宿主机信息请求失败: httpCode={}, body={}", response.code(), respBody);
                           var17 = CommonResult.error("RkHyper-v宿主机信息请求失败: " + respBody);
                           break label102;
                        }

                        JSONObject json = JSON.parseObject(respBody);
                        Integer code = json.getInteger("code");
                        Boolean state = json.getBoolean("state");
                        String msg = json.getString("msg");
                        if (code != null && code != 0) {
                           var19 = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v宿主机信息请求失败，错误码：" + code);
                           break label103;
                        }

                        if (Boolean.FALSE.equals(state)) {
                           var19 = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v宿主机信息请求失败");
                           break label104;
                        }

                        SystemInfo info = this.mapRkHostInfo(json.getJSONObject("data"));
                        info.setStatus(1);
                        var13 = CommonResult.data(info);
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

                     return var13;
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
         } catch (Exception var16) {
            log.warn("RkHyper-v宿主机信息异常: url={}, e={}", url, var16.getMessage());
            return CommonResult.error("RkHyper-v宿主机信息异常: " + var16.getMessage());
         }
      }
   }

   private CommonResult<SystemInfo> requestMasterPing(MasterStatusParam param) {
      String validateMsg = this.validateMasterParam(param);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(param.getIp(), param.getApiPort()) + "/api/cvm/ping";
         JSONObject body = this.buildMasterBody(param);

         try {
            this.appendSignIfPresent(body, param.getApiPassword());
            log.info("RkHyper-v宿主机状态检测请求参数: {}", this.buildSafeLogBody(body).toJSONString());
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
               .url(url)
               .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
               .addHeader("Content-Type", "application/json");
            if (this.isNotBlank(param.getApiPassword())) {
               requestBuilder.addHeader("apikey", param.getApiPassword());
            }

            Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute();

            CommonResult var19;
            label92: {
               CommonResult var15;
               label93: {
                  CommonResult info;
                  try {
                     String respBody = response.body() != null ? response.body().string() : "";
                     if (!response.isSuccessful()) {
                        log.warn("RkHyper-v宿主机状态检测失败: httpCode={}, body={}", response.code(), respBody);
                        var19 = CommonResult.error("RkHyper-v宿主机状态检测失败: " + respBody);
                        break label92;
                     }

                     JSONObject json = JSON.parseObject(respBody);
                     Integer code = json.getInteger("code");
                     Boolean state = json.getBoolean("state");
                     String msg = json.getString("msg");
                     JSONObject data = json.getJSONObject("data");
                     String message = data != null ? data.getString("message") : null;
                     if (Boolean.TRUE.equals(state) && Integer.valueOf(0).equals(code) && "pong".equalsIgnoreCase(message)) {
                        SystemInfo infox = new SystemInfo();
                        infox.setStatus(1);
                        var15 = CommonResult.data(infox);
                        break label93;
                     }

                     info = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v宿主机状态检测失败");
                  } catch (Throwable var17) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var16) {
                           var17.addSuppressed(var16);
                        }
                     }

                     throw var17;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return info;
               }

               if (response != null) {
                  response.close();
               }

               return var15;
            }

            if (response != null) {
               response.close();
            }

            return var19;
         } catch (Exception var18) {
            log.warn("RkHyper-v宿主机状态检测异常: url={}, e={}", url, var18.getMessage());
            return CommonResult.error("RkHyper-v宿主机状态检测异常: " + var18.getMessage());
         }
      }
   }

   private SystemInfo mapRkHostInfo(JSONObject data) {
      SystemInfo info = new SystemInfo();
      if (data == null) {
         return info;
      } else {
         JSONObject cpu = data.getJSONObject("CPU");
         if (cpu != null) {
            Double usagePercent = getDoubleFromItem(cpu, "UsagePercent");
            if (usagePercent != null) {
               info.setCpuUsage(round2(usagePercent));
            }
         }

         JSONObject memory = data.getJSONObject("Memory");
         if (memory != null) {
            Double totalGb = getDoubleFromItem(memory, "TotalGB");
            Double freeGb = getDoubleFromItem(memory, "FreeGB");
            if (totalGb != null) {
               info.setMemory(round2(totalGb));
            }

            if (freeGb != null) {
               info.setFreeMemory(round2(freeGb));
            }
         }

         JSONArray disks = data.getJSONArray("Disks");
         if (disks != null) {
            info.setSpace(this.mapRkDiskList(disks));
         }

         return info;
      }
   }

   private List<DiskSpace> mapRkDiskList(JSONArray disks) {
      List<DiskSpace> list = new ArrayList<>();

      for (int i = 0; i < disks.size(); i++) {
         if (disks.get(i) instanceof JSONObject item) {
            DiskSpace disk = new DiskSpace();
            disk.setName(this.blankToDefault(item.getString("DriveLetter"), "disk" + i));
            Double totalGb = getDoubleFromItem(item, "TotalSizeGB");
            Double freeGb = getDoubleFromItem(item, "FreeSizeGB");
            if (totalGb != null) {
               disk.setSize(round2(totalGb));
            }

            if (freeGb != null) {
               disk.setFreeSpace(round2(freeGb));
            }

            list.add(disk);
         }
      }

      return list;
   }

   private static Double getDoubleFromItem(JSONObject item, String... keys) {
      for (String key : keys) {
         Object value = item.get(key);
         if (value != null) {
            if (value instanceof Number) {
               return ((Number)value).doubleValue();
            }

            try {
               return Double.parseDouble(String.valueOf(value));
            } catch (Exception var8) {
            }
         }
      }

      return null;
   }

   private static Long getLongFromItem(JSONObject item, String... keys) {
      for (String key : keys) {
         Object value = item.get(key);
         if (value != null) {
            if (value instanceof Number) {
               return ((Number)value).longValue();
            }

            try {
               return Long.parseLong(String.valueOf(value));
            } catch (Exception var8) {
            }
         }
      }

      return null;
   }

   private static Integer getIntegerFromItem(JSONObject item, String... keys) {
      Long value = getLongFromItem(item, keys);
      return value != null ? safeInt(value) : null;
   }

   private static long valueOrZero(Long value) {
      return value != null ? value : 0L;
   }

   private static Integer safeInt(long value) {
      if (value > 2147483647L) {
         return Integer.MAX_VALUE;
      } else {
         return value < -2147483648L ? Integer.MIN_VALUE : (int)value;
      }
   }

   private static Double round2(Double value) {
      return value == null ? null : Math.round(value * 100.0) / 100.0;
   }

   private CommonResult unsupported(String action) {
      return CommonResult.error("RkHyper-v暂未实现" + action);
   }

   @Override
   public CommonResult deleteHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/remove";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         return this.postJson(url, body, hostParam.getApiKey(), "删除云主机");
      }
   }

   @Override
   public CommonResult startHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/start";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         return this.postJson(url, body, hostParam.getApiKey(), "开机");
      }
   }

   @Override
   public CommonResult closeHost(HostParam hostParam) {
      return this.stopHost(hostParam, 3, "关机");
   }

   @Override
   public CommonResult restartHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/restart";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         return this.postJson(url, body, hostParam.getApiKey(), "重启");
      }
   }

   @Override
   public CommonResult reinstallHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else if (!this.isNotBlank(this.resolveImageFile(hostParam))) {
         return CommonResult.error("系统镜像路径为空");
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/reinstall";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("ImageFile", this.resolveImageFile(hostParam));
         body.put("Password", this.blankToDefault(hostParam.getSystemPassword(), ""));
         if (hostParam.getPort() != null && hostParam.getPort() > 0) {
            body.put("RemotePort", hostParam.getPort());
         }

         body.put("FormatDataDisk", Integer.valueOf(1).equals(hostParam.getFormatDataDisk()) ? 1 : 0);
         body.put("ForceDeleteSnapshots", 1);
         return this.postJson(url, body, hostParam.getApiKey(), "重装系统");
      }
   }

   @Override
   public CommonResult rebuildHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/rebuild";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("FullRebuild", Integer.valueOf(1).equals(hostParam.getFullRebuild()) ? 1 : 0);
         return this.postJson(url, body, hostParam.getApiKey(), "重建云主机");
      }
   }

   @Override
   public CommonResult renewHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else if (hostParam.getMonths() != null && hostParam.getMonths() > 0) {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/renew";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("Days", 0);
         body.put("Months", hostParam.getMonths());
         body.put("Years", 0);
         return this.postJson(url, body, hostParam.getApiKey(), "实例续费");
      } else {
         return CommonResult.error("续费月份无效");
      }
   }

   @Override
   public CommonResult updatePasswordHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else if (!this.isNotBlank(hostParam.getSystemPassword())) {
         return CommonResult.error("密码不能为空");
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/password";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("Password", hostParam.getSystemPassword());
         return this.postJson(url, body, hostParam.getApiKey(), "修改密码");
      }
   }

   @Override
   public CommonResult setNetworkStateHost(HostParam hostParam) {
      return this.unsupported("网络状态变更");
   }

   @Override
   public CommonResult powerHost(HostParam hostParam) {
      return this.stopHost(hostParam, 3, "断电");
   }

   private CommonResult stopHost(HostParam hostParam, int closeType, String actionName) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/stop";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("CVMCloseType", closeType);
         return this.postJson(url, body, hostParam.getApiKey(), actionName);
      }
   }

   @Override
   public CommonResult updateBandwidthHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else if (hostParam.getBandwidth() != null && hostParam.getBandwidth() > 0) {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/bandwidth";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("ExtranetBandWidth", hostParam.getBandwidth());
         body.put("IntranetBandWidth", 1000);
         body.put("ExtranetInboundBandWidth", this.valueOrDefault(hostParam.getBandwidthIn(), hostParam.getBandwidth()));
         return this.postJson(url, body, hostParam.getApiKey(), "设置带宽");
      } else {
         return CommonResult.error("外网带宽无效");
      }
   }

   @Override
   public CommonResult updateHardDiskHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         JSONArray resizeDisks = this.buildResizeDisks(hostParam);
         if (resizeDisks.isEmpty()) {
            return CommonResult.error("数据盘扩容参数为空");
         } else {
            String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/resizeVhd";
            JSONObject body = new JSONObject();
            body.put("_BindId", hostParam.getServerId());
            body.put("_IsAsyn", 1);
            body.put("_QueueId", this.buildQueueId(hostParam));
            body.put("VMName", hostParam.getHostName());
            body.put("Disks", resizeDisks.toJSONString());
            return this.postJson(url, body, hostParam.getApiKey(), "磁盘扩容");
         }
      }
   }

   @Override
   public CommonResult updateCpuRamHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else if (hostParam.getCpu() == null || hostParam.getCpu() <= 0) {
         return CommonResult.error("CPU数量无效");
      } else if (hostParam.getRam() != null && hostParam.getRam() > 0) {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/upgrade-config";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("Processor", hostParam.getCpu());
         body.put("ProcessorMaximum", this.valueOrDefault(hostParam.getCpuLimit(), 100));
         body.put("ProcessorReserve", this.valueOrDefault(hostParam.getCpuReservePercentage(), 0));
         body.put("ProcessorRelativeWeight", this.valueOrDefault(hostParam.getCpuRelativeWeight(), 100));
         body.put("ProcessorVirtualizationEnabled", 0);
         body.put("MemoryStartupBytes", this.resolveMemoryStartupGb(hostParam));
         body.put("MemoryMaximumBytes", hostParam.getRam());
         body.put("MemoryBuffer", this.valueOrDefault(hostParam.getMemoryBufferPercentage(), 20));
         body.put("MemoryPriority", this.valueOrDefault(hostParam.getMemoryPriority(), 50));
         return this.postJson(url, body, hostParam.getApiKey(), "升级配置");
      } else {
         return CommonResult.error("内存大小无效");
      }
   }

   @Override
   public CommonResult monitorHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/vmMeter";
         JSONObject body = this.buildVmMeterBody(hostParam);

         try {
            this.appendSignIfPresent(body, hostParam.getApiKey());
            log.info("RkHyper-v查询云服务器监控请求参数: {}", this.buildSafeLogBody(body).toJSONString());
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
               .url(url)
               .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
               .addHeader("Content-Type", "application/json");
            if (this.isNotBlank(hostParam.getApiKey())) {
               requestBuilder.addHeader("apikey", hostParam.getApiKey());
            }

            Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute();

            CommonResult var16;
            label102: {
               CommonResult var18;
               label103: {
                  label104: {
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("RkHyper-v查询云服务器监控失败: httpCode={}, body={}", response.code(), respBody);
                           var16 = CommonResult.error("RkHyper-v查询云服务器监控失败: " + respBody);
                           break label102;
                        }

                        JSONObject json = JSON.parseObject(respBody);
                        Integer code = json.getInteger("code");
                        Boolean state = json.getBoolean("state");
                        String msg = json.getString("msg");
                        if (code != null && code != 0) {
                           var18 = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v查询云服务器监控失败，错误码：" + code);
                           break label103;
                        }

                        if (Boolean.FALSE.equals(state)) {
                           var18 = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v查询云服务器监控失败");
                           break label104;
                        }

                        var18 = CommonResult.data(this.mapRkVmMeter(json.getJSONObject("data"), hostParam.getDateType()));
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
            }

            if (response != null) {
               response.close();
            }

            return var16;
         } catch (Exception var15) {
            log.warn("RkHyper-v查询云服务器监控异常: url={}, e={}", url, var15.getMessage());
            return CommonResult.error("RkHyper-v查询云服务器监控异常: " + var15.getMessage());
         }
      }
   }

   private JSONObject buildVmMeterBody(HostParam hostParam) {
      JSONObject body = new JSONObject();
      body.put("_BindId", hostParam.getServerId());
      body.put("_IsAsyn", 0);
      body.put("_QueueId", this.buildQueueId(hostParam));
      body.put("VMName", hostParam.getHostName());
      body.put("DateType", hostParam.getDateType() != null ? hostParam.getDateType() : 2);
      if (this.isNotBlank(hostParam.getMinDateTime())) {
         body.put("MinDateTime", hostParam.getMinDateTime());
      }

      if (this.isNotBlank(hostParam.getMaxDateTime())) {
         body.put("MaxDateTime", hostParam.getMaxDateTime());
      }

      body.put("Sort", this.isNotBlank(hostParam.getSort()) ? hostParam.getSort() : "desc");
      return body;
   }

   private HostMonitorResult mapRkVmMeter(JSONObject data, Integer dateType) {
      HostMonitorResult result = new HostMonitorResult();
      if (data == null) {
         return result;
      } else {
         JSONArray stateArray = data.getJSONArray("StateData");
         result.setStateData(this.mapRkStateDataList(stateArray));
         JSONObject stateItem = this.firstObject(stateArray);
         if (stateItem != null) {
            Double cpuUse = getDoubleFromItem(stateItem, "CPUUSE");
            if (cpuUse != null) {
               result.setCpuStats(round2(cpuUse));
            }

            Integer memoryAssigned = getIntegerFromItem(stateItem, "MemoryAssigned");
            if (memoryAssigned != null) {
               result.setMemoryAssigned(memoryAssigned);
            }

            Integer vmState = getIntegerFromItem(stateItem, "State");
            if (vmState != null) {
               result.setVmState(vmState);
            }
         }

         JSONArray networkArray = data.getJSONArray("NetworkData");
         List<HostMonitorResult.NetworkData> networkDataList = this.mapRkNetworkDataList(networkArray, dateType);
         result.setNetworkData(networkDataList);
         if (!networkDataList.isEmpty()) {
            HostMonitorResult.NetworkData networkItem = networkDataList.get(0);
            HostMonitorResult.NetworkStats networkStats = new HostMonitorResult.NetworkStats();
            networkStats.setBytesSentPersec(networkItem.getBytesSentPersec());
            networkStats.setBytesReceivedPersec(networkItem.getBytesReceivedPersec());
            result.setNetworkStats(networkStats);
            HostMonitorResult.Traffic traffic = new HostMonitorResult.Traffic();
            traffic.setTrafficOut(networkItem.getTrafficOut());
            traffic.setTrafficIn(networkItem.getTrafficIn());
            result.setTraffic(traffic);
         }

         JSONArray ioArray = data.getJSONArray("IOData");
         result.setIoData(this.mapRkIoDataList(ioArray));
         JSONObject ioItem = this.firstObject(ioArray);
         if (ioItem != null) {
            result.setDiskStats(this.mapRkIoData(ioItem));
         }

         return result;
      }
   }

   private List<HostMonitorResult.StateData> mapRkStateDataList(JSONArray array) {
      List<HostMonitorResult.StateData> list = new ArrayList<>();
      if (array != null && !array.isEmpty()) {
         for (Object item : array) {
            if (item instanceof JSONObject) {
               list.add(this.mapRkStateData((JSONObject)item));
            }
         }

         return list;
      } else {
         return list;
      }
   }

   private HostMonitorResult.StateData mapRkStateData(JSONObject item) {
      HostMonitorResult.StateData stateData = new HostMonitorResult.StateData();
      Double cpuUse = getDoubleFromItem(item, "CPUUSE");
      Integer memoryAssigned = getIntegerFromItem(item, "MemoryAssigned");
      Integer vmState = getIntegerFromItem(item, "State");
      stateData.setCpuStats(cpuUse != null ? round2(cpuUse) : 0.0);
      stateData.setMemoryAssigned(memoryAssigned != null ? memoryAssigned : 0);
      stateData.setVmState(vmState != null ? vmState : 0);
      stateData.setGetTime(this.getTimeString(item));
      return stateData;
   }

   private List<HostMonitorResult.NetworkData> mapRkNetworkDataList(JSONArray array, Integer dateType) {
      List<HostMonitorResult.NetworkData> list = new ArrayList<>();
      if (array != null && !array.isEmpty()) {
         List<JSONObject> items = new ArrayList<>();

         for (Object itemObj : array) {
            if (itemObj instanceof JSONObject) {
               items.add((JSONObject)itemObj);
            }
         }

         for (int i = 0; i < items.size(); i++) {
            JSONObject current = items.get(i);
            JSONObject previous = this.findPreviousRkNetworkItem(items, i);
            list.add(this.mapRkNetworkData(current, previous, dateType));
         }

         return list;
      } else {
         return list;
      }
   }

   private JSONObject findPreviousRkNetworkItem(List<JSONObject> items, int currentIndex) {
      JSONObject current = items.get(currentIndex);
      Long currentMillis = this.parseRkGetTimeMillis(current);
      if (currentMillis == null) {
         return null;
      } else {
         JSONObject previous = null;
         long previousMillis = Long.MIN_VALUE;

         for (int i = 0; i < items.size(); i++) {
            if (i != currentIndex) {
               JSONObject candidate = items.get(i);
               Long candidateMillis = this.parseRkGetTimeMillis(candidate);
               if (candidateMillis != null && candidateMillis < currentMillis && candidateMillis > previousMillis) {
                  previous = candidate;
                  previousMillis = candidateMillis;
               }
            }
         }

         return previous;
      }
   }

   private HostMonitorResult.NetworkData mapRkNetworkData(JSONObject item, JSONObject previousItem, Integer dateType) {
      HostMonitorResult.NetworkData networkData = new HostMonitorResult.NetworkData();
      long exBytes = valueOrZero(getLongFromItem(item, "ExBytes"));
      long exBytesReceived = valueOrZero(getLongFromItem(item, "ExBytesReceived"));
      long exBytesSent = valueOrZero(getLongFromItem(item, "ExBytesSent"));
      long inBytes = valueOrZero(getLongFromItem(item, "InBytes"));
      long inBytesReceived = valueOrZero(getLongFromItem(item, "InBytesReceived"));
      long inBytesSent = valueOrZero(getLongFromItem(item, "InBytesSent"));
      networkData.setBytesSentPersec(this.calculateRkBandwidthMbps(item, previousItem, dateType, true));
      networkData.setBytesReceivedPersec(this.calculateRkBandwidthMbps(item, previousItem, dateType, false));
      networkData.setTrafficOut(this.bytesToMb(exBytesSent));
      networkData.setTrafficIn(this.bytesToMb(exBytesReceived));
      networkData.setExBytes(exBytes);
      networkData.setExBytesReceived(exBytesReceived);
      networkData.setExBytesSent(exBytesSent);
      networkData.setInBytes(inBytes);
      networkData.setInBytesReceived(inBytesReceived);
      networkData.setInBytesSent(inBytesSent);
      networkData.setGetTime(this.getTimeString(item));
      return networkData;
   }

   private int calculateRkBandwidthMbps(JSONObject current, JSONObject previous, Integer dateType, boolean sent) {
      if (current == null) {
         return 0;
      } else {
         Double seconds = this.resolveRkSampleSeconds(current, previous, dateType);
         if (seconds != null && !(seconds <= 0.0)) {
            long sampleBytes = sent ? valueOrZero(getLongFromItem(current, "ExBytesSent")) : valueOrZero(getLongFromItem(current, "ExBytesReceived"));
            if (sampleBytes <= 0L) {
               return 0;
            } else {
               double mbps = sampleBytes * 8.0 / seconds / 1024.0 / 1024.0;
               return safeInt(Math.round(mbps));
            }
         } else {
            return 0;
         }
      }
   }

   private long bytesToMb(double bytes) {
      return bytes <= 0.0 ? 0L : Math.round(bytes / 1024.0 / 1024.0);
   }

   private Double resolveRkSampleSeconds(JSONObject current, JSONObject previous, Integer dateType) {
      if (dateType != null) {
         switch (dateType) {
            case 1:
               return 1.0;
            case 2:
               return 60.0;
            case 3:
               return 3600.0;
            case 4:
               return 86400.0;
            case 5:
               return this.resolveRkMonthSeconds(current);
            case 6:
               return this.resolveRkYearSeconds(current);
         }
      }

      Double secondsByPrecision = this.resolveRkSampleSecondsByGetTimePrecision(current);
      if (secondsByPrecision != null) {
         return secondsByPrecision;
      } else if (previous == null) {
         return null;
      } else {
         Long currentMillis = this.parseRkGetTimeMillis(current);
         Long previousMillis = this.parseRkGetTimeMillis(previous);
         return currentMillis != null && previousMillis != null && currentMillis > previousMillis ? (currentMillis - previousMillis) / 1000.0 : null;
      }
   }

   private Double resolveRkSampleSecondsByGetTimePrecision(JSONObject item) {
      if (item != null && item.get("GetTime") != null) {
         String value = String.valueOf(item.get("GetTime")).trim();
         if (value.length() >= 14) {
            return 1.0;
         } else if (value.length() == 12) {
            return 60.0;
         } else if (value.length() == 10) {
            return 3600.0;
         } else if (value.length() == 8) {
            return 86400.0;
         } else if (value.length() == 6) {
            return this.resolveRkMonthSeconds(item);
         } else {
            return value.length() == 4 ? this.resolveRkYearSeconds(item) : null;
         }
      } else {
         return null;
      }
   }

   private Double resolveRkMonthSeconds(JSONObject item) {
      if (item != null && item.get("GetTime") != null) {
         String value = String.valueOf(item.get("GetTime")).trim();
         if (value.length() < 6) {
            return null;
         } else {
            try {
               YearMonth yearMonth = YearMonth.parse(value.substring(0, 6), DateTimeFormatter.ofPattern("yyyyMM"));
               return yearMonth.lengthOfMonth() * 86400.0;
            } catch (Exception var4) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private Double resolveRkYearSeconds(JSONObject item) {
      if (item != null && item.get("GetTime") != null) {
         String value = String.valueOf(item.get("GetTime")).trim();
         if (value.length() < 4) {
            return null;
         } else {
            try {
               Year year = Year.parse(value.substring(0, 4), DateTimeFormatter.ofPattern("yyyy"));
               return year.length() * 86400.0;
            } catch (Exception var4) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private Long parseRkGetTimeMillis(JSONObject item) {
      if (item == null) {
         return null;
      } else {
         Object getTime = item.get("GetTime");
         if (getTime == null) {
            return null;
         } else {
            String value = String.valueOf(getTime).trim();

            try {
               Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(value);
               return date.getTime();
            } catch (Exception var5) {
               return null;
            }
         }
      }
   }

   private List<HostMonitorResult.DiskStats> mapRkIoDataList(JSONArray array) {
      List<HostMonitorResult.DiskStats> list = new ArrayList<>();
      if (array != null && !array.isEmpty()) {
         for (Object item : array) {
            if (item instanceof JSONObject) {
               list.add(this.mapRkIoData((JSONObject)item));
            }
         }

         return list;
      } else {
         return list;
      }
   }

   private HostMonitorResult.DiskStats mapRkIoData(JSONObject item) {
      HostMonitorResult.DiskStats diskStats = new HostMonitorResult.DiskStats();
      Long diskUsedBytes = getLongFromItem(item, "DiskUsedBytes");
      Long readBytes = getLongFromItem(item, "ReadBytes");
      Long writeBytes = getLongFromItem(item, "WriteBytes");
      diskStats.setDiskUsedBytes(valueOrZero(diskUsedBytes));
      diskStats.setDiskUsedGb(round2(valueOrZero(diskUsedBytes) / 1024.0 / 1024.0 / 1024.0));
      diskStats.setReadBytes(valueOrZero(readBytes));
      diskStats.setWriteBytes(valueOrZero(writeBytes));
      diskStats.setGetTime(this.getTimeString(item));
      return diskStats;
   }

   private String getTimeString(JSONObject item) {
      Object getTime = item.get("GetTime");
      return getTime != null ? String.valueOf(getTime) : "";
   }

   private JSONObject firstObject(JSONArray array) {
      if (array != null && !array.isEmpty()) {
         Object item = array.get(0);
         return item instanceof JSONObject ? (JSONObject)item : null;
      } else {
         return null;
      }
   }

   @Override
   public CommonResult thumbnailHost(HostParam hostParam) {
      return this.unsupported("运行截图");
   }

   @Override
   public CommonResult getStateHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/state";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 0);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());

         try {
            this.appendSignIfPresent(body, hostParam.getApiKey());
            log.info("RkHyper-v同步云服务器状态请求参数: {}", this.buildSafeLogBody(body).toJSONString());
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
               .url(url)
               .post(RequestBody.create(body.toJSONString(), JSON_TYPE))
               .addHeader("Content-Type", "application/json");
            if (this.isNotBlank(hostParam.getApiKey())) {
               requestBuilder.addHeader("apikey", hostParam.getApiKey());
            }

            Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute();

            CommonResult var19;
            label111: {
               CommonResult var21;
               label112: {
                  label113: {
                     CommonResult var15;
                     try {
                        String respBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                           log.warn("RkHyper-v同步云服务器状态失败: httpCode={}, body={}", response.code(), respBody);
                           var19 = CommonResult.error("RkHyper-v同步云服务器状态失败: " + respBody);
                           break label111;
                        }

                        JSONObject json = JSON.parseObject(respBody);
                        Integer code = json.getInteger("code");
                        Boolean state = json.getBoolean("state");
                        String msg = json.getString("msg");
                        if (code != null && code != 0) {
                           var21 = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v同步云服务器状态失败，错误码：" + code);
                           break label112;
                        }

                        if (Boolean.FALSE.equals(state)) {
                           var21 = CommonResult.error(this.isNotBlank(msg) ? msg : "RkHyper-v同步云服务器状态失败");
                           break label113;
                        }

                        JSONObject data = json.getJSONObject("data");
                        JSONObject result = new JSONObject();
                        String vmState = data != null ? data.getString("State") : null;
                        result.put("state", this.normalizeRkVmState(vmState));
                        result.put("rawState", vmState);
                        result.put("VMName", data != null ? data.getString("VMName") : hostParam.getHostName());
                        var15 = CommonResult.data(result);
                     } catch (Throwable var17) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var16) {
                              var17.addSuppressed(var16);
                           }
                        }

                        throw var17;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var15;
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
         } catch (Exception var18) {
            log.warn("RkHyper-v同步云服务器状态异常: url={}, e={}", url, var18.getMessage());
            return CommonResult.error("RkHyper-v同步云服务器状态异常: " + var18.getMessage());
         }
      }
   }

   private String normalizeRkVmState(String state) {
      if ("Running".equalsIgnoreCase(state)) {
         return "Running";
      } else if (!"Off".equalsIgnoreCase(state) && !"Stopped".equalsIgnoreCase(state)) {
         return this.isNotBlank(state) ? state : "Unknown";
      } else {
         return "Stopped";
      }
   }

   @Override
   public CommonResult guidHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/vnc";
         JSONObject body = new JSONObject();
         body.put("_IsAsyn", 0);
         body.put("VMName", hostParam.getHostName());
         CommonResult result = this.postJson(url, body, hostParam.getApiKey(), "获取VNC");
         if (result != null && Integer.valueOf(200).equals(result.getCode())) {
            JSONObject json = result.getData() instanceof JSONObject ? (JSONObject)result.getData() : null;
            JSONObject data = json != null ? json.getJSONObject("data") : null;
            String vncUrl = this.firstNotBlank(data != null ? data.getString("GuacamoleUrl") : null, data != null ? data.getString("VNCUrl") : null);
            if (!this.isNotBlank(vncUrl)) {
               return CommonResult.error("RkHyper-v未返回VNC地址");
            } else {
               if (data == null) {
                  data = new JSONObject();
               }

               data.put("url", vncUrl);
               return CommonResult.data(data);
            }
         } else {
            return result != null ? result : CommonResult.error("RkHyper-v获取VNC失败");
         }
      }
   }

   @Override
   public CommonResult batchSetIpHost(HostParam hostParam) {
      return this.setIpHost(hostParam);
   }

   @Override
   public CommonResult resetNetworkHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/resetip";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("ResetMAC", false);
         return this.postJson(url, body, hostParam.getApiKey(), "重置网络");
      }
   }

   @Override
   public CommonResult resetIpHost(HostParam hostParam) {
      return this.resetNetworkHost(hostParam);
   }

   private CommonResult setIpHost(HostParam hostParam) {
      String validateMsg = this.validateVmActionParam(hostParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else if (!this.isNotBlank(this.resolveExtranetIp(hostParam))) {
         return CommonResult.error("外网IP为空");
      } else if (!this.isNotBlank(hostParam.getPrivateIp())) {
         return CommonResult.error("内网IP为空");
      } else {
         String url = this.buildBaseUrl(hostParam.getMasterIp(), hostParam.getApiPort()) + "/api/cvm/setip";
         JSONObject body = new JSONObject();
         body.put("_BindId", hostParam.getServerId());
         body.put("_IsAsyn", 1);
         body.put("_QueueId", this.buildQueueId(hostParam));
         body.put("VMName", hostParam.getHostName());
         body.put("ExtranetMACAddress", this.blankToDefault(this.resolveExtranetMac(hostParam), ""));
         body.put("IntranetMACAddress", this.blankToDefault(hostParam.getPrivateMac(), ""));
         body.put("ExtranetIP", this.buildExtranetIp(hostParam).toJSONString());
         body.put("ExtranetIPV6", "[]");
         if (hostParam.getAutoDns() != null && !hostParam.getAutoDns().isEmpty()) {
            String dns = String.join(",", hostParam.getAutoDns());
            body.put("ExtranetDNS", dns);
            body.put("IntranetDNS", dns);
         }

         body.put("ExtranetDNSV6", "[]");
         body.put("IntranetIPAddress", hostParam.getPrivateIp());
         return this.postJson(url, body, hostParam.getApiKey(), "设置IP");
      }
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
      return this.unsupported("创建备份");
   }

   @Override
   public CommonResult restoreBackupHost(HostBackupParam hostBackupParam) {
      return this.unsupported("还原备份");
   }

   @Override
   public CommonResult removeBackupHost(HostBackupParam hostBackupParam) {
      return this.unsupported("删除备份");
   }

   @Override
   public CommonResult createSnapshotHost(HostBackupParam hostBackupParam) {
      String validateMsg = this.validateSnapshotParam(hostBackupParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostBackupParam.getMasterIp(), hostBackupParam.getApiPort()) + "/api/cvm/snapshot/add";
         JSONObject body = this.buildSnapshotBody(hostBackupParam);
         return this.postJson(url, body, hostBackupParam.getApiKey(), "创建快照");
      }
   }

   @Override
   public CommonResult restoreSnapshotHost(HostBackupParam hostBackupParam) {
      String validateMsg = this.validateSnapshotParam(hostBackupParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostBackupParam.getMasterIp(), hostBackupParam.getApiPort()) + "/api/cvm/snapshot/restore";
         JSONObject body = this.buildSnapshotBody(hostBackupParam);
         return this.postJson(url, body, hostBackupParam.getApiKey(), "还原快照");
      }
   }

   @Override
   public CommonResult removeSnapshotHost(HostBackupParam hostBackupParam) {
      String validateMsg = this.validateSnapshotParam(hostBackupParam);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostBackupParam.getMasterIp(), hostBackupParam.getApiPort()) + "/api/cvm/snapshot/remove";
         JSONObject body = this.buildSnapshotBody(hostBackupParam);
         return this.postJson(url, body, hostBackupParam.getApiKey(), "删除快照");
      }
   }

   private String validateSnapshotParam(HostBackupParam p) {
      if (p == null) {
         return "请求参数为空";
      } else if (!this.isNotBlank(p.getMasterIp())) {
         return "节点通讯地址为空";
      } else if (!this.isNotBlank(p.getServerId())) {
         return "云服务器ID为空";
      } else if (!this.isNotBlank(p.getHostName())) {
         return "云主机名称为空";
      } else {
         return !this.isNotBlank(this.resolveSnapshotName(p)) ? "快照名称为空" : null;
      }
   }

   private JSONObject buildSnapshotBody(HostBackupParam p) {
      JSONObject body = new JSONObject();
      body.put("_BindId", p.getServerId());
      body.put("_IsAsyn", 0);
      body.put("_QueueId", this.buildSnapshotQueueId(p));
      body.put("VMName", p.getHostName());
      body.put("SnapshotName", this.resolveSnapshotName(p));
      return body;
   }

   private String resolveSnapshotName(HostBackupParam p) {
      return this.isNotBlank(p.getSnapshotName()) ? p.getSnapshotName() : p.getBackupName();
   }

   private String buildSnapshotQueueId(HostBackupParam p) {
      String base = p.getServerId() + System.currentTimeMillis();
      return base.length() > 32 ? base.substring(base.length() - 32) : base;
   }

   @Override
   public CommonResult addFirewallHost(HostStrategy hostStrategy) {
      String validateMsg = this.validateFirewallParam(hostStrategy);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostStrategy.getMasterIp(), hostStrategy.getApiPort()) + "/api/cvm/firewall/add";
         JSONObject body = this.buildFirewallBody(hostStrategy);
         body.put("Replace", 1);
         return this.postJson(url, body, hostStrategy.getApiKey(), "新增防火墙策略");
      }
   }

   @Override
   public CommonResult updateFirewallHost(HostStrategy hostStrategy) {
      String validateMsg = this.validateFirewallUpdateParam(hostStrategy);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostStrategy.getMasterIp(), hostStrategy.getApiPort()) + "/api/cvm/firewall/update";
         JSONObject body = this.buildFirewallBody(hostStrategy);
         body.put("OldWeight", hostStrategy.getOldWeight());
         return this.postJson(url, body, hostStrategy.getApiKey(), "修改防火墙策略");
      }
   }

   @Override
   public CommonResult removeFirewallHost(HostStrategy hostStrategy) {
      String validateMsg = this.validateFirewallRemoveParam(hostStrategy);
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostStrategy.getMasterIp(), hostStrategy.getApiPort()) + "/api/cvm/firewall/remove";
         JSONObject body = this.buildFirewallRemoveBody(hostStrategy);
         return this.postJson(url, body, hostStrategy.getApiKey(), "删除防火墙策略");
      }
   }

   private String validateFirewallParam(HostStrategy p) {
      if (p == null) {
         return "请求参数为空";
      } else if (!this.isNotBlank(p.getMasterIp())) {
         return "节点通讯地址为空";
      } else if (!this.isNotBlank(p.getHostName())) {
         return "云主机名称为空";
      } else {
         String direction = this.resolveFirewallDirection(p.getDirection());
         if (!this.isNotBlank(direction)) {
            return "防火墙方向无效";
         } else if (!this.isNotBlank(this.resolveFirewallAction(p.getAction()))) {
            return "防火墙动作无效";
         } else if (p.getPort() != null && p.getPort() > 0) {
            if (p.getWeight() != null && "Inbound".equals(direction) && p.getWeight() < 4) {
               return "入站防火墙权重不能小于4";
            } else {
               return p.getWeight() != null && "Outbound".equals(direction) && p.getWeight() < 103 ? "出站防火墙权重不能小于103" : null;
            }
         } else {
            return "防火墙本地端口无效";
         }
      }
   }

   private String validateFirewallUpdateParam(HostStrategy p) {
      String validateMsg = this.validateFirewallParam(p);
      if (validateMsg != null) {
         return validateMsg;
      } else {
         return p.getOldWeight() == null ? "防火墙原权重为空" : null;
      }
   }

   private String validateFirewallRemoveParam(HostStrategy p) {
      if (p == null) {
         return "请求参数为空";
      } else if (!this.isNotBlank(p.getMasterIp())) {
         return "节点通讯地址为空";
      } else if (!this.isNotBlank(p.getHostName())) {
         return "云主机名称为空";
      } else {
         return p.getWeight() == null ? "防火墙权重为空" : null;
      }
   }

   private JSONObject buildFirewallBody(HostStrategy p) {
      JSONObject body = this.buildFirewallBaseBody(p, 0);
      String direction = this.resolveFirewallDirection(p.getDirection());
      body.put("Direction", direction);
      body.put("Action", this.resolveFirewallAction(p.getAction()));
      body.put("Protocol", this.resolveFirewallProtocol(p.getProtocol()));
      body.put("LocalPort", "Outbound".equals(direction) ? "ANY" : p.getPort());
      body.put("RemotePort", "Outbound".equals(direction) ? p.getPort() : "ANY");
      body.put("LocalIPAddress", "ANY");
      body.put("RemoteIPAddress", this.blankToDefault(p.getIp(), "ANY"));
      if (p.getWeight() != null) {
         body.put("Weight", p.getWeight());
      }

      body.put("Stateful", 1);
      return body;
   }

   private JSONObject buildFirewallRemoveBody(HostStrategy p) {
      JSONObject body = this.buildFirewallBaseBody(p, 0);
      body.put("Weight", p.getWeight());
      return body;
   }

   private JSONObject buildFirewallBaseBody(HostStrategy p, int isAsyn) {
      JSONObject body = new JSONObject();
      body.put("_BindId", p.getServerId());
      body.put("_IsAsyn", isAsyn);
      body.put("_QueueId", this.buildQueueId(p));
      body.put("VMName", p.getHostName());
      return body;
   }

   private String resolveFirewallDirection(String direction) {
      if (!this.isNotBlank(direction)) {
         return null;
      } else {
         String value = direction.trim();
         if ("1".equals(value) || "in".equalsIgnoreCase(value) || "inbound".equalsIgnoreCase(value)) {
            return "Inbound";
         } else {
            return !"2".equals(value) && !"out".equalsIgnoreCase(value) && !"outbound".equalsIgnoreCase(value) ? null : "Outbound";
         }
      }
   }

   private String resolveFirewallAction(Integer action) {
      if (Integer.valueOf(1).equals(action)) {
         return "Allow";
      } else {
         return Integer.valueOf(2).equals(action) ? "Deny" : null;
      }
   }

   private String resolveFirewallProtocol(String protocol) {
      return this.isNotBlank(protocol) && !"any".equalsIgnoreCase(protocol) ? protocol.trim().toUpperCase() : "Any";
   }

   @Override
   public CommonResult addForwardPort(HostPort hostPort) {
      String validateMsg = this.validateForwardPortParam(hostPort, "端口映射");
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostPort.getMasterIp(), hostPort.getApiPort()) + "/api/cvm/ikuai/add";
         JSONObject body = this.buildForwardPortBody(hostPort);
         return this.postJson(url, body, hostPort.getApiKey(), "新增端口映射");
      }
   }

   @Override
   public CommonResult removeForwardPort(HostPort hostPort) {
      String validateMsg = this.validateForwardPortParam(hostPort, "端口映射");
      if (validateMsg != null) {
         return CommonResult.error(validateMsg);
      } else {
         String url = this.buildBaseUrl(hostPort.getMasterIp(), hostPort.getApiPort()) + "/api/cvm/ikuai/delete";
         JSONObject body = this.buildForwardPortBody(hostPort);
         return this.postJson(url, body, hostPort.getApiKey(), "删除端口映射");
      }
   }

   private String validateForwardPortParam(HostPort p, String actionName) {
      if (p == null) {
         return "请求参数为空";
      } else if (!this.isNotBlank(p.getMasterIp())) {
         return "节点通讯地址为空";
      } else if (!this.isNotBlank(p.getHostName())) {
         return "云主机名称为空";
      } else if (!this.isNotBlank(p.getPublicIp())) {
         return actionName + "失败：云主机NAT IP为空";
      } else if (p.getPublicPort() == null || p.getPublicPort() <= 0) {
         return "公网端口无效";
      } else {
         return p.getPrivatePort() != null && p.getPrivatePort() > 0 ? null : "内网端口无效";
      }
   }

   private JSONObject buildForwardPortBody(HostPort p) {
      JSONObject body = new JSONObject();
      body.put("vm_name", p.getHostName());
      body.put("dip", p.getPublicIp());
      body.put("sport", String.valueOf(p.getPublicPort()));
      body.put("dport", String.valueOf(p.getPrivatePort()));
      body.put("_IsAsyn", "0");
      return body;
   }

   @Override
   public CommonResult getMasterStatus(MasterStatusParam param) {
      CommonResult<SystemInfo> result = this.requestMasterPing(param);
      if (result != null && Integer.valueOf(200).equals(result.getCode()) && result.getData() != null) {
         SystemInfo info = result.getData();
         info.setStatus(1);
         return CommonResult.data(info);
      } else {
         SystemInfo failInfo = new SystemInfo();
         failInfo.setStatus(0);
         return CommonResult.data(failInfo);
      }
   }

   @Override
   public CommonResult getCompanyInfo(MasterStatusParam param) {
      return this.requestHostInfo(param);
   }

   @Override
   public CommonResult<List<MasterMonitoringItem>> getNetworkMonitoring(MasterStatusParam param) {
      return CommonResult.data(new ArrayList<>());
   }
}

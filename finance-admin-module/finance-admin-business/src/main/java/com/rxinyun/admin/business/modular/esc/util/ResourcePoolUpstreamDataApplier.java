package com.rxinyun.admin.business.modular.esc.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import java.util.Date;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResourcePoolUpstreamDataApplier {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolUpstreamDataApplier.class);

   private ResourcePoolUpstreamDataApplier() {
   }

   public static void apply(JSONObject data, BaseServer existingServer, String localServerId) {
      String rawStatus = data.getString("serverStatus");
      if (StrUtil.isNotBlank(rawStatus)) {
         ServerStatusEnum statusEnum = ServerStatusEnum.getByCode(rawStatus);
         existingServer.setServerStatus(statusEnum.getCode());
      }

      String topLevelPublicIp = firstNonBlank(data.getString("publicIp"), data.getString("public_ip"));
      if (StrUtil.isNotBlank(topLevelPublicIp)) {
         existingServer.setPublicIp(topLevelPublicIp);
      }

      JSONArray ipList = data.getJSONArray("ipList");
      if (ipList != null && !ipList.isEmpty()) {
         for (int i = 0; i < ipList.size(); i++) {
            JSONObject ipObj = ipList.getJSONObject(i);
            String ip = firstNonBlank(ipObj.getString("ip"), ipObj.getString("ipAddress"));
            String ipType = firstNonBlank(ipObj.getString("ipType"), ipObj.getString("type"));
            String ipTypeUpper = ipType != null ? ipType.trim().toUpperCase() : "";
            if (!"PUBLIC".equals(ipTypeUpper) && !"PUBLIC_IP".equals(ipTypeUpper) && !"WAN".equals(ipTypeUpper)) {
               if ("PRIVATE".equals(ipTypeUpper) || "PRIVATE_IP".equals(ipTypeUpper) || "LAN".equals(ipTypeUpper)) {
                  existingServer.setPrivateIp(ip);
                  setCloudPrivateNetwork(ipObj, existingServer);
               }
            } else {
               if (StrUtil.isBlank(existingServer.getPublicIp()) && StrUtil.isNotBlank(ip)) {
                  existingServer.setPublicIp(ip);
               }

               setCloudPublicNetwork(ipObj, existingServer);
            }
         }
      }

      if (StrUtil.isBlank(existingServer.getPrivateIp())) {
         existingServer.setPrivateIp(firstNonBlank(data.getString("privateIp"), data.getString("private_ip")));
      }

      if (StrUtil.isBlank(existingServer.getPublicGateway())) {
         existingServer.setPublicGateway(data.getString("publicGateway"));
      }

      if (StrUtil.isBlank(existingServer.getPublicNetmask())) {
         existingServer.setPublicNetmask(data.getString("publicNetmask"));
      }

      if (StrUtil.isBlank(existingServer.getPrivateGateway())) {
         existingServer.setPrivateGateway(data.getString("privateGateway"));
      }

      if (StrUtil.isBlank(existingServer.getPrivateNetmask())) {
         existingServer.setPrivateNetmask(data.getString("privateNetmask"));
      }

      Integer topLevelPort = data.getInteger("port");
      if (topLevelPort != null) {
         existingServer.setPort(topLevelPort);
      }

      applyCloudPortFromList(data, existingServer, localServerId);
      String systemName = firstNonBlank(data.getString("systemName"), data.getString("osName"));
      if (systemName != null) {
         existingServer.setSystemName(systemName);
      }

      setIfNotBlank(data.getString("systemUserName"), existingServer::setSystemUserName);
      setIfNotBlank(data.getString("systemPassword"), existingServer::setSystemPassword);
      setIfNotBlank(data.getString("systemId"), existingServer::setSystemId);
      setIfNotBlank(data.getString("systemClassId"), existingServer::setSystemClassId);
      setIfNotBlank(data.getString("systemClass"), existingServer::setSystemClass);
      Integer cpu = data.getInteger("cpu");
      if (cpu != null) {
         existingServer.setCpu(cpu);
      }

      Integer ram = data.getInteger("ram");
      if (ram != null) {
         existingServer.setRam(ram);
      } else {
         Integer memoryGb = data.getInteger("memoryGb");
         if (memoryGb != null) {
            existingServer.setRam(memoryGb * 1024);
         }
      }

      Integer systemDisk = data.getInteger("systemDisk");
      if (systemDisk != null) {
         existingServer.setSystemDisk(systemDisk);
      } else {
         Integer diskGb = data.getInteger("diskGb");
         if (diskGb != null) {
            existingServer.setSystemDisk(diskGb);
         }
      }

      setIfNotNull(data.getInteger("bandwidth"), existingServer::setBandwidth);
      setIfNotNull(data.getInteger("bandwidthIn"), existingServer::setBandwidthIn);
      setIfNotNull(data.getInteger("defense"), existingServer::setDefense);
      setIfNotNull(data.getInteger("traffic"), existingServer::setTraffic);
      setIfNotNull(data.getInteger("shareIp"), existingServer::setShareIp);
      setIfNotNull(data.getInteger("syncTimeFlag"), existingServer::setSyncTimeFlag);
      setIfNotNull(data.getInteger("businessStatus"), existingServer::setBusinessStatus);
      setIfNotNull(resolveDate(data, "buyTime"), existingServer::setBuyTime);
      setIfNotNull(resolveDate(data, "endTime"), existingServer::setEndTime);
      setIfNotNull(resolveDate(data, "deleteTime"), existingServer::setDeleteTime);
      String upstreamServerId = firstNonBlank(data.getString("upstreamServerId"), data.getString("id"));
      if (StrUtil.isNotBlank(upstreamServerId)) {
         existingServer.setUpstreamServerId(upstreamServerId);
      }
   }

   public static void applyToIndependent(JSONObject data, BaseIndependent existing, String localServerId) {
      String rawStatus = data.getString("serverStatus");
      if (StrUtil.isNotBlank(rawStatus)) {
         ServerStatusEnum statusEnum = ServerStatusEnum.getByCode(rawStatus);
         existing.setServerStatus(statusEnum.getCode());
      }

      JSONArray ipList = data.getJSONArray("ipList");
      if (ipList != null && !ipList.isEmpty()) {
         for (int i = 0; i < ipList.size(); i++) {
            JSONObject ipObj = ipList.getJSONObject(i);
            String ip = firstNonBlank(ipObj.getString("ip"), ipObj.getString("ipAddress"));
            String ipType = firstNonBlank(ipObj.getString("ipType"), ipObj.getString("type"));
            String ipTypeUpper = ipType != null ? ipType.trim().toUpperCase() : "";
            if ("PUBLIC".equals(ipTypeUpper) || "PUBLIC_IP".equals(ipTypeUpper) || "WAN".equals(ipTypeUpper)) {
               existing.setServerIp(ip);
               break;
            }
         }
      }

      if (StrUtil.isBlank(existing.getServerIp())) {
         existing.setServerIp(firstNonBlank(data.getString("serverIp"), data.getString("publicIp"), data.getString("public_ip")));
      }

      applyIndependentPortFromList(data, existing, localServerId);
      String systemName = firstNonBlank(data.getString("systemName"), data.getString("osName"));
      if (systemName != null) {
         existing.setSystemName(systemName);
      }

      setIfNotNull(data.getInteger("bandwidth"), existing::setBandwidth);
      setIfNotNull(data.getInteger("defense"), existing::setDefense);
      setIfNotNull(data.getInteger("ipSize"), existing::setIpSize);
      setIfNotNull(data.getInteger("businessStatus"), existing::setBusinessStatus);
      setIfNotBlank(data.getString("systemUserName"), existing::setSystemUserName);
      setIfNotBlank(data.getString("systemPassword"), existing::setSystemPassword);
      setIfNotBlank(data.getString("systemId"), existing::setSystemId);
      setIfNotBlank(data.getString("systemClassId"), existing::setSystemClassId);
      setIfNotBlank(data.getString("systemClass"), existing::setSystemClass);
      setIfNotNull(resolveDate(data, "buyTime"), existing::setBuyTime);
      setIfNotNull(resolveDate(data, "endTime"), existing::setEndTime);
      setIfNotNull(resolveDate(data, "deleteTime"), existing::setDeleteTime);
      String upstreamServerId = firstNonBlank(data.getString("upstreamServerId"), data.getString("id"));
      if (StrUtil.isNotBlank(upstreamServerId)) {
         existing.setUpstreamServerId(upstreamServerId);
      }
   }

   private static void setCloudPublicNetwork(JSONObject ipObj, BaseServer existingServer) {
      setIfNotBlank(firstNonBlank(ipObj.getString("gateway"), ipObj.getString("publicGateway")), existingServer::setPublicGateway);
      setIfNotBlank(firstNonBlank(ipObj.getString("subnetMask"), ipObj.getString("netmask"), ipObj.getString("mask")), existingServer::setPublicNetmask);
      setIfNotBlank(ipObj.getString("mac"), existingServer::setPublicMac);
   }

   private static void setCloudPrivateNetwork(JSONObject ipObj, BaseServer existingServer) {
      setIfNotBlank(firstNonBlank(ipObj.getString("gateway"), ipObj.getString("privateGateway")), existingServer::setPrivateGateway);
      setIfNotBlank(firstNonBlank(ipObj.getString("subnetMask"), ipObj.getString("netmask"), ipObj.getString("mask")), existingServer::setPrivateNetmask);
      setIfNotBlank(ipObj.getString("mac"), existingServer::setPrivateMac);
   }

   private static void applyCloudPortFromList(JSONObject data, BaseServer existingServer, String localServerId) {
      JSONArray portList = data.getJSONArray("portList");
      if (existingServer.getPort() == null && portList != null && !portList.isEmpty()) {
         for (int i = 0; i < portList.size(); i++) {
            JSONObject portObj = portList.getJSONObject(i);
            Integer port = portObj.getInteger("port");
            String desc = portObj.getString("desc");
            if (port != null && ("SSH".equalsIgnoreCase(desc) || "RDP".equalsIgnoreCase(desc))) {
               existingServer.setPort(port);
               log.info("从端口映射集合兜底同步管理端口: {} ({})，localServerId: {}", new Object[]{port, desc, localServerId});
               return;
            }
         }
      }
   }

   private static void applyIndependentPortFromList(JSONObject data, BaseIndependent existing, String localServerId) {
      JSONArray portList = data.getJSONArray("portList");
      if (portList != null && !portList.isEmpty()) {
         for (int i = 0; i < portList.size(); i++) {
            JSONObject portObj = portList.getJSONObject(i);
            Integer port = portObj.getInteger("port");
            String desc = portObj.getString("desc");
            if (port != null && ("SSH".equalsIgnoreCase(desc) || "RDP".equalsIgnoreCase(desc))) {
               existing.setPort(port);
               log.info("独立服务器同步管理端口: {} ({})，localServerId: {}", new Object[]{port, desc, localServerId});
               return;
            }
         }
      }
   }

   private static void setIfNotBlank(String value, Consumer<String> setter) {
      if (StrUtil.isNotBlank(value)) {
         setter.accept(value);
      }
   }

   private static <T> void setIfNotNull(T value, Consumer<T> setter) {
      if (value != null) {
         setter.accept(value);
      }
   }

   private static Date resolveDate(JSONObject data, String fieldName) {
      try {
         return data.getDate(fieldName);
      } catch (Exception var3) {
         log.warn("资源池上游时间字段解析失败 field={} value={}", fieldName, data.get(fieldName));
         return null;
      }
   }

   private static String firstNonBlank(String... vals) {
      if (vals == null) {
         return null;
      } else {
         for (String v : vals) {
            if (StrUtil.isNotBlank(v)) {
               return v;
            }
         }

         return null;
      }
   }
}

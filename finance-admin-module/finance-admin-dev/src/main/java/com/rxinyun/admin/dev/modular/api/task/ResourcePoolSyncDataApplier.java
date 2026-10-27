package com.rxinyun.admin.dev.modular.api.task;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResourcePoolSyncDataApplier {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolSyncDataApplier.class);

   private ResourcePoolSyncDataApplier() {
   }

   public static String resolveUpstreamServerId(JSONObject data) {
      if (data == null) {
         return null;
      } else {
         String u = StrUtil.trimToNull(data.getString("upstreamServerId"));
         if (u == null) {
            u = StrUtil.trimToNull(data.getString("id"));
         }

         return u;
      }
   }

   public static void apply(JSONObject data, BaseServer existingServer, String localServerId) {
      String rawStatus = data.getString("serverStatus");
      if (rawStatus != null && !rawStatus.isEmpty()) {
         ServerStatusEnum statusEnum = ServerStatusEnum.getByCode(rawStatus);
         existingServer.setServerStatus(statusEnum.getCode());
         log.info("服务器状态同步：{} → {}，localServerId: {}", new Object[]{rawStatus, statusEnum.getCode(), localServerId});
      }

      String topLevelPublicIp = firstNonBlank(data.getString("publicIp"), data.getString("public_ip"));
      if (StrUtil.isNotBlank(topLevelPublicIp)) {
         existingServer.setPublicIp(topLevelPublicIp);
         log.info("从顶层字段同步公网 IP: {}，localServerId: {}", topLevelPublicIp, localServerId);
      }

      JSONArray ipList = data.getJSONArray("ipList");
      if (ipList != null && !ipList.isEmpty()) {
         for (int i = 0; i < ipList.size(); i++) {
            JSONObject ipObj = ipList.getJSONObject(i);
            String ip = ipObj.getString("ip");
            if (StrUtil.isBlank(ip)) {
               ip = ipObj.getString("ipAddress");
            }

            String ipType = ipObj.getString("ipType");
            if (StrUtil.isBlank(ipType)) {
               ipType = ipObj.getString("type");
            }

            String ipTypeUpper = ipType != null ? ipType.trim().toUpperCase() : "";
            if (!"PUBLIC".equals(ipTypeUpper) && !"PUBLIC_IP".equals(ipTypeUpper) && !"WAN".equals(ipTypeUpper)) {
               if ("PRIVATE".equals(ipTypeUpper) || "PRIVATE_IP".equals(ipTypeUpper) || "LAN".equals(ipTypeUpper)) {
                  existingServer.setPrivateIp(ip);
                  log.info("同步内网 IP: {}，localServerId: {}", ip, localServerId);
                  String gw = firstNonBlank(ipObj.getString("gateway"), ipObj.getString("privateGateway"));
                  if (StrUtil.isNotBlank(gw)) {
                     existingServer.setPrivateGateway(gw);
                  }

                  String mask = firstNonBlank(ipObj.getString("subnetMask"), ipObj.getString("netmask"), ipObj.getString("mask"));
                  if (StrUtil.isNotBlank(mask)) {
                     existingServer.setPrivateNetmask(mask);
                  }

                  String mac = ipObj.getString("mac");
                  if (StrUtil.isNotBlank(mac)) {
                     existingServer.setPrivateMac(mac);
                  }
               }
            } else {
               if (StrUtil.isBlank(existingServer.getPublicIp()) && StrUtil.isNotBlank(ip)) {
                  existingServer.setPublicIp(ip);
                  log.info("从 IP 绑定集合兜底同步公网 IP: {}，localServerId: {}", ip, localServerId);
               }

               String gwx = firstNonBlank(ipObj.getString("gateway"), ipObj.getString("publicGateway"));
               if (StrUtil.isNotBlank(gwx)) {
                  existingServer.setPublicGateway(gwx);
               }

               String maskx = firstNonBlank(ipObj.getString("subnetMask"), ipObj.getString("netmask"), ipObj.getString("mask"));
               if (StrUtil.isNotBlank(maskx)) {
                  existingServer.setPublicNetmask(maskx);
               }

               String mac = ipObj.getString("mac");
               if (StrUtil.isNotBlank(mac)) {
                  existingServer.setPublicMac(mac);
               }
            }
         }
      }

      if (StrUtil.isBlank(existingServer.getPrivateIp())) {
         String p = firstNonBlank(data.getString("privateIp"), data.getString("private_ip"));
         if (StrUtil.isNotBlank(p)) {
            existingServer.setPrivateIp(p);
            log.info("从顶层字段同步私网 IP: {}，localServerId: {}", p, localServerId);
         }
      }

      if (StrUtil.isBlank(existingServer.getPublicGateway()) && StrUtil.isNotBlank(data.getString("publicGateway"))) {
         existingServer.setPublicGateway(data.getString("publicGateway"));
      }

      if (StrUtil.isBlank(existingServer.getPublicNetmask()) && StrUtil.isNotBlank(data.getString("publicNetmask"))) {
         existingServer.setPublicNetmask(data.getString("publicNetmask"));
      }

      if (StrUtil.isBlank(existingServer.getPrivateGateway()) && StrUtil.isNotBlank(data.getString("privateGateway"))) {
         existingServer.setPrivateGateway(data.getString("privateGateway"));
      }

      if (StrUtil.isBlank(existingServer.getPrivateNetmask()) && StrUtil.isNotBlank(data.getString("privateNetmask"))) {
         existingServer.setPrivateNetmask(data.getString("privateNetmask"));
      }

      Integer topLevelPort = data.getInteger("port");
      if (topLevelPort != null) {
         existingServer.setPort(topLevelPort);
         log.info("从顶层字段同步管理端口: {}，localServerId: {}", topLevelPort, localServerId);
      }

      JSONArray portList = data.getJSONArray("portList");
      if (existingServer.getPort() == null && portList != null && !portList.isEmpty()) {
         for (int i = 0; i < portList.size(); i++) {
            JSONObject portObj = portList.getJSONObject(i);
            Integer port = portObj.getInteger("port");
            String desc = portObj.getString("desc");
            if (port != null && ("SSH".equalsIgnoreCase(desc) || "RDP".equalsIgnoreCase(desc))) {
               existingServer.setPort(port);
               log.info("从端口映射集合兜底同步管理端口: {} ({})，localServerId: {}", new Object[]{port, desc, localServerId});
               break;
            }
         }
      }

      String systemName = data.getString("systemName");
      if (systemName == null) {
         systemName = data.getString("osName");
      }

      if (systemName != null) {
         existingServer.setSystemName(systemName);
      }

      String systemUserName = data.getString("systemUserName");
      if (StrUtil.isNotBlank(systemUserName)) {
         existingServer.setSystemUserName(systemUserName);
      }

      String systemPassword = data.getString("systemPassword");
      if (StrUtil.isNotBlank(systemPassword)) {
         existingServer.setSystemPassword(systemPassword);
      }

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

      Integer bandwidth = data.getInteger("bandwidth");
      if (bandwidth != null) {
         existingServer.setBandwidth(bandwidth);
      }

      Integer shareIp = data.getInteger("shareIp");
      if (shareIp != null) {
         existingServer.setShareIp(shareIp);
      }

      Integer syncTimeFlag = data.getInteger("syncTimeFlag");
      if (syncTimeFlag != null) {
         existingServer.setSyncTimeFlag(syncTimeFlag);
      }

      Date buyTime = resolveDate(data, "buyTime");
      if (buyTime != null) {
         existingServer.setBuyTime(buyTime);
      }

      Date endTime = resolveDate(data, "endTime");
      if (endTime != null) {
         existingServer.setEndTime(endTime);
      }

      Date deleteTime = resolveDate(data, "deleteTime");
      if (deleteTime != null) {
         existingServer.setDeleteTime(deleteTime);
      }

      String up = StrUtil.blankToDefault(StrUtil.trimToNull(data.getString("upstreamServerId")), StrUtil.trimToNull(data.getString("id")));
      if (StrUtil.isNotBlank(up)) {
         existingServer.setUpstreamServerId(up);
      }
   }

   public static void applyToIndependent(JSONObject data, BaseIndependent existing, String localServerId) {
      String rawStatus = data.getString("serverStatus");
      if (rawStatus != null && !rawStatus.isEmpty()) {
         ServerStatusEnum statusEnum = ServerStatusEnum.getByCode(rawStatus);
         existing.setServerStatus(statusEnum.getCode());
         log.info("独立服务器状态同步：{} → {}，localServerId: {}", new Object[]{rawStatus, statusEnum.getCode(), localServerId});
      }

      JSONArray ipList = data.getJSONArray("ipList");
      if (ipList != null && !ipList.isEmpty()) {
         for (int i = 0; i < ipList.size(); i++) {
            JSONObject ipObj = ipList.getJSONObject(i);
            String ip = ipObj.getString("ip");
            if (StrUtil.isBlank(ip)) {
               ip = ipObj.getString("ipAddress");
            }

            String ipType = ipObj.getString("ipType");
            if (StrUtil.isBlank(ipType)) {
               ipType = ipObj.getString("type");
            }

            String ipTypeUpper = ipType != null ? ipType.trim().toUpperCase() : "";
            if ("PUBLIC".equals(ipTypeUpper) || "PUBLIC_IP".equals(ipTypeUpper) || "WAN".equals(ipTypeUpper)) {
               existing.setServerIp(ip);
               log.info("独立服务器同步公网 IP(serverIp): {}，localServerId: {}", ip, localServerId);
               break;
            }
         }
      }

      if (StrUtil.isBlank(existing.getServerIp())) {
         String p = firstNonBlank(data.getString("serverIp"), data.getString("publicIp"), data.getString("public_ip"));
         if (StrUtil.isNotBlank(p)) {
            existing.setServerIp(p);
            log.info("独立服务器从顶层字段同步 serverIp: {}，localServerId: {}", p, localServerId);
         }
      }

      JSONArray portList = data.getJSONArray("portList");
      if (portList != null && !portList.isEmpty()) {
         for (int i = 0; i < portList.size(); i++) {
            JSONObject portObj = portList.getJSONObject(i);
            Integer port = portObj.getInteger("port");
            String desc = portObj.getString("desc");
            if (port != null && ("SSH".equalsIgnoreCase(desc) || "RDP".equalsIgnoreCase(desc))) {
               existing.setPort(port);
               log.info("独立服务器同步管理端口: {} ({})，localServerId: {}", new Object[]{port, desc, localServerId});
               break;
            }
         }
      }

      String systemName = data.getString("systemName");
      if (systemName == null) {
         systemName = data.getString("osName");
      }

      if (systemName != null) {
         existing.setSystemName(systemName);
      }

      Integer bandwidth = data.getInteger("bandwidth");
      if (bandwidth != null) {
         existing.setBandwidth(bandwidth);
      }

      Integer defense = data.getInteger("defense");
      if (defense != null) {
         existing.setDefense(defense);
      }

      String sysUser = data.getString("systemUserName");
      if (StrUtil.isNotBlank(sysUser)) {
         existing.setSystemUserName(sysUser);
      }

      String sysPwd = data.getString("systemPassword");
      if (StrUtil.isNotBlank(sysPwd)) {
         existing.setSystemPassword(sysPwd);
      }

      String sid = data.getString("systemId");
      if (StrUtil.isNotBlank(sid)) {
         existing.setSystemId(sid);
      }

      String scid = data.getString("systemClassId");
      if (StrUtil.isNotBlank(scid)) {
         existing.setSystemClassId(scid);
      }

      String sc = data.getString("systemClass");
      if (StrUtil.isNotBlank(sc)) {
         existing.setSystemClass(sc);
      }

      Date buyTime = resolveDate(data, "buyTime");
      if (buyTime != null) {
         existing.setBuyTime(buyTime);
      }

      Date endTime = resolveDate(data, "endTime");
      if (endTime != null) {
         existing.setEndTime(endTime);
      }

      Date deleteTime = resolveDate(data, "deleteTime");
      if (deleteTime != null) {
         existing.setDeleteTime(deleteTime);
      }

      String up = StrUtil.blankToDefault(StrUtil.trimToNull(data.getString("upstreamServerId")), StrUtil.trimToNull(data.getString("id")));
      if (StrUtil.isNotBlank(up)) {
         existing.setUpstreamServerId(up);
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

   private static Date resolveDate(JSONObject data, String fieldName) {
      try {
         return data.getDate(fieldName);
      } catch (Exception var3) {
         log.warn("资源池上游时间字段解析失败 field={} value={}", fieldName, data.get(fieldName));
         return null;
      }
   }
}

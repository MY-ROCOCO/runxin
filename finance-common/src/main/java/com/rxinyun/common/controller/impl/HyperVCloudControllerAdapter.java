package com.rxinyun.common.controller.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.common.qzsystem.entity.HostStrategy;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import com.rxinyun.common.qzsystem.entity.MasterStatusParam;
import com.rxinyun.common.qzsystem.entity.SystemInfo;
import com.rxinyun.common.qzsystem.utils.HostOpenerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class HyperVCloudControllerAdapter implements CloudControllerAdapter {
   @Override
   public String getGuardVersion() {
      return "1";
   }

   @Override
   public CommonResult openHost(HostParam hostParam) {
      return HostOpenerUtil.openHost(hostParam);
   }

   @Override
   public CommonResult deleteHost(HostParam hostParam) {
      return HostOpenerUtil.deleteHost(hostParam);
   }

   @Override
   public CommonResult startHost(HostParam hostParam) {
      return HostOpenerUtil.startHost(hostParam);
   }

   @Override
   public CommonResult closeHost(HostParam hostParam) {
      return HostOpenerUtil.closeHost(hostParam);
   }

   @Override
   public CommonResult restartHost(HostParam hostParam) {
      return HostOpenerUtil.restartHost(hostParam);
   }

   @Override
   public CommonResult reinstallHost(HostParam hostParam) {
      return HostOpenerUtil.reinstallHost(hostParam);
   }

   @Override
   public CommonResult rebuildHost(HostParam hostParam) {
      return HostOpenerUtil.openHost(hostParam);
   }

   @Override
   public CommonResult updatePasswordHost(HostParam hostParam) {
      return HostOpenerUtil.updatePasswordHost(hostParam);
   }

   @Override
   public CommonResult setNetworkStateHost(HostParam hostParam) {
      return HostOpenerUtil.setNetworkStateHost(hostParam);
   }

   @Override
   public CommonResult powerHost(HostParam hostParam) {
      return HostOpenerUtil.powerHost(hostParam);
   }

   @Override
   public CommonResult updateBandwidthHost(HostParam hostParam) {
      return HostOpenerUtil.updateBandwidthHost(hostParam);
   }

   @Override
   public CommonResult updateHardDiskHost(HostParam hostParam) {
      return HostOpenerUtil.updateHardDiskHost(hostParam);
   }

   @Override
   public CommonResult updateCpuRamHost(HostParam hostParam) {
      return HostOpenerUtil.updateCpuRamHost(hostParam);
   }

   @Override
   public CommonResult monitorHost(HostParam hostParam) {
      return HostOpenerUtil.monitorHost(hostParam);
   }

   @Override
   public CommonResult thumbnailHost(HostParam hostParam) {
      return HostOpenerUtil.thumbnailHost(hostParam);
   }

   @Override
   public CommonResult getStateHost(HostParam hostParam) {
      return HostOpenerUtil.getStateHost(hostParam);
   }

   @Override
   public CommonResult guidHost(HostParam hostParam) {
      CommonResult result = HostOpenerUtil.guidHost(hostParam);
      if (result != null && result.getData() != null && Integer.valueOf(200).equals(result.getCode())) {
         JSONObject data = (JSONObject)result.getData();
         String guid = data.getString("guid");
         String httpsEnable = data.getString("https_enable");
         String domain = data.getString("domain");
         String httpPort = data.getString("http_port");
         String httpsPort = data.getString("https_port");
         if (httpPort == null || httpPort.isEmpty()) {
            httpPort = "8001";
         }

         if (httpsPort == null || httpsPort.isEmpty()) {
            httpsPort = "443";
         }

         boolean useHttps = "1".equals(httpsEnable);
         String protocol = useHttps ? "https" : "http";
         String host = domain != null && !domain.isEmpty() ? domain : hostParam.getMasterIp();
         String port = useHttps ? httpsPort : httpPort;
         String serverParam = hostParam.getMasterIp() != null && !hostParam.getMasterIp().isEmpty() ? hostParam.getMasterIp() : "127.0.0.1";
         String guid9 = guid != null && guid.length() >= 9 ? guid.substring(0, 9) : (guid != null ? guid : "");
         String password = "Qz0" + guid9;
         String user = hostParam.getHostName() != null ? hostParam.getHostName() : "";
         String vncUrl = protocol
            + "://"
            + host
            + ":"
            + port
            + "/?__EVENTTARGET=&__EVENTARGUMENT&server="
            + serverParam
            + "&vmGuid="
            + guid
            + "&password="
            + password
            + "&user="
            + user
            + "&connect=Connect!";
         data.put("url", vncUrl);
         return CommonResult.data(data);
      } else {
         return result != null ? result : CommonResult.error("获取VNC失败");
      }
   }

   @Override
   public CommonResult batchSetIpHost(HostParam hostParam) {
      return HostOpenerUtil.batsetip(hostParam);
   }

   @Override
   public CommonResult resetNetworkHost(HostParam hostParam) {
      return HostOpenerUtil.resetNetwork(hostParam);
   }

   @Override
   public CommonResult resetIpHost(HostParam hostParam) {
      CommonResult bindResult = HostOpenerUtil.batsetip(hostParam);
      if (bindResult != null && bindResult.getCode() != null && Integer.valueOf(200).equals(bindResult.getCode())) {
         try {
            TimeUnit.SECONDS.sleep(1L);
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            return CommonResult.error("等待ResetNetwork执行前被中断");
         }

         return HostOpenerUtil.resetNetwork(hostParam);
      } else {
         return bindResult != null ? bindResult : CommonResult.error("UpdateOrAddIpHyperVBat执行失败");
      }
   }

   @Override
   public CommonResult getNetworkHistory(HostParam hostParam) {
      return HostOpenerUtil.getNetworkHistory(hostParam);
   }

   @Override
   public CommonResult getCpuHistory(HostParam hostParam) {
      return HostOpenerUtil.getCpuHistory(hostParam);
   }

   @Override
   public CommonResult createBackupHost(HostBackupParam hostBackupParam) {
      return HostOpenerUtil.createBackupHost(hostBackupParam);
   }

   @Override
   public CommonResult restoreBackupHost(HostBackupParam hostBackupParam) {
      return HostOpenerUtil.restoreBackupHost(hostBackupParam);
   }

   @Override
   public CommonResult removeBackupHost(HostBackupParam hostBackupParam) {
      return HostOpenerUtil.removeBackupHost(hostBackupParam);
   }

   @Override
   public CommonResult addFirewallHost(HostStrategy hostStrategy) {
      return HostOpenerUtil.addFirewallHost(hostStrategy);
   }

   @Override
   public CommonResult removeFirewallHost(HostStrategy hostStrategy) {
      return HostOpenerUtil.removeFirewallHost(hostStrategy);
   }

   @Override
   public CommonResult addForwardPort(HostPort hostPort) {
      return HostOpenerUtil.addForwardPort(hostPort);
   }

   @Override
   public CommonResult removeForwardPort(HostPort hostPort) {
      return HostOpenerUtil.removeForwardPort(hostPort);
   }

   @Override
   public CommonResult getMasterStatus(MasterStatusParam param) {
      CommonResult result = HostOpenerUtil.monitorCompany(param.getIp(), param.getApiPassword(), param.getApiPort());
      if (result != null && result.getCode() != null && result.getCode().equals(200) && result.getData() != null) {
         SystemInfo info = (SystemInfo)result.getData();
         info.setStatus(1);
         return CommonResult.data(info);
      } else {
         SystemInfo failInfo = new SystemInfo();
         failInfo.setStatus(0);
         return result != null && result.getCode() != null && result.getCode().equals(200)
            ? CommonResult.data(failInfo)
            : (result != null ? result : CommonResult.error("请求失败"));
      }
   }

   @Override
   public CommonResult getCompanyInfo(MasterStatusParam param) {
      return HostOpenerUtil.monitorCompany(param.getIp(), param.getApiPassword(), param.getApiPort());
   }

   @Override
   public CommonResult<List<MasterMonitoringItem>> getNetworkMonitoring(MasterStatusParam param) {
      CommonResult<?> raw = HostOpenerUtil.getNetworkMonitoring(param.getIp(), param.getApiPassword(), param.getApiPort());
      if (raw != null && Integer.valueOf(200).equals(raw.getCode()) && raw.getData() != null) {
         List<MasterMonitoringItem> list = this.convertToMonitoringList(raw.getData());
         return CommonResult.data(list);
      } else {
         return raw != null && raw.getCode() != null ? CommonResult.get(raw.getCode(), raw.getMsg(), new ArrayList<>()) : CommonResult.error("获取监控数据失败");
      }
   }

   private List<MasterMonitoringItem> convertToMonitoringList(Object data) {
      List<MasterMonitoringItem> list = new ArrayList<>();
      if (!(data instanceof JSONObject obj)) {
         return list;
      } else {
         for (String instanceName : obj.keySet()) {
            Object val = obj.get(instanceName);
            String upload = "0";
            String download = "0";
            if (val instanceof JSONArray arr) {
               if (arr.size() >= 1 && arr.get(0) != null) {
                  upload = String.valueOf(arr.get(0)).trim();
               }

               if (arr.size() >= 2 && arr.get(1) != null) {
                  download = String.valueOf(arr.get(1)).trim();
               }
            }

            list.add(new MasterMonitoringItem(instanceName != null ? instanceName : "", upload.isEmpty() ? "0" : upload, download.isEmpty() ? "0" : download));
         }

         return list;
      }
   }
}

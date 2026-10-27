package com.rxinyun.common.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.common.qzsystem.entity.HostStrategy;
import com.rxinyun.common.qzsystem.entity.MasterMonitoringItem;
import com.rxinyun.common.qzsystem.entity.MasterStatusParam;
import java.util.List;

public interface CloudControllerAdapter {
   String getGuardVersion();

   CommonResult openHost(HostParam hostParam);

   CommonResult deleteHost(HostParam hostParam);

   CommonResult startHost(HostParam hostParam);

   CommonResult closeHost(HostParam hostParam);

   CommonResult restartHost(HostParam hostParam);

   CommonResult reinstallHost(HostParam hostParam);

   CommonResult rebuildHost(HostParam hostParam);

   default CommonResult renewHost(HostParam hostParam) {
      return CommonResult.ok();
   }

   CommonResult updatePasswordHost(HostParam hostParam);

   CommonResult setNetworkStateHost(HostParam hostParam);

   CommonResult powerHost(HostParam hostParam);

   CommonResult updateBandwidthHost(HostParam hostParam);

   CommonResult updateHardDiskHost(HostParam hostParam);

   CommonResult updateCpuRamHost(HostParam hostParam);

   CommonResult monitorHost(HostParam hostParam);

   CommonResult thumbnailHost(HostParam hostParam);

   CommonResult getStateHost(HostParam hostParam);

   CommonResult guidHost(HostParam hostParam);

   CommonResult batchSetIpHost(HostParam hostParam);

   CommonResult resetNetworkHost(HostParam hostParam);

   CommonResult resetIpHost(HostParam hostParam);

   CommonResult getNetworkHistory(HostParam hostParam);

   CommonResult getCpuHistory(HostParam hostParam);

   CommonResult createBackupHost(HostBackupParam hostBackupParam);

   CommonResult restoreBackupHost(HostBackupParam hostBackupParam);

   CommonResult removeBackupHost(HostBackupParam hostBackupParam);

   default CommonResult createSnapshotHost(HostBackupParam hostBackupParam) {
      return CommonResult.error("当前受控暂未实现创建快照");
   }

   default CommonResult restoreSnapshotHost(HostBackupParam hostBackupParam) {
      return CommonResult.error("当前受控暂未实现还原快照");
   }

   default CommonResult removeSnapshotHost(HostBackupParam hostBackupParam) {
      return CommonResult.error("当前受控暂未实现删除快照");
   }

   CommonResult addFirewallHost(HostStrategy hostStrategy);

   default CommonResult updateFirewallHost(HostStrategy hostStrategy) {
      return CommonResult.error("当前受控暂未实现修改防火墙策略");
   }

   CommonResult removeFirewallHost(HostStrategy hostStrategy);

   CommonResult addForwardPort(HostPort hostPort);

   CommonResult removeForwardPort(HostPort hostPort);

   CommonResult getMasterStatus(MasterStatusParam param);

   CommonResult getCompanyInfo(MasterStatusParam param);

   CommonResult<List<MasterMonitoringItem>> getNetworkMonitoring(MasterStatusParam param);
}

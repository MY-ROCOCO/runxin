package com.rxinyun.common.vhost;

import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.vhost.param.VhostCreateCommand;
import com.rxinyun.common.vhost.param.VhostExpireCommand;
import com.rxinyun.common.vhost.param.VhostPasswordCommand;
import com.rxinyun.common.vhost.param.VhostPowerCommand;
import com.rxinyun.common.vhost.result.VhostCreateResult;
import com.rxinyun.common.vhost.result.VhostNetWork;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import com.rxinyun.common.vhost.result.VhostSystemTotal;
import java.util.List;

public interface VhostPanelAdapter {
   String controlledVersion();

   VhostCreateResult createHost(VhostCreateCommand command);

   VhostCreateResult findExistingHost(VhostCreateCommand command);

   String setExpireTime(VhostExpireCommand command);

   String startSite(VhostPowerCommand command);

   String stopSite(VhostPowerCommand command);

   String deleteSite(VhostPowerCommand command);

   List<VhostPhpVersion> listPhpVersions(String panelUrl, String apiKey);

   default VhostSystemTotal getSystemTotal(String panelUrl, String apiKey) {
      throw new CommonException("受控版本不支持系统基础统计");
   }

   default VhostNetWork getNetWork(String panelUrl, String apiKey) {
      throw new CommonException("受控版本不支持实时状态");
   }

   String setPhpVersion(VhostPowerCommand command);

   List<String> addDomain(VhostPowerCommand command);

   List<String> deleteDomain(VhostPowerCommand command);

   String changeFtpPassword(VhostPasswordCommand command);

   String changeDbPassword(VhostPasswordCommand command);
}

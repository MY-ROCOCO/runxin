package com.rxinyun.admin.dev.modular.update.service;

import com.rxinyun.admin.dev.modular.update.param.CheckUpdateParam;
import com.rxinyun.admin.dev.modular.update.param.DownloadParam;
import com.rxinyun.admin.dev.modular.update.param.InstallParam;
import com.rxinyun.admin.dev.modular.update.result.DownloadResult;
import com.rxinyun.admin.dev.modular.update.result.InstallResult;
import com.rxinyun.admin.dev.modular.update.result.UpdateCheckResult;
import com.rxinyun.admin.dev.modular.update.result.UpdateProgressResult;

public interface UpdateService {
   UpdateCheckResult checkUpdate(CheckUpdateParam param);

   DownloadResult download(DownloadParam param);

   InstallResult install(InstallParam param);

   UpdateProgressResult getUpdateProgress();
}

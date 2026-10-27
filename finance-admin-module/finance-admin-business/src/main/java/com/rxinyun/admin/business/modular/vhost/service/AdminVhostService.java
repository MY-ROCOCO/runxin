package com.rxinyun.admin.business.modular.vhost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostDomainParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostIdParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPageParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPasswordParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPhpParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostRemarkParam;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import java.util.List;

public interface AdminVhostService {
   Page<AdminVhost> page(AdminVhostPageParam pageParam);

   AdminVhost detail(AdminVhostIdParam idParam);

   CommonResult<String> start(AdminVhostIdParam idParam);

   CommonResult<String> stop(AdminVhostIdParam idParam);

   List<VhostPhpVersion> phpVersions(AdminVhostIdParam idParam);

   CommonResult<String> changePhp(AdminVhostPhpParam param);

   CommonResult<String> addDomain(AdminVhostDomainParam param);

   CommonResult<String> deleteDomain(AdminVhostDomainParam param);

   CommonResult<String> changeFtpPassword(AdminVhostPasswordParam param);

   CommonResult<String> changeDbPassword(AdminVhostPasswordParam param);

   void editRemark(AdminVhostRemarkParam param);
}

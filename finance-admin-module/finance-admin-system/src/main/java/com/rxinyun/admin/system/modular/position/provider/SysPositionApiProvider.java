package com.rxinyun.admin.system.modular.position.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.position.param.SysPositionSelectorPositionParam;
import com.rxinyun.admin.system.modular.position.service.SysPositionService;
import com.rxinyun.sys.api.SysPositionApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysPositionApiProvider implements SysPositionApi {
   @Resource
   private SysPositionService sysPositionService;

   public String getNameById(String positionId) {
      return this.sysPositionService.queryEntity(positionId).getName();
   }

   public Page<JSONObject> positionSelector(String orgId, String searchKey) {
      SysPositionSelectorPositionParam sysPositionSelectorPositionParam = new SysPositionSelectorPositionParam();
      sysPositionSelectorPositionParam.setOrgId(orgId);
      sysPositionSelectorPositionParam.setSearchKey(searchKey);
      return (Page<JSONObject>)BeanUtil.toBean(this.sysPositionService.positionSelector(sysPositionSelectorPositionParam), Page.class);
   }
}

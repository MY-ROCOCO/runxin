package com.rxinyun.admin.system.modular.org.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import com.rxinyun.admin.system.modular.org.param.SysOrgSelectorOrgListParam;
import com.rxinyun.admin.system.modular.org.service.SysOrgService;
import com.rxinyun.sys.api.SysOrgApi;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SysOrgApiProvider implements SysOrgApi {
   @Resource
   private SysOrgService sysOrgService;

   public String getNameById(String orgId) {
      return this.sysOrgService.queryEntity(orgId).getName();
   }

   public String getSupervisorIdByOrgId(String orgId) {
      SysOrg sysOrg = (SysOrg)this.sysOrgService.getById(orgId);
      return ObjectUtil.isNotEmpty(sysOrg) ? sysOrg.getDirectorId() : null;
   }

   public List<Tree<String>> orgTreeSelector() {
      return this.sysOrgService.orgTreeSelector();
   }

   public Page<JSONObject> orgListSelector(String parentId) {
      SysOrgSelectorOrgListParam sysOrgSelectorOrgListParam = new SysOrgSelectorOrgListParam();
      sysOrgSelectorOrgListParam.setParentId(parentId);
      return (Page<JSONObject>)BeanUtil.toBean(this.sysOrgService.orgListSelector(sysOrgSelectorOrgListParam), Page.class);
   }
}

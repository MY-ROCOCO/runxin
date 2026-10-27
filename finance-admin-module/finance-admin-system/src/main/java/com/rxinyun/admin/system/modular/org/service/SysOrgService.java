package com.rxinyun.admin.system.modular.org.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import com.rxinyun.admin.system.modular.org.param.SysOrgAddParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgEditParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgIdParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgPageParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgSelectorOrgListParam;
import com.rxinyun.admin.system.modular.org.param.SysOrgSelectorUserParam;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import java.util.List;

public interface SysOrgService extends IService<SysOrg> {
   Page<SysOrg> page(SysOrgPageParam sysOrgPageParam);

   List<Tree<String>> tree();

   void add(SysOrgAddParam sysOrgAddParam);

   void edit(SysOrgEditParam sysOrgEditParam);

   void delete(List<SysOrgIdParam> sysOrgIdParamList);

   SysOrg detail(SysOrgIdParam sysOrgIdParam);

   SysOrg queryEntity(String id);

   List<SysOrg> getAllOrgList();

   String getOrgIdByOrgFullNameWithCreate(String orgFullName);

   List<SysOrg> getParentAndChildListById(List<SysOrg> originDataList, String id, boolean includeSelf);

   List<SysOrg> getChildListById(List<SysOrg> originDataList, String id, boolean includeSelf);

   List<SysOrg> getParentListById(List<SysOrg> originDataList, String id, boolean includeSelf);

   SysOrg getById(List<SysOrg> originDataList, String id);

   SysOrg getParentById(List<SysOrg> originDataList, String id);

   SysOrg getChildById(List<SysOrg> originDataList, String id);

   List<Tree<String>> orgTreeSelector();

   Page<SysOrg> orgListSelector(SysOrgSelectorOrgListParam sysOrgSelectorOrgListParam);

   Page<SysUser> userSelector(SysOrgSelectorUserParam sysOrgSelectorUserParam);
}

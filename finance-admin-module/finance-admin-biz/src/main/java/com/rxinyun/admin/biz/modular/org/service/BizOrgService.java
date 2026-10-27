package com.rxinyun.admin.biz.modular.org.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.biz.modular.org.entity.BizOrg;
import com.rxinyun.admin.biz.modular.org.param.BizOrgAddParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgEditParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgIdParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgPageParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgSelectorOrgListParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgSelectorUserParam;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
import java.util.List;

public interface BizOrgService extends IService<BizOrg> {
   Page<BizOrg> page(BizOrgPageParam bizOrgPageParam);

   List<Tree<String>> tree();

   void add(BizOrgAddParam bizOrgAddParam);

   void edit(BizOrgEditParam bizOrgEditParam);

   void delete(List<BizOrgIdParam> bizOrgIdParamList);

   BizOrg detail(BizOrgIdParam bizOrgIdParam);

   BizOrg queryEntity(String id);

   List<BizOrg> getAllOrgList();

   String getOrgIdByOrgFullNameWithCreate(String orgFullName);

   List<BizOrg> getParentAndChildListById(List<BizOrg> originDataList, String id, boolean includeSelf);

   List<BizOrg> getChildListById(List<BizOrg> originDataList, String id, boolean includeSelf);

   List<BizOrg> getParentListById(List<BizOrg> originDataList, String id, boolean includeSelf);

   BizOrg getById(List<BizOrg> originDataList, String id);

   BizOrg getParentById(List<BizOrg> originDataList, String id);

   BizOrg getChildById(List<BizOrg> originDataList, String id);

   List<Tree<String>> orgTreeSelector();

   List<BizOrg> orgListSelector(BizOrgSelectorOrgListParam bizOrgSelectorOrgListParam);

   Page<BizUser> userSelector(BizOrgSelectorUserParam bizOrgSelectorUserParam);
}

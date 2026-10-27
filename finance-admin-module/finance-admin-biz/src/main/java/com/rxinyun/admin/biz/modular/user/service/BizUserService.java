package com.rxinyun.admin.biz.modular.user.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.biz.modular.org.entity.BizOrg;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
import com.rxinyun.admin.biz.modular.user.param.BizUserAddParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserEditParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserExportParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserGrantRoleParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserIdParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserPageParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorOrgListParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorPositionParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorRoleParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorUserParam;
import com.rxinyun.admin.biz.modular.user.result.BizUserRoleResult;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface BizUserService extends IService<BizUser> {
   Page<BizUser> page(BizUserPageParam bizUserPageParam);

   void add(BizUserAddParam bizUserAddParam);

   void edit(BizUserEditParam bizUserEditParam);

   void delete(List<BizUserIdParam> bizUserIdParamList);

   BizUser detail(BizUserIdParam bizUserIdPara);

   BizUser queryEntity(String id);

   void disableUser(BizUserIdParam bizUserIdParam);

   void enableUser(BizUserIdParam bizUserIdParam);

   void resetPassword(BizUserIdParam bizUserIdParam);

   List<String> ownRole(BizUserIdParam bizUserIdParam);

   void grantRole(BizUserGrantRoleParam bizUserGrantRoleParam);

   void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException;

   void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException;

   List<Tree<String>> orgTreeSelector();

   Page<BizOrg> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam);

   Page<BizPosition> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam);

   Page<BizUserRoleResult> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam);

   Page<BizUser> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam);
}

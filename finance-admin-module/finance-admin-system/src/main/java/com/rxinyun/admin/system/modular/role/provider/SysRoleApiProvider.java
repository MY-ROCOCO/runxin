package com.rxinyun.admin.system.modular.role.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.core.enums.SysBuildInEnum;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.admin.system.modular.resource.entity.SysButton;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.enums.SysResourceCategoryEnum;
import com.rxinyun.admin.system.modular.resource.service.SysButtonService;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantResourceParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorRoleParam;
import com.rxinyun.admin.system.modular.role.service.SysRoleService;
import com.rxinyun.sys.api.SysRoleApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysRoleApiProvider implements SysRoleApi {
   @Resource
   private SysRoleService sysRoleService;
   @Resource
   private SysMenuService sysMenuService;
   @Resource
   private SysButtonService sysButtonService;
   @Resource
   private SysRelationService sysRelationService;

   public boolean orgHasRole(List<String> orgIdList) {
      return false;
   }

   public Page<JSONObject> roleSelector(String orgId, String category, String searchKey, List<String> dataScopeList, boolean excludeSuperAdmin) {
      SysRoleSelectorRoleParam sysRoleSelectorRoleParam = new SysRoleSelectorRoleParam();
      sysRoleSelectorRoleParam.setSearchKey(searchKey);
      sysRoleSelectorRoleParam.setExcludeSuperAdmin(excludeSuperAdmin);
      return (Page<JSONObject>)BeanUtil.toBean(this.sysRoleService.roleSelector(sysRoleSelectorRoleParam), Page.class);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void grantForGenMenuAndButton(String menuId) {
      String superAdminRoleId = ((SysRole)this.sysRoleService
            .getOne((Wrapper<SysRole>)new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue())))
         .getId();
      SysRoleGrantResourceParam sysRoleGrantResourceParam = new SysRoleGrantResourceParam();
      sysRoleGrantResourceParam.setId(superAdminRoleId);
      SysMenu sysMenu = this.sysMenuService.queryEntity(menuId);
      SysRoleGrantResourceParam.SysRoleGrantResource sysRoleGrantResource = new SysRoleGrantResourceParam.SysRoleGrantResource();
      sysRoleGrantResource.setMenuId(sysMenu.getId());
      List<String> buttonIdList = this.sysButtonService
         .list(
            (Wrapper<SysButton>)((LambdaQueryWrapper<SysButton>)new LambdaQueryWrapper<SysButton>().eq(SysButton::getParentId, sysMenu.getId()))
               .eq(SysButton::getCategory, SysResourceCategoryEnum.BUTTON.getValue())
         )
         .stream()
         .map(SysButton::getId)
         .collect(Collectors.toList());
      sysRoleGrantResource.setButtonInfo(buttonIdList);
      sysRoleGrantResourceParam.setGrantInfoList(CollectionUtil.newArrayList(new SysRoleGrantResourceParam.SysRoleGrantResource[]{sysRoleGrantResource}));
      List<String> menuIdList = sysRoleGrantResourceParam.getGrantInfoList()
         .stream()
         .map(SysRoleGrantResourceParam.SysRoleGrantResource::getMenuId)
         .collect(Collectors.toList());
      List<String> extJsonList = sysRoleGrantResourceParam.getGrantInfoList().stream().<String>map(JSONUtil::toJsonStr).collect(Collectors.toList());
      List<String> existMenuIdList = this.sysMenuService
         .list((Wrapper<SysMenu>)new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue()))
         .stream()
         .map(SysMenu::getId)
         .collect(Collectors.toList());
      if (ObjectUtil.isNotEmpty(existMenuIdList)) {
         this.sysRelationService
            .remove(
               (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getObjectId, superAdminRoleId))
                     .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()))
                  .notIn(SysRelation::getTargetId, existMenuIdList)
            );
      }

      this.sysRelationService.saveRelationBatchWithAppend(superAdminRoleId, menuIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue(), extJsonList);
   }
}

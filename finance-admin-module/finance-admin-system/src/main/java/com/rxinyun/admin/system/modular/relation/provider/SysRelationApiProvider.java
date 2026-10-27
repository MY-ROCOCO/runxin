package com.rxinyun.admin.system.modular.relation.provider;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import com.rxinyun.admin.system.modular.relation.enums.SysRelationCategoryEnum;
import com.rxinyun.admin.system.modular.relation.service.SysRelationService;
import com.rxinyun.sys.api.SysRelationApi;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class SysRelationApiProvider implements SysRelationApi {
   @Resource
   private SysRelationService sysRelationService;

   public List<String> getUserIdListByRoleIdList(List<String> roleIdList) {
      return this.sysRelationService.getRelationObjectIdListByTargetIdListAndCategory(roleIdList, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
   }

   public void removeRoleHasMobileMenuRelation(List<String> targetIdList) {
      this.sysRelationService
         .remove(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().in(SysRelation::getTargetId, targetIdList))
               .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue())
         );
   }

   public void removeRoleHasMobileButtonRelation(List<String> targetIdList, List<String> buttonIdList) {
      this.sysRelationService
         .list(
            (Wrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)((LambdaQueryWrapper<SysRelation>)new LambdaQueryWrapper<SysRelation>().in(SysRelation::getTargetId, targetIdList))
                  .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue()))
               .isNotNull(SysRelation::getExtJson)
         )
         .forEach(
            mobileRelation -> {
               JSONObject extJsonObject = JSONUtil.parseObj(mobileRelation.getExtJson());
               List<String> buttonInfoList = extJsonObject.getBeanList("buttonInfo", String.class);
               if (ObjectUtil.isNotEmpty(buttonInfoList)) {
                  Set<String> intersectionDistinct = CollectionUtil.intersectionDistinct(buttonIdList, buttonInfoList, new Collection[0]);
                  if (ObjectUtil.isNotEmpty(intersectionDistinct)) {
                     Collection<String> disjunction = CollectionUtil.disjunction(buttonInfoList, intersectionDistinct);
                     extJsonObject.set("buttonInfo", disjunction);
                  }
               }

               this.sysRelationService
                  .update(
                     (Wrapper<SysRelation>)((LambdaUpdateWrapper<SysRelation>)new LambdaUpdateWrapper<SysRelation>().eq(SysRelation::getId, mobileRelation.getId()))
                        .set(SysRelation::getExtJson, JSONUtil.toJsonStr(extJsonObject))
                  );
            }
         );
   }
}

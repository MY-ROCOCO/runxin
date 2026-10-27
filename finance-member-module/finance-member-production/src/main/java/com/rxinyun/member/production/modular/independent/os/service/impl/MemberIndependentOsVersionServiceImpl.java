package com.rxinyun.member.production.modular.independent.os.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsVersion;
import com.rxinyun.member.production.modular.independent.os.mapper.MemberIndependentOsVersionMapper;
import com.rxinyun.member.production.modular.independent.os.param.OsVersionIdParam;
import com.rxinyun.member.production.modular.independent.os.service.MemberIndependentOsVersionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
   rollbackFor = {Exception.class}
)
@Service
public class MemberIndependentOsVersionServiceImpl
   extends ServiceImpl<MemberIndependentOsVersionMapper, MemberIndependentOsVersion>
   implements MemberIndependentOsVersionService {
   @Override
   public MemberIndependentOsVersion detail(OsVersionIdParam osVersionIdParam) {
      return this.queryEntity(osVersionIdParam.getId());
   }

   @Override
   public MemberIndependentOsVersion queryEntity(String id) {
      MemberIndependentOsVersion osVersion = (MemberIndependentOsVersion)this.getById(id);
      if (ObjectUtil.isEmpty(osVersion)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return osVersion;
      }
   }

   @Override
   public List<MemberIndependentOsVersion> osList(String osClassId, String lineId) {
      LambdaQueryChainWrapper<MemberIndependentOsVersion> query = (LambdaQueryChainWrapper<MemberIndependentOsVersion>)this.lambdaQuery()
         .eq(MemberIndependentOsVersion::getStatus, 1);
      if (ObjectUtil.isNotEmpty(osClassId)) {
         query.eq(MemberIndependentOsVersion::getOsClassId, osClassId);
      }

      List<MemberIndependentOsVersion> list = query.list();
      List<MemberIndependentOsVersion> result = new ArrayList<>();

      for (MemberIndependentOsVersion osVersion : list) {
         String versionLineId = StrUtil.blankToDefault(osVersion.getLineId(), "").replace(" ", "");
         if (StrUtil.isNotBlank(lineId) && StrUtil.split(versionLineId, ',').stream().anyMatch(lineId::equals)) {
            result.add(osVersion);
         }
      }

      return result;
   }
}

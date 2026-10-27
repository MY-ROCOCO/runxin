package com.rxinyun.member.production.modular.independent.os.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsClass;
import com.rxinyun.member.production.modular.independent.os.mapper.MemberIndependentOsClassMapper;
import com.rxinyun.member.production.modular.independent.os.param.OsClassIdParam;
import com.rxinyun.member.production.modular.independent.os.service.MemberIndependentOsClassService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
   rollbackFor = {Exception.class}
)
@Service
public class MemberIndependentOsClassServiceImpl
   extends ServiceImpl<MemberIndependentOsClassMapper, MemberIndependentOsClass>
   implements MemberIndependentOsClassService {
   @Override
   public MemberIndependentOsClass detail(OsClassIdParam osClassIdParam) {
      return this.queryEntity(osClassIdParam.getId());
   }

   @Override
   public MemberIndependentOsClass queryEntity(String id) {
      MemberIndependentOsClass osClass = (MemberIndependentOsClass)this.getById(id);
      if (ObjectUtil.isEmpty(osClass)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return osClass;
      }
   }

   @Override
   public List<MemberIndependentOsClass> osTypeList() {
      return (this.lambdaQuery().eq(MemberIndependentOsClass::getStatus, 1)).list();
   }
}

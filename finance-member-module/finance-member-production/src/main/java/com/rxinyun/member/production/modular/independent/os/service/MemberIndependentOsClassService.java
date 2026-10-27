package com.rxinyun.member.production.modular.independent.os.service;

import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsClass;
import com.rxinyun.member.production.modular.independent.os.param.OsClassIdParam;
import java.util.List;

public interface MemberIndependentOsClassService {
   MemberIndependentOsClass detail(OsClassIdParam osClassIdParam);

   MemberIndependentOsClass queryEntity(String id);

   List<MemberIndependentOsClass> osTypeList();
}

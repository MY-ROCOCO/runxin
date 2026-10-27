package com.rxinyun.member.production.modular.independent.os.service;

import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsVersion;
import com.rxinyun.member.production.modular.independent.os.param.OsVersionIdParam;
import java.util.List;

public interface MemberIndependentOsVersionService {
   MemberIndependentOsVersion detail(OsVersionIdParam osVersionIdParam);

   MemberIndependentOsVersion queryEntity(String id);

   List<MemberIndependentOsVersion> osList(String osClassId, String lineId);
}

package com.rxinyun.admin.production.modular.independent.os.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsVersion;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionEditParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionIdParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionPageParam;
import java.util.List;

public interface OsVersionService {
   Boolean isBindLine(String id);

   Page<IndependentOsVersion> page(OsVersionPageParam pageParam);

   void add(OsVersionAddParam osVersionAddParam);

   void edit(OsVersionEditParam osVersionEditParam);

   IndependentOsVersion detail(OsVersionIdParam osVersionIdParam);

   IndependentOsVersion queryEntity(String id);

   void delete(List<OsVersionIdParam> osVersionIdParamList);
}

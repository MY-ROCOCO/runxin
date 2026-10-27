package com.rxinyun.admin.production.modular.independent.os.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsClass;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassEditParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassIdParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassPageParam;
import java.util.List;

public interface OsClassService {
   Page<IndependentOsClass> page(OsClassPageParam pageParam);

   void add(OsClassAddParam osClassAddParam);

   void edit(OsClassEditParam osClassEditParam);

   IndependentOsClass detail(OsClassIdParam osClassIdParam);

   IndependentOsClass queryEntity(String id);

   void delete(List<OsClassIdParam> osClassIdParamList);
}

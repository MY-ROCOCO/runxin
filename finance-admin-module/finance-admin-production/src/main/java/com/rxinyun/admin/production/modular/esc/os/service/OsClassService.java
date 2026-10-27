package com.rxinyun.admin.production.modular.esc.os.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.os.entity.OsClass;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassEditParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassIdParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassPageParam;
import java.util.List;

public interface OsClassService {
   Page<OsClass> page(OsClassPageParam pageParam);

   void add(OsClassAddParam osClassAddParam);

   void edit(OsClassEditParam osClassEditParam);

   OsClass detail(OsClassIdParam osClassIdParam);

   OsClass queryEntity(String id);

   void delete(List<OsClassIdParam> osClassIdParamList);
}

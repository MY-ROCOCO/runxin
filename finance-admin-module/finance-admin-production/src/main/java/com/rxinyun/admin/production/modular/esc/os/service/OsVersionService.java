package com.rxinyun.admin.production.modular.esc.os.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.os.entity.OsVersion;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionEditParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionIdParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionPageParam;
import java.util.List;

public interface OsVersionService {
   Page<OsVersion> page(OsVersionPageParam pageParam);

   void add(OsVersionAddParam osVersionAddParam);

   void edit(OsVersionEditParam osVersionEditParam);

   OsVersion detail(OsVersionIdParam osVersionIdParam);

   OsVersion queryEntity(String id);

   void delete(List<OsVersionIdParam> osVersionIdParamList);
}

package com.rxinyun.admin.dev.modular.dict.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.dict.entity.DevDict;
import com.rxinyun.admin.dev.modular.dict.param.DevDictAddParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictEditParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictIdParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictListParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictPageParam;
import com.rxinyun.admin.dev.modular.dict.param.DevDictTreeParam;
import java.util.List;

public interface DevDictService extends IService<DevDict> {
   Page<DevDict> page(DevDictPageParam devDictPageParam);

   List<DevDict> list(DevDictListParam devDictListParam);

   List<Tree<String>> tree(DevDictTreeParam devDictTreeParam);

   void add(DevDictAddParam devDictAddParam);

   void edit(DevDictEditParam devDictEditParam);

   void delete(List<DevDictIdParam> devDictIdParamList);

   DevDict detail(DevDictIdParam devDictIdParam);

   DevDict queryEntity(String id);
}

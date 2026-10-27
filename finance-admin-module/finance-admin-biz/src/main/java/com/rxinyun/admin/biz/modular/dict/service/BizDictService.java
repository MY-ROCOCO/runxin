package com.rxinyun.admin.biz.modular.dict.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.biz.modular.dict.entity.BizDict;
import com.rxinyun.admin.biz.modular.dict.param.BizDictEditParam;
import com.rxinyun.admin.biz.modular.dict.param.BizDictPageParam;
import java.util.List;

public interface BizDictService extends IService<BizDict> {
   Page<BizDict> page(BizDictPageParam bizDictPageParam);

   List<Tree<String>> tree();

   List<Tree<String>> treeAll();

   void edit(BizDictEditParam bizDictEditParam);

   BizDict queryEntity(String id);
}

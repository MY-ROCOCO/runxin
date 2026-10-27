package com.rxinyun.admin.biz.modular.dict.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.biz.modular.dict.entity.BizDict;
import com.rxinyun.admin.biz.modular.dict.param.BizDictEditParam;
import com.rxinyun.admin.biz.modular.dict.param.BizDictPageParam;
import com.rxinyun.admin.biz.modular.dict.service.BizDictService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "业务字典控制器"
)
@RestController
@Validated
public class BizDictController {
   @Resource
   private BizDictService bizDictService;

   @Operation(
      summary = "获取业务字典分页"
   )
   @SaCheckPermission({"/admin/biz/dict/page"})
   @GetMapping({"/admin/biz/dict/page"})
   public CommonResult<Page<BizDict>> page(BizDictPageParam bizDictPageParam) {
      return CommonResult.data(this.bizDictService.page(bizDictPageParam));
   }

   @Operation(
      summary = "获取业务字典树"
   )
   @SaCheckPermission({"/admin/biz/dict/tree"})
   @GetMapping({"/admin/biz/dict/tree"})
   public CommonResult<List<Tree<String>>> tree() {
      return CommonResult.data(this.bizDictService.tree());
   }

   @Operation(
      summary = "获取所有字典树"
   )
   @GetMapping({"/admin/biz/dict/treeAll"})
   public CommonResult<List<Tree<String>>> treeAll() {
      return CommonResult.data(this.bizDictService.treeAll());
   }

   @Operation(
      summary = "编辑业务字典"
   )
   @CommonLog("编辑业务字典")
   @SaCheckPermission({"/admin/biz/dict/edit"})
   @PostMapping({"/admin/biz/dict/edit"})
   public CommonResult<String> edit(@RequestBody @Valid BizDictEditParam bizDictEditParam) {
      this.bizDictService.edit(bizDictEditParam);
      return CommonResult.ok();
   }
}

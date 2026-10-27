package com.rxinyun.admin.system.modular.index.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.rxinyun.admin.system.modular.index.service.SysIndexService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "系统首页控制器"
)
@ApiSupport(
   author = "RUNXIN_TEAM",
   order = 0
)
@RestController
@Validated
public class SysIndexController {
   @Resource
   private SysIndexService sysIndexService;
}

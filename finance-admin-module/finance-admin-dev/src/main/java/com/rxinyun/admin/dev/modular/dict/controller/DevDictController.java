package com.rxinyun.admin.dev.modular.dict.controller;

import com.rxinyun.admin.dev.modular.dict.service.DevDictService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "字典控制器"
)
@RestController
@Validated
public class DevDictController {
   @Resource
   private DevDictService devDictService;
}

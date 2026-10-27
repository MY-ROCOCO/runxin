package com.rxinyun.admin.dev.modular.message.controller;

import com.rxinyun.admin.dev.modular.message.service.DevMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "站内信控制器"
)
@RestController
@Validated
public class DevMessageController {
   @Resource
   private DevMessageService devMessageService;
}

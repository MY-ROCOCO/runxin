package com.rxinyun.admin.dev.modular.monitor.controller;

import com.rxinyun.admin.dev.modular.monitor.service.DevMonitorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "监控控制器"
)
@RestController
@Validated
public class DevMonitorController {
   @Resource
   private DevMonitorService devMonitorService;
}

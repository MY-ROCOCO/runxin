package com.rxinyun.core.license;

import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开源版授权信息展示。
 *
 * 仅用于兼容管理端首页的授权卡片，不做任何授权校验。
 */
@Tag(name = "开源版授权展示")
@RestController
public class OpenSourceLicenseController {

    @Operation(summary = "开源版授权信息（仅展示，不校验）")
    @GetMapping({"/admin/auth/b/getLicense"})
    public CommonResult<Map<String, Object>> getLicense() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "VALID");
        data.put("message", "开源版，无授权限制");
        data.put("serialNumber", "OPEN-SOURCE");
        data.put("productName", "Reckonkvm财务管理系统");
        data.put("productVersion", "开源版");
        data.put("domain", "无限制");
        data.put("expiresTime", "无限制");
        data.put("licenseId", "OPEN-SOURCE");
        data.put("customer", "开源版");
        data.put("edition", "community");
        data.put("domains", List.of("无限制"));
        data.put("ips", List.of());
        data.put("features", List.of("all"));
        data.put("limits", Map.of());
        data.put("bindMode", "none");
        data.put("instanceId", null);
        data.put("serverIps", List.of());
        data.put("kid", "open-source");
        data.put("issuedTime", "无限制");
        data.put("notBefore", "无限制");
        return CommonResult.data(data);
    }
}

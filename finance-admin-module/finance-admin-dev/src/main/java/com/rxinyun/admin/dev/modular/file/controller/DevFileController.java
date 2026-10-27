package com.rxinyun.admin.dev.modular.file.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.file.entity.DevFile;
import com.rxinyun.admin.dev.modular.file.enums.DevFileEngineTypeEnum;
import com.rxinyun.admin.dev.modular.file.param.DevFileIdParam;
import com.rxinyun.admin.dev.modular.file.param.DevFileListParam;
import com.rxinyun.admin.dev.modular.file.param.DevFilePageParam;
import com.rxinyun.admin.dev.modular.file.param.DevFileUrlListParam;
import com.rxinyun.admin.dev.modular.file.service.DevFileService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
   name = "文件控制器"
)
@RestController
@Validated
public class DevFileController {
   private static final String RUNXIN_SYS_DEFAULT_FILE_ENGINE_KEY = "RUNXIN_SYS_DEFAULT_FILE_ENGINE";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private DevFileService devFileService;

   @Operation(
      summary = "动态上传文件返回id"
   )
   @CommonLog("动态上传文件返回id")
   @PostMapping({"/admin/dev/file/uploadDynamicReturnId"})
   public CommonResult<String> uploadDynamicReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnId(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_FILE_ENGINE"), file));
   }

   @Operation(
      summary = "动态上传文件返回url"
   )
   @CommonLog("动态上传文件返回url")
   @PostMapping({"/admin/dev/file/uploadDynamicReturnUrl"})
   public CommonResult<String> uploadDynamicReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnUrl(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_FILE_ENGINE"), file));
   }

   @Operation(
      summary = "上传本地文件返回id"
   )
   @CommonLog("上传本地文件返回id")
   @PostMapping({"/admin/dev/file/uploadLocalReturnId"})
   public CommonResult<String> uploadLocalReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnId(DevFileEngineTypeEnum.LOCAL.getValue(), file));
   }

   @Operation(
      summary = "上传本地文件返回url"
   )
   @CommonLog("上传本地文件返回url")
   @PostMapping({"/admin/dev/file/uploadLocalReturnUrl"})
   public CommonResult<String> uploadLocalReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.LOCAL.getValue(), file));
   }

   @Operation(
      summary = "上传阿里云文件返回id"
   )
   @CommonLog("上传阿里云文件返回id")
   @PostMapping({"/admin/dev/file/uploadAliyunReturnId"})
   public CommonResult<String> uploadAliyunReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnId(DevFileEngineTypeEnum.ALIYUN.getValue(), file));
   }

   @Operation(
      summary = "上传阿里云文件返回url"
   )
   @CommonLog("上传阿里云文件返回url")
   @PostMapping({"/admin/dev/file/uploadAliyunReturnUrl"})
   public CommonResult<String> uploadAliyunReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.ALIYUN.getValue(), file));
   }

   @Operation(
      summary = "上传腾讯云文件返回id"
   )
   @CommonLog("上传腾讯云文件返回id")
   @PostMapping({"/admin/dev/file/uploadTencentReturnId"})
   public CommonResult<String> uploadTencentReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnId(DevFileEngineTypeEnum.TENCENT.getValue(), file));
   }

   @Operation(
      summary = "上传腾讯云文件返回url"
   )
   @CommonLog("上传腾讯云文件返回url")
   @PostMapping({"/admin/dev/file/uploadTencentReturnUrl"})
   public CommonResult<String> uploadTencentReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.TENCENT.getValue(), file));
   }

   @Operation(
      summary = "上传MINIO文件返回id"
   )
   @CommonLog("上传MINIO文件返回id")
   @PostMapping({"/admin/dev/file/uploadMinioReturnId"})
   public CommonResult<String> uploadMinioReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnId(DevFileEngineTypeEnum.MINIO.getValue(), file));
   }

   @Operation(
      summary = "上传MINIO文件返回url"
   )
   @CommonLog("上传MINIO文件返回url")
   @PostMapping({"/admin/dev/file/uploadMinioReturnUrl"})
   public CommonResult<String> uploadMinioReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.MINIO.getValue(), file));
   }

   @Operation(
      summary = "获取文件分页列表"
   )
   @GetMapping({"/admin/dev/file/page"})
   public CommonResult<Page<DevFile>> page(DevFilePageParam devFilePageParam) {
      return CommonResult.data(this.devFileService.page(devFilePageParam));
   }

   @Operation(
      summary = "获取文件列表"
   )
   @GetMapping({"/admin/dev/file/list"})
   public CommonResult<List<DevFile>> list(DevFileListParam devFileListParam) {
      return CommonResult.data(this.devFileService.list(devFileListParam));
   }

   @Operation(
      summary = "下载文件"
   )
   @CommonLog("下载文件")
   @GetMapping(
      value = {"/admin/dev/file/download"},
      produces = {"application/octet-stream"}
   )
   public void download(@Valid DevFileIdParam devFileIdParam, HttpServletResponse response) throws IOException {
      this.devFileService.download(devFileIdParam, response);
   }

   @Operation(
      summary = "删除文件"
   )
   @CommonLog("删除文件")
   @PostMapping({"/admin/dev/file/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DevFileIdParam> devFileIdParamList) {
      this.devFileService.delete(devFileIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取文件详情"
   )
   @GetMapping({"/admin/dev/file/detail"})
   public CommonResult<DevFile> detail(@Valid DevFileIdParam devFileIdParam) {
      return CommonResult.data(this.devFileService.detail(devFileIdParam));
   }

   @Operation(
      summary = "根据文件url集合获取文件集合"
   )
   @PostMapping({"/admin/dev/file/getFileListByUrlList"})
   public CommonResult<List<DevFile>> getFileListByUrlList(@RequestBody @Valid DevFileUrlListParam devFileUrlListParam) {
      return CommonResult.data(this.devFileService.getFileListByUrlList(devFileUrlListParam));
   }
}

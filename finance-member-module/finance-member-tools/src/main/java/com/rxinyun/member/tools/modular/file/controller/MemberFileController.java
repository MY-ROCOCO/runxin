package com.rxinyun.member.tools.modular.file.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.tools.modular.file.entity.MemberFile;
import com.rxinyun.member.tools.modular.file.enums.MemberFileEngineTypeEnum;
import com.rxinyun.member.tools.modular.file.param.MemberFileIdParam;
import com.rxinyun.member.tools.modular.file.param.MemberFileListParam;
import com.rxinyun.member.tools.modular.file.param.MemberFilePageParam;
import com.rxinyun.member.tools.modular.file.param.MemberFileUrlListParam;
import com.rxinyun.member.tools.modular.file.service.MemberFileService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
   name = "文件控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberFileController {
   private static final String RUNXIN_SYS_DEFAULT_FILE_ENGINE_KEY = "RUNXIN_SYS_DEFAULT_FILE_ENGINE";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private MemberFileService memberFileService;

   @Operation(
      summary = "动态上传文件返回id"
   )
   @CommonLog("动态上传文件返回id")
   @PostMapping({"/tools/file/uploadDynamicReturnId"})
   public CommonResult<String> uploadDynamicReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnId(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_FILE_ENGINE"), file));
   }

   @Operation(
      summary = "动态上传文件返回url"
   )
   @CommonLog("动态上传文件返回url")
   @PostMapping({"/tools/file/uploadDynamicReturnUrl"})
   public CommonResult<String> uploadDynamicReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnUrl(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_FILE_ENGINE"), file));
   }

   @Operation(
      summary = "上传本地文件返回id"
   )
   @CommonLog("上传本地文件返回id")
   @PostMapping({"/tools/file/uploadLocalReturnId"})
   public CommonResult<String> uploadLocalReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.LOCAL.getValue(), file));
   }

   @Operation(
      summary = "上传本地文件返回url"
   )
   @CommonLog("上传本地文件返回url")
   @PostMapping({"/tools/file/uploadLocalReturnUrl"})
   public CommonResult<String> uploadLocalReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.LOCAL.getValue(), file));
   }

   @Operation(
      summary = "上传阿里云文件返回id"
   )
   @CommonLog("上传阿里云文件返回id")
   @PostMapping({"/tools/file/uploadAliyunReturnId"})
   public CommonResult<String> uploadAliyunReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.ALIYUN.getValue(), file));
   }

   @Operation(
      summary = "上传阿里云文件返回url"
   )
   @CommonLog("上传阿里云文件返回url")
   @PostMapping({"/tools/file/uploadAliyunReturnUrl"})
   public CommonResult<String> uploadAliyunReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.ALIYUN.getValue(), file));
   }

   @Operation(
      summary = "上传腾讯云文件返回id"
   )
   @CommonLog("上传腾讯云文件返回id")
   @PostMapping({"/tools/file/uploadTencentReturnId"})
   public CommonResult<String> uploadTencentReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.TENCENT.getValue(), file));
   }

   @Operation(
      summary = "上传腾讯云文件返回url"
   )
   @CommonLog("上传腾讯云文件返回url")
   @PostMapping({"/tools/file/uploadTencentReturnUrl"})
   public CommonResult<String> uploadTencentReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.TENCENT.getValue(), file));
   }

   @Operation(
      summary = "上传MINIO文件返回id"
   )
   @CommonLog("上传MINIO文件返回id")
   @PostMapping({"/tools/file/uploadMinioReturnId"})
   public CommonResult<String> uploadMinioReturnId(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.MINIO.getValue(), file));
   }

   @Operation(
      summary = "上传MINIO文件返回url"
   )
   @CommonLog("上传MINIO文件返回url")
   @PostMapping({"/tools/file/uploadMinioReturnUrl"})
   public CommonResult<String> uploadMinioReturnUrl(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.MINIO.getValue(), file));
   }

   @Operation(
      summary = "获取文件分页列表"
   )
   @GetMapping({"/tools/file/page"})
   public CommonResult<Page<MemberFile>> page(MemberFilePageParam memberFilePageParam) {
      return CommonResult.data(this.memberFileService.page(memberFilePageParam));
   }

   @Operation(
      summary = "获取文件列表"
   )
   @GetMapping({"/tools/file/list"})
   public CommonResult<List<MemberFile>> list(MemberFileListParam memberFileListParam) {
      return CommonResult.data(this.memberFileService.list(memberFileListParam));
   }

   @Operation(
      summary = "下载文件"
   )
   @CommonLog("下载文件")
   @GetMapping(
      value = {"/tools/file/download"},
      produces = {"application/octet-stream"}
   )
   public void download(@Valid MemberFileIdParam memberFileIdParam, HttpServletResponse response) throws IOException {
      this.memberFileService.download(memberFileIdParam, response);
   }

   @Operation(
      summary = "下载文件"
   )
   @CommonLog("下载文件")
   @GetMapping(
      value = {"/tools/file/download/{fileName:.+}"},
      produces = {"application/octet-stream"}
   )
   public void downloadByFileName(@PathVariable String fileName, HttpServletResponse response) throws IOException {
      MemberFileIdParam memberFileIdParam = new MemberFileIdParam();
      int suffixIndex = fileName.indexOf(46);
      memberFileIdParam.setId(suffixIndex > 0 ? fileName.substring(0, suffixIndex) : fileName);
      this.memberFileService.download(memberFileIdParam, response);
   }

   @Operation(
      summary = "删除文件"
   )
   @CommonLog("删除文件")
   @PostMapping({"/tools/file/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<MemberFileIdParam> memberFileIdParamList) {
      this.memberFileService.delete(memberFileIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取文件详情"
   )
   @GetMapping({"/tools/file/detail"})
   public CommonResult<MemberFile> detail(@Valid MemberFileIdParam memberFileIdParam) {
      return CommonResult.data(this.memberFileService.detail(memberFileIdParam));
   }

   @Operation(
      summary = "根据文件url集合获取文件集合"
   )
   @PostMapping({"/tools/file/getFileListByUrlList"})
   public CommonResult<List<MemberFile>> getFileListByUrlList(@RequestBody @Valid MemberFileUrlListParam memberFileUrlListParam) {
      return CommonResult.data(this.memberFileService.getFileListByUrlList(memberFileUrlListParam));
   }
}

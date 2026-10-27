package com.rxinyun.member.tools.modular.file.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.prop.CommonProperties;
import com.rxinyun.common.util.CommonDownloadUtil;
import com.rxinyun.common.util.CommonResponseUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.tools.modular.file.entity.MemberFile;
import com.rxinyun.member.tools.modular.file.enums.MemberFileEngineTypeEnum;
import com.rxinyun.member.tools.modular.file.mapper.MemberFileMapper;
import com.rxinyun.member.tools.modular.file.param.MemberFileIdParam;
import com.rxinyun.member.tools.modular.file.param.MemberFileListParam;
import com.rxinyun.member.tools.modular.file.param.MemberFilePageParam;
import com.rxinyun.member.tools.modular.file.param.MemberFileUrlListParam;
import com.rxinyun.member.tools.modular.file.service.MemberFileService;
import com.rxinyun.member.tools.modular.file.util.MemberFileAliyunUtil;
import com.rxinyun.member.tools.modular.file.util.MemberFileLocalUtil;
import com.rxinyun.member.tools.modular.file.util.MemberFileMinIoUtil;
import com.rxinyun.member.tools.modular.file.util.MemberFileTencentUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MemberFileServiceImpl extends ServiceImpl<MemberFileMapper, MemberFile> implements MemberFileService {
   @Resource
   private CommonProperties commonProperties;
   @Resource
   private DevConfigApi devConfigApi;
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";

   @Override
   public String uploadReturnId(String engine, MultipartFile file) {
      this.requireClientLogin();
      return this.storageFile(engine, file, true);
   }

   @Override
   public String uploadReturnUrl(String engine, MultipartFile file) {
      this.requireClientLogin();
      return this.storageFile(engine, file, false);
   }

   @Override
   public Page<MemberFile> page(MemberFilePageParam devFilePageParam) {
      this.requireClientLogin();
      QueryWrapper<MemberFile> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devFilePageParam.getEngine())) {
         queryWrapper.lambda().eq(MemberFile::getEngine, devFilePageParam.getEngine());
      }

      if (ObjectUtil.isNotEmpty(devFilePageParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberFile::getName, devFilePageParam.getSearchKey());
      }

      return (Page<MemberFile>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<MemberFile> list(MemberFileListParam devFileListParam) {
      this.requireClientLogin();
      QueryWrapper<MemberFile> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devFileListParam.getEngine())) {
         queryWrapper.lambda().eq(MemberFile::getEngine, devFileListParam.getEngine());
      }

      if (ObjectUtil.isNotEmpty(devFileListParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberFile::getName, devFileListParam.getSearchKey());
      }

      return this.list(queryWrapper);
   }

   @Override
   public void download(MemberFileIdParam devFileIdParam, HttpServletResponse response) throws IOException {
      this.requireClientLogin();

      MemberFile memberFile;
      try {
         memberFile = this.queryEntity(devFileIdParam.getId());
      } catch (Exception var5) {
         CommonResponseUtil.renderError(response, var5.getMessage());
         return;
      }

      if (!memberFile.getEngine().equals(MemberFileEngineTypeEnum.LOCAL.getValue())) {
         CommonResponseUtil.renderError(response, "非本地文件不支持此方式下载，id值为：" + memberFile.getId());
      } else {
         File file = FileUtil.file(memberFile.getStoragePath());
         if (!FileUtil.exist(file)) {
            CommonResponseUtil.renderError(response, "找不到存储的文件，id值为：" + memberFile.getId());
         } else {
            CommonDownloadUtil.download(memberFile.getName(), IoUtil.readBytes(FileUtil.getInputStream(file)), response);
         }
      }
   }

   @Override
   public void delete(List<MemberFileIdParam> devFileIdParamList) {
      this.requireClientLogin();
      this.removeByIds(CollStreamUtil.toList(devFileIdParamList, MemberFileIdParam::getId));
   }

   private String storageFile(String engine, MultipartFile file, boolean returnFileId) {
      if (ObjectUtil.isEmpty(engine)) {
         engine = MemberFileEngineTypeEnum.LOCAL.getValue();
      }

      String fileId = IdWorker.getIdStr();
      String bucketName;
      String storageUrl;
      if (engine.equals(MemberFileEngineTypeEnum.LOCAL.getValue())) {
         bucketName = "defaultBucketName";
         storageUrl = MemberFileLocalUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      } else if (engine.equals(MemberFileEngineTypeEnum.ALIYUN.getValue())) {
         bucketName = MemberFileAliyunUtil.getDefaultBucketName();
         storageUrl = MemberFileAliyunUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      } else if (engine.equals(MemberFileEngineTypeEnum.TENCENT.getValue())) {
         bucketName = MemberFileTencentUtil.getDefaultBucketName();
         storageUrl = MemberFileTencentUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      } else {
         if (!engine.equals(MemberFileEngineTypeEnum.MINIO.getValue())) {
            throw new CommonException("不支持的文件引擎：{}", new Object[]{engine});
         }

         bucketName = MemberFileMinIoUtil.getDefaultBucketName();
         storageUrl = MemberFileMinIoUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      }

      MemberFile memberFile = new MemberFile();
      memberFile.setId(fileId);
      memberFile.setEngine(engine);
      memberFile.setBucket(bucketName);
      memberFile.setName(file.getOriginalFilename());
      String suffix = ObjectUtil.isNotEmpty(file.getOriginalFilename()) ? StrUtil.subAfter(file.getOriginalFilename(), ".", true) : null;
      memberFile.setSuffix(suffix);
      memberFile.setSizeKb(Convert.toStr(NumberUtil.div(new BigDecimal(file.getSize()), BigDecimal.valueOf(1024L)).setScale(0, RoundingMode.HALF_UP)));
      memberFile.setSizeInfo(FileUtil.readableFileSize(file.getSize()));
      memberFile.setObjName(ObjectUtil.isNotEmpty(memberFile.getSuffix()) ? fileId + "." + memberFile.getSuffix() : null);
      if (ObjectUtil.isNotEmpty(suffix) && isPic(suffix)) {
         try {
            memberFile.setThumbnail(buildThumbnailBase64(file.getBytes(), suffix));
         } catch (Exception var13) {
         }
      }

      memberFile.setStoragePath(storageUrl);
      String downloadUrl;
      if (engine.equals(MemberFileEngineTypeEnum.LOCAL.getValue())) {
         String apiUrl = this.devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL");
         boolean fromHomeUrl = StrUtil.isNotBlank(apiUrl);
         if (fromHomeUrl) {
            apiUrl = apiUrl.trim();
            if (apiUrl.endsWith("/")) {
               apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
            }
         }

         if (StrUtil.isBlank(apiUrl)) {
            apiUrl = this.commonProperties.getBackendUrl();
         }

         if (ObjectUtil.isEmpty(apiUrl)) {
            throw new CommonException("后端域名地址未正确配置：请配置 RUNXIN_SYS_HOME_URL（网站网址）或 runxin.config.common.backend-url", new Object[0]);
         }

         if (fromHomeUrl && StrUtil.isNotBlank(this.commonProperties.getBackendUrl())) {
            apiUrl = appendPortFromBackendIfMissing(apiUrl, this.commonProperties.getBackendUrl());
         }

         String fileName = StrUtil.isNotBlank(suffix) ? fileId + "." + suffix : fileId;
         downloadUrl = apiUrl + "/member/tools/file/download/" + fileName;
         memberFile.setDownloadPath(downloadUrl);
      } else {
         downloadUrl = storageUrl;
         memberFile.setDownloadPath(memberFile.getStoragePath());
      }

      this.save(memberFile);
      if (returnFileId) {
         return fileId;
      } else {
         return StrUtil.isNotBlank(memberFile.getThumbnail()) ? memberFile.getThumbnail() : downloadUrl;
      }
   }

   private static String appendPortFromBackendIfMissing(String baseUrl, String backendUrl) {
      try {
         URI base = URI.create(baseUrl);
         URI backend = URI.create(backendUrl.trim());
         int backendPort = backend.getPort();
         if (backendPort <= 0) {
            return baseUrl;
         } else {
            boolean baseHasPort = base.getPort() > 0;
            if (baseHasPort) {
               return baseUrl;
            } else {
               String host = base.getHost();
               if (host == null) {
                  return baseUrl;
               } else {
                  String scheme = backend.getScheme();
                  if (StrUtil.isBlank(scheme)) {
                     scheme = "http";
                  }

                  return scheme + "://" + host + ":" + backendPort;
               }
            }
         }
      } catch (Exception var8) {
         return baseUrl;
      }
   }

   private void requireClientLogin() {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (clientLoginUser == null || clientLoginUser.getId() == null) {
         throw new CommonException("请先登录", new Object[0]);
      }
   }

   public String genFileKey(String fileId, MultipartFile file) {
      String originalFileName = file.getOriginalFilename();
      String fileSuffix = FileUtil.getSuffix(originalFileName);
      String fileObjectName = fileId + "." + fileSuffix;
      String dateFolderPath = DateUtil.thisYear() + "/" + (DateUtil.thisMonth() + 1) + "/" + DateUtil.thisDayOfMonth() + "/";
      return dateFolderPath + fileObjectName;
   }

   @Override
   public MemberFile detail(MemberFileIdParam devFileIdParam) {
      this.requireClientLogin();
      return this.queryEntity(devFileIdParam.getId());
   }

   @Override
   public List<MemberFile> getFileListByUrlList(MemberFileUrlListParam devFileUrlListParam) {
      this.requireClientLogin();
      LambdaQueryWrapper<MemberFile> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
            
               MemberFile::getId,
               MemberFile::getSuffix,
               MemberFile::getDownloadPath,
               MemberFile::getName,
               MemberFile::getThumbnail,
               MemberFile::getSizeKb,
               MemberFile::getSizeInfo,
               MemberFile::getObjName
            
         )
         .in(MemberFile::getDownloadPath, devFileUrlListParam.getUrlList());
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public MemberFile queryEntity(String id) {
      MemberFile devFile = (MemberFile)this.getById(id);
      if (ObjectUtil.isEmpty(devFile)) {
         throw new CommonException("文件不存在，id值为：{}", new Object[]{id});
      } else {
         return devFile;
      }
   }

   private static String buildThumbnailBase64(byte[] imageBytes, String suffix) {
      if ("ico".equalsIgnoreCase(suffix)) {
         return "data:image/x-icon;base64," + Base64.getEncoder().encodeToString(imageBytes);
      } else {
         BufferedImage image = ImgUtil.toImage(imageBytes);
         return ImgUtil.toBase64DataUri(image, suffix);
      }
   }

   private static boolean isPic(String fileSuffix) {
      fileSuffix = fileSuffix.toLowerCase();
      return "gif".equals(fileSuffix)
         || "jpg".equals(fileSuffix)
         || "jpeg".equals(fileSuffix)
         || "bmp".equals(fileSuffix)
         || "png".equals(fileSuffix)
         || "psd".equals(fileSuffix)
         || "ico".equals(fileSuffix);
   }
}

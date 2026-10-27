package com.rxinyun.admin.dev.modular.file.service.impl;

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
import com.rxinyun.admin.dev.modular.file.entity.DevFile;
import com.rxinyun.admin.dev.modular.file.enums.DevFileEngineTypeEnum;
import com.rxinyun.admin.dev.modular.file.mapper.DevFileMapper;
import com.rxinyun.admin.dev.modular.file.param.DevFileIdParam;
import com.rxinyun.admin.dev.modular.file.param.DevFileListParam;
import com.rxinyun.admin.dev.modular.file.param.DevFilePageParam;
import com.rxinyun.admin.dev.modular.file.param.DevFileUrlListParam;
import com.rxinyun.admin.dev.modular.file.service.DevFileService;
import com.rxinyun.admin.dev.modular.file.util.DevFileAliyunUtil;
import com.rxinyun.admin.dev.modular.file.util.DevFileLocalUtil;
import com.rxinyun.admin.dev.modular.file.util.DevFileMinIoUtil;
import com.rxinyun.admin.dev.modular.file.util.DevFileTencentUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.prop.CommonProperties;
import com.rxinyun.common.util.CommonDownloadUtil;
import com.rxinyun.common.util.CommonResponseUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.dev.api.DevConfigApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
public class DevFileServiceImpl extends ServiceImpl<DevFileMapper, DevFile> implements DevFileService {
   @Resource
   private CommonProperties commonProperties;
   @Resource
   private DevConfigApi devConfigApi;
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";

   @Override
   public String uploadReturnId(String engine, MultipartFile file) {
      return this.storageFile(engine, file, true);
   }

   @Override
   public String uploadReturnUrl(String engine, MultipartFile file) {
      return this.storageFile(engine, file, false);
   }

   @Override
   public Page<DevFile> page(DevFilePageParam devFilePageParam) {
      QueryWrapper<DevFile> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devFilePageParam.getEngine())) {
         queryWrapper.lambda().eq(DevFile::getEngine, devFilePageParam.getEngine());
      }

      if (ObjectUtil.isNotEmpty(devFilePageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevFile::getName, devFilePageParam.getSearchKey());
      }

      return (Page<DevFile>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<DevFile> list(DevFileListParam devFileListParam) {
      QueryWrapper<DevFile> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(devFileListParam.getEngine())) {
         queryWrapper.lambda().eq(DevFile::getEngine, devFileListParam.getEngine());
      }

      if (ObjectUtil.isNotEmpty(devFileListParam.getSearchKey())) {
         queryWrapper.lambda().like(DevFile::getName, devFileListParam.getSearchKey());
      }

      return this.list(queryWrapper);
   }

   @Override
   public void download(DevFileIdParam devFileIdParam, HttpServletResponse response) throws IOException {
      DevFile devFile;
      try {
         devFile = this.queryEntity(devFileIdParam.getId());
      } catch (Exception var5) {
         CommonResponseUtil.renderError(response, var5.getMessage());
         return;
      }

      if (!devFile.getEngine().equals(DevFileEngineTypeEnum.LOCAL.getValue())) {
         CommonResponseUtil.renderError(response, "非本地文件不支持此方式下载，id值为：" + devFile.getId());
      } else {
         File file = FileUtil.file(devFile.getStoragePath());
         if (!FileUtil.exist(file)) {
            CommonResponseUtil.renderError(response, "找不到存储的文件，id值为：" + devFile.getId());
         } else {
            CommonDownloadUtil.download(devFile.getName(), IoUtil.readBytes(FileUtil.getInputStream(file)), response);
         }
      }
   }

   @Override
   public void delete(List<DevFileIdParam> devFileIdParamList) {
      this.removeByIds(CollStreamUtil.toList(devFileIdParamList, DevFileIdParam::getId));
   }

   private String storageFile(String engine, MultipartFile file, boolean returnFileId) {
      if (ObjectUtil.isEmpty(engine)) {
         engine = DevFileEngineTypeEnum.LOCAL.getValue();
      }

      String fileId = IdWorker.getIdStr();
      String bucketName;
      String storageUrl;
      if (engine.equals(DevFileEngineTypeEnum.LOCAL.getValue())) {
         bucketName = "defaultBucketName";
         storageUrl = DevFileLocalUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      } else if (engine.equals(DevFileEngineTypeEnum.ALIYUN.getValue())) {
         bucketName = DevFileAliyunUtil.getDefaultBucketName();
         storageUrl = DevFileAliyunUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      } else if (engine.equals(DevFileEngineTypeEnum.TENCENT.getValue())) {
         bucketName = DevFileTencentUtil.getDefaultBucketName();
         storageUrl = DevFileTencentUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      } else {
         if (!engine.equals(DevFileEngineTypeEnum.MINIO.getValue())) {
            throw new CommonException("不支持的文件引擎：{}", new Object[]{engine});
         }

         bucketName = DevFileMinIoUtil.getDefaultBucketName();
         storageUrl = DevFileMinIoUtil.storageFileWithReturnUrl(bucketName, this.genFileKey(fileId, file), file);
      }

      DevFile devFile = new DevFile();
      devFile.setId(fileId);
      devFile.setEngine(engine);
      devFile.setBucket(bucketName);
      devFile.setName(file.getOriginalFilename());
      String suffix = ObjectUtil.isNotEmpty(file.getOriginalFilename()) ? StrUtil.subAfter(file.getOriginalFilename(), ".", true) : null;
      devFile.setSuffix(suffix);
      devFile.setSizeKb(Convert.toStr(NumberUtil.div(new BigDecimal(file.getSize()), BigDecimal.valueOf(1024L)).setScale(0, RoundingMode.HALF_UP)));
      devFile.setSizeInfo(FileUtil.readableFileSize(file.getSize()));
      devFile.setObjName(ObjectUtil.isNotEmpty(devFile.getSuffix()) ? fileId + "." + devFile.getSuffix() : null);
      if (ObjectUtil.isNotEmpty(suffix) && isPic(suffix)) {
         try {
            devFile.setThumbnail(buildThumbnailBase64(file.getBytes(), suffix));
         } catch (Exception var12) {
         }
      }

      devFile.setStoragePath(storageUrl);
      String downloadUrl;
      if (engine.equals(DevFileEngineTypeEnum.LOCAL.getValue())) {
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

         apiUrl = ensureSchemeMatchesRequest(apiUrl);
         downloadUrl = apiUrl + "/dev/file/download?id=" + fileId;
         devFile.setDownloadPath(downloadUrl);
      } else {
         downloadUrl = storageUrl;
         devFile.setDownloadPath(devFile.getStoragePath());
      }

      this.save(devFile);
      if (returnFileId) {
         return fileId;
      } else {
         return StrUtil.isNotBlank(devFile.getThumbnail()) ? devFile.getThumbnail() : downloadUrl;
      }
   }

   private static String ensureSchemeMatchesRequest(String baseUrl) {
      if (StrUtil.isBlank(baseUrl)) {
         return baseUrl;
      } else if (!CommonServletUtil.isWeb()) {
         return baseUrl;
      } else {
         try {
            HttpServletRequest request = CommonServletUtil.getRequest();
            String requestScheme = request.getHeader("X-Forwarded-Proto");
            if (StrUtil.isBlank(requestScheme)) {
               requestScheme = request.getScheme();
            }

            if (StrUtil.isBlank(requestScheme)) {
               return baseUrl;
            } else {
               URI uri = URI.create(baseUrl);
               String currentScheme = uri.getScheme();
               return requestScheme.equalsIgnoreCase(currentScheme)
                  ? baseUrl
                  : baseUrl.replaceFirst("^" + (currentScheme != null ? currentScheme : "http") + "(?=:)", requestScheme);
            }
         } catch (Exception var5) {
            return baseUrl;
         }
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

   public String genFileKey(String fileId, MultipartFile file) {
      String originalFileName = file.getOriginalFilename();
      String fileSuffix = FileUtil.getSuffix(originalFileName);
      String fileObjectName = fileId + "." + fileSuffix;
      String dateFolderPath = DateUtil.thisYear() + "/" + (DateUtil.thisMonth() + 1) + "/" + DateUtil.thisDayOfMonth() + "/";
      return dateFolderPath + fileObjectName;
   }

   @Override
   public DevFile detail(DevFileIdParam devFileIdParam) {
      return this.queryEntity(devFileIdParam.getId());
   }

   @Override
   public List<DevFile> getFileListByUrlList(DevFileUrlListParam devFileUrlListParam) {
      LambdaQueryWrapper<DevFile> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
            
               DevFile::getId,
               DevFile::getSuffix,
               DevFile::getDownloadPath,
               DevFile::getName,
               DevFile::getThumbnail,
               DevFile::getSizeKb,
               DevFile::getSizeInfo,
               DevFile::getObjName
            
         )
         .in(DevFile::getDownloadPath, devFileUrlListParam.getUrlList());
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public DevFile queryEntity(String id) {
      DevFile devFile = (DevFile)this.getById(id);
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

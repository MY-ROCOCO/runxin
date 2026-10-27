package com.rxinyun.admin.dev.modular.file.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.rxinyun.admin.dev.modular.file.entity.DevFile;
import com.rxinyun.admin.dev.modular.file.enums.DevFileEngineTypeEnum;
import com.rxinyun.admin.dev.modular.file.service.DevFileService;
import com.rxinyun.dev.api.DevFileApi;
import com.rxinyun.dev.core.pojo.BaseDevFile;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DevFileApiProvider implements DevFileApi {
   @Resource
   private DevFileService devFileService;

   public String storageFileWithReturnUrlLocal(MultipartFile file) {
      return this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.LOCAL.getValue(), file);
   }

   public String storageFileWithReturnIdLocal(MultipartFile file) {
      return this.devFileService.uploadReturnId(DevFileEngineTypeEnum.LOCAL.getValue(), file);
   }

   public String storageFileWithReturnUrlAliyun(MultipartFile file) {
      return this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.ALIYUN.getValue(), file);
   }

   public String storageFileWithReturnIdAliyun(MultipartFile file) {
      return this.devFileService.uploadReturnId(DevFileEngineTypeEnum.ALIYUN.getValue(), file);
   }

   public String storageFileWithReturnUrlTencent(MultipartFile file) {
      return this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.TENCENT.getValue(), file);
   }

   public String storageFileWithReturnIdTencent(MultipartFile file) {
      return this.devFileService.uploadReturnId(DevFileEngineTypeEnum.TENCENT.getValue(), file);
   }

   public String storageFileWithReturnUrlMinio(MultipartFile file) {
      return this.devFileService.uploadReturnUrl(DevFileEngineTypeEnum.MINIO.getValue(), file);
   }

   public String storageFileWithReturnIdMinio(MultipartFile file) {
      return this.devFileService.uploadReturnId(DevFileEngineTypeEnum.MINIO.getValue(), file);
   }

   public BaseDevFile getFileById(String fileId) {
      return fileId != null && !fileId.trim().isEmpty()
         ? this.toBaseDevFile(
            (DevFile)this.devFileService
               .getOne(
                  ((LambdaQueryWrapper<DevFile>)new LambdaQueryWrapper<DevFile>().eq(DevFile::getId, fileId))
                     .select(DevFile::getId, DevFile::getName, DevFile::getSuffix, DevFile::getDownloadPath, DevFile::getThumbnail)
               )
         )
         : null;
   }

   public BaseDevFile getFileByUrl(String fileUrl) {
      return fileUrl != null && !fileUrl.trim().isEmpty()
         ? this.toBaseDevFile(
            (DevFile)this.devFileService
               .getOne(
                  ((LambdaQueryWrapper<DevFile>)new LambdaQueryWrapper<DevFile>().eq(DevFile::getDownloadPath, fileUrl))
                     .select(DevFile::getId, DevFile::getName, DevFile::getSuffix, DevFile::getDownloadPath, DevFile::getThumbnail)
               )
         )
         : null;
   }

   private BaseDevFile toBaseDevFile(DevFile devFile) {
      if (devFile == null) {
         return null;
      } else {
         BaseDevFile baseDevFile = new BaseDevFile();
         baseDevFile.setId(devFile.getId());
         baseDevFile.setName(devFile.getName());
         baseDevFile.setSuffix(devFile.getSuffix());
         baseDevFile.setDownloadPath(devFile.getDownloadPath());
         baseDevFile.setThumbnail(devFile.getThumbnail());
         return baseDevFile;
      }
   }
}

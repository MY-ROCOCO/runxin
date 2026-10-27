package com.rxinyun.member.tools.modular.file.provider;

import com.rxinyun.dev.api.MemberFileApi;
import com.rxinyun.member.tools.modular.file.enums.MemberFileEngineTypeEnum;
import com.rxinyun.member.tools.modular.file.service.MemberFileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MemberFileApiProvider implements MemberFileApi {
   @Resource
   private MemberFileService memberFileService;

   public String storageFileWithReturnUrlLocal(MultipartFile file) {
      return this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.LOCAL.getValue(), file);
   }

   public String storageFileWithReturnIdLocal(MultipartFile file) {
      return this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.LOCAL.getValue(), file);
   }

   public String storageFileWithReturnUrlAliyun(MultipartFile file) {
      return this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.ALIYUN.getValue(), file);
   }

   public String storageFileWithReturnIdAliyun(MultipartFile file) {
      return this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.ALIYUN.getValue(), file);
   }

   public String storageFileWithReturnUrlTencent(MultipartFile file) {
      return this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.TENCENT.getValue(), file);
   }

   public String storageFileWithReturnIdTencent(MultipartFile file) {
      return this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.TENCENT.getValue(), file);
   }

   public String storageFileWithReturnUrlMinio(MultipartFile file) {
      return this.memberFileService.uploadReturnUrl(MemberFileEngineTypeEnum.MINIO.getValue(), file);
   }

   public String storageFileWithReturnIdMinio(MultipartFile file) {
      return this.memberFileService.uploadReturnId(MemberFileEngineTypeEnum.MINIO.getValue(), file);
   }
}

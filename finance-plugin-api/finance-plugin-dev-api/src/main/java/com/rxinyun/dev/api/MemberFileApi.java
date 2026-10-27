package com.rxinyun.dev.api;

import org.springframework.web.multipart.MultipartFile;

public interface MemberFileApi {
   String storageFileWithReturnUrlLocal(MultipartFile file);

   String storageFileWithReturnIdLocal(MultipartFile file);

   String storageFileWithReturnUrlAliyun(MultipartFile file);

   String storageFileWithReturnIdAliyun(MultipartFile file);

   String storageFileWithReturnUrlTencent(MultipartFile file);

   String storageFileWithReturnIdTencent(MultipartFile file);

   String storageFileWithReturnUrlMinio(MultipartFile file);

   String storageFileWithReturnIdMinio(MultipartFile file);
}

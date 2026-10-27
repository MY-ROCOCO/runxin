package com.rxinyun.dev.api;

import com.rxinyun.dev.core.pojo.BaseDevFile;
import org.springframework.web.multipart.MultipartFile;

public interface DevFileApi {
   String storageFileWithReturnUrlLocal(MultipartFile file);

   String storageFileWithReturnIdLocal(MultipartFile file);

   String storageFileWithReturnUrlAliyun(MultipartFile file);

   String storageFileWithReturnIdAliyun(MultipartFile file);

   String storageFileWithReturnUrlTencent(MultipartFile file);

   String storageFileWithReturnIdTencent(MultipartFile file);

   String storageFileWithReturnUrlMinio(MultipartFile file);

   String storageFileWithReturnIdMinio(MultipartFile file);

   BaseDevFile getFileById(String fileId);

   BaseDevFile getFileByUrl(String fileUrl);
}

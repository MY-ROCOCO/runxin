package com.rxinyun.admin.dev.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.rxinyun.admin.dev.modular.file.enums.DevFileBucketAuthEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class DevFileLocalUtil {
   private static final Logger log = LoggerFactory.getLogger(DevFileLocalUtil.class);
   private static JSONObject client;
   private static final String RUNXIN_FILE_LOCAL_FOLDER_FOR_WINDOWS_KEY = "RUNXIN_FILE_LOCAL_FOLDER_FOR_WINDOWS";
   private static final String RUNXIN_FILE_LOCAL_FOLDER_FOR_UNIX_KEY = "RUNXIN_FILE_LOCAL_FOLDER_FOR_UNIX";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String uploadFileFolder;
      if (SystemUtil.getOsInfo().isWindows()) {
         String localFolderForWindows = devConfigApi.getValueByKey("RUNXIN_FILE_LOCAL_FOLDER_FOR_WINDOWS");
         if (ObjectUtil.isEmpty(localFolderForWindows)) {
            throw new CommonException("本地文件操作客户端未正确配置：RUNXIN_FILE_LOCAL_FOLDER_FOR_WINDOWS为空", new Object[0]);
         }

         uploadFileFolder = localFolderForWindows;
      } else {
         String localFolderForUnix = devConfigApi.getValueByKey("RUNXIN_FILE_LOCAL_FOLDER_FOR_UNIX");
         if (ObjectUtil.isEmpty(localFolderForUnix)) {
            throw new CommonException("本地文件操作客户端未正确配置：RUNXIN_FILE_LOCAL_FOLDER_FOR_UNIX为空", new Object[0]);
         }

         uploadFileFolder = localFolderForUnix;
      }

      if (!FileUtil.exist(uploadFileFolder)) {
         FileUtil.mkdir(uploadFileFolder);
      }

      client = JSONUtil.createObj();
      client.set("localFileUploadFolder", uploadFileFolder);
   }

   public static void destroyClient() {
      client.clear();
   }

   public static JSONObject getClient() {
      return client;
   }

   public static String getUploadFileFolder() {
      return client.getStr("localFileUploadFolder");
   }

   public static boolean doesBucketExist(String bucketName) {
      initClient();
      return FileUtil.exist(getUploadFileFolder() + FileUtil.FILE_SEPARATOR + bucketName);
   }

   public static void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
   }

   public static boolean isExistingFile(String bucketName, String key) {
      initClient();
      return FileUtil.exist(getUploadFileFolder() + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key);
   }

   public static void storageFile(String bucketName, String key, File file) {
      BufferedInputStream inputStream;
      try {
         inputStream = FileUtil.getInputStream(file);
      } catch (IORuntimeException var5) {
         throw new CommonException("获取文件流异常，名称是：{}", new Object[]{file.getName()});
      }

      storageFile(bucketName, key, inputStream);
   }

   public static void storageFile(String bucketName, String key, MultipartFile multipartFile) {
      InputStream inputStream;
      try {
         inputStream = multipartFile.getInputStream();
      } catch (IOException var5) {
         throw new CommonException("获取文件流异常，名称是：{}", new Object[]{multipartFile.getName()});
      }

      storageFile(bucketName, key, inputStream);
   }

   public static void storageFile(String bucketName, String key, byte[] bytes) {
      initClient();
      FileUtil.writeBytes(bytes, getUploadFileFolder() + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key);
   }

   public static void storageFile(String bucketName, String key, InputStream inputStream) {
      initClient();
      FileUtil.writeFromStream(inputStream, getUploadFileFolder() + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, File file) {
      storageFile(bucketName, key, file);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, MultipartFile multipartFile) {
      storageFile(bucketName, key, multipartFile);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, byte[] bytes) {
      storageFile(bucketName, key, bytes);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, InputStream inputStream) {
      storageFile(bucketName, key, inputStream);
      return getFileAuthUrl(bucketName, key);
   }

   public static byte[] getFileBytes(String bucketName, String key) {
      File file = getFileByBucketNameAndKey(bucketName, key);
      return FileUtil.readBytes(file);
   }

   public static void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
   }

   public static void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
      initClient();
      File file = getFileByBucketNameAndKey(originBucketName, originFileKey);
      File newFile = FileUtil.file(getUploadFileFolder() + FileUtil.FILE_SEPARATOR + newBucketName + FileUtil.FILE_SEPARATOR + newFileKey);
      FileUtil.copy(file, newFile, true);
   }

   public static String getFileAuthUrl(String bucketName, String key) {
      initClient();
      File file = getFileByBucketNameAndKey(bucketName, key);
      return file.getAbsolutePath();
   }

   public static void deleteFile(String bucketName, String key) {
      File file = getFileByBucketNameAndKey(bucketName, key);
      FileUtil.del(file);
   }

   public static File getFileByBucketNameAndKey(String bucketName, String key) {
      initClient();
      String path = getUploadFileFolder() + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key;
      File file = FileUtil.file(path);
      if (!FileUtil.exist(file)) {
         throw new CommonException("文件{}不存在", new Object[]{path});
      } else {
         return file;
      }
   }
}

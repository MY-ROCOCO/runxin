package com.rxinyun.member.tools.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.tools.modular.file.enums.MemberFileBucketAuthEnum;
import jakarta.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class MemberFileAliyunUtil {
   private static final Logger log = LoggerFactory.getLogger(MemberFileAliyunUtil.class);
   private static OSS client;
   private static String defaultBucketName;
   private static final String RUNXIN_FILE_ALIYUN_ACCESS_KEY_ID_KEY = "RUNXIN_FILE_ALIYUN_ACCESS_KEY_ID";
   private static final String RUNXIN_FILE_ALIYUN_ACCESS_KEY_SECRET_KEY = "RUNXIN_FILE_ALIYUN_ACCESS_KEY_SECRET";
   private static final String RUNXIN_FILE_ALIYUN_END_POINT_KEY = "RUNXIN_FILE_ALIYUN_END_POINT";
   private static final String RUNXIN_FILE_ALIYUN_DEFAULT_BUCKET_NAME = "RUNXIN_FILE_ALIYUN_DEFAULT_BUCKET_NAME";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String accessKeyId = devConfigApi.getValueByKey("RUNXIN_FILE_ALIYUN_ACCESS_KEY_ID");
      if (ObjectUtil.isEmpty(accessKeyId)) {
         throw new CommonException("阿里云文件操作客户端未正确配置：accessKeyId为空", new Object[0]);
      } else {
         String accessKeySecret = devConfigApi.getValueByKey("RUNXIN_FILE_ALIYUN_ACCESS_KEY_SECRET");
         if (ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("阿里云文件操作客户端未正确配置：accessKeySecret为空", new Object[0]);
         } else {
            String endpoint = devConfigApi.getValueByKey("RUNXIN_FILE_ALIYUN_END_POINT");
            if (ObjectUtil.isEmpty(endpoint)) {
               throw new CommonException("阿里云文件操作客户端未正确配置：endpoint为空", new Object[0]);
            } else {
               defaultBucketName = devConfigApi.getValueByKey("RUNXIN_FILE_ALIYUN_DEFAULT_BUCKET_NAME");
               if (ObjectUtil.isEmpty(defaultBucketName)) {
                  throw new CommonException("阿里云文件操作客户端未正确配置：defaultBucketName为空", new Object[0]);
               } else {
                  client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
               }
            }
         }
      }
   }

   public static String getDefaultBucketName() {
      initClient();
      return defaultBucketName;
   }

   public static void destroyClient() {
      initClient();
      client.shutdown();
   }

   public OSS getClient() {
      initClient();
      return client;
   }

   public boolean doesBucketExist(String bucketName) {
      try {
         initClient();
         return client.doesBucketExist(bucketName);
      } catch (ClientException | OSSException var3) {
         throw new CommonException(var3.getMessage(), new Object[0]);
      }
   }

   public static void setBucketAcl(String bucketName, MemberFileBucketAuthEnum devFileBucketAuthEnum) {
      try {
         initClient();
         if (devFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PRIVATE)) {
            client.setBucketAcl(bucketName, CannedAccessControlList.Private);
         } else if (devFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ)) {
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
         } else if (devFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
         }
      } catch (ClientException | OSSException var3) {
         throw new CommonException(var3.getMessage(), new Object[0]);
      }
   }

   public static boolean isExistingFile(String bucketName, String key) {
      try {
         initClient();
         return client.doesObjectExist(bucketName, key);
      } catch (ClientException | OSSException var3) {
         throw new CommonException(var3.getMessage(), new Object[0]);
      }
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
      ByteArrayInputStream byteArrayInputStream = null;

      try {
         initClient();
         byteArrayInputStream = new ByteArrayInputStream(bytes);
         ObjectMetadata objectMetadata = new ObjectMetadata();
         objectMetadata.setContentType(getFileContentType(key));
         client.putObject(bucketName, key, byteArrayInputStream, objectMetadata);
      } catch (ClientException | OSSException var8) {
         throw new CommonException(var8.getMessage(), new Object[0]);
      } finally {
         IoUtil.close(byteArrayInputStream);
      }
   }

   public static void storageFile(String bucketName, String key, InputStream inputStream) {
      try {
         initClient();
         ObjectMetadata objectMetadata = new ObjectMetadata();
         objectMetadata.setContentType(getFileContentType(key));
         client.putObject(bucketName, key, inputStream, objectMetadata);
      } catch (ClientException | OSSException var7) {
         throw new CommonException(var7.getMessage(), new Object[0]);
      } finally {
         IoUtil.close(inputStream);
      }
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, File file) {
      storageFile(bucketName, key, file);
      setFileAcl(bucketName, key, MemberFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, MultipartFile multipartFile) {
      storageFile(bucketName, key, multipartFile);
      setFileAcl(bucketName, key, MemberFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, byte[] bytes) {
      storageFile(bucketName, key, bytes);
      setFileAcl(bucketName, key, MemberFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, InputStream inputStream) {
      storageFile(bucketName, key, inputStream);
      setFileAcl(bucketName, key, MemberFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static byte[] getFileBytes(String bucketName, String key) {
      InputStream objectContent = null;

      byte[] var4;
      try {
         initClient();
         OSSObject ossObject = client.getObject(bucketName, key);
         objectContent = ossObject.getObjectContent();
         var4 = IoUtil.readBytes(objectContent);
      } catch (ClientException | OSSException var8) {
         throw new CommonException(var8.getMessage(), new Object[0]);
      } finally {
         IoUtil.close(objectContent);
      }

      return var4;
   }

   public static void setFileAcl(String bucketName, String key, MemberFileBucketAuthEnum devFileBucketAuthEnum) {
      try {
         initClient();
         if (devFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PRIVATE)) {
            client.setObjectAcl(bucketName, key, CannedAccessControlList.Private);
         } else if (devFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ)) {
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
         } else if (devFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicReadWrite);
         }
      } catch (ClientException | OSSException var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }
   }

   public static void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
      try {
         initClient();
         client.copyObject(originBucketName, originFileKey, newBucketName, newFileKey);
      } catch (ClientException | OSSException var5) {
         throw new CommonException(var5.getMessage(), new Object[0]);
      }
   }

   public static String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
      initClient();
      GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
      Date expirationDate = new Date(System.currentTimeMillis() + timeoutMillis);
      request.setExpiration(expirationDate);

      URL url;
      try {
         url = client.generatePresignedUrl(request);
      } catch (ClientException | OSSException var7) {
         throw new CommonException(var7.getMessage(), new Object[0]);
      }

      return url.toString();
   }

   public static String getFileAuthUrl(String bucketName, String key) {
      try {
         initClient();
         OSSClient ossClient = (OSSClient)client;
         List<String> urlList = StrUtil.split(ossClient.getEndpoint().toString(), "://");
         return urlList.get(0) + "://" + bucketName + "." + urlList.get(1) + "/" + key;
      } catch (ClientException | OSSException var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }
   }

   public static void deleteFile(String bucketName, String key) {
      try {
         initClient();
         client.deleteObject(bucketName, key);
      } catch (ClientException | OSSException var3) {
         throw new CommonException(var3.getMessage(), new Object[0]);
      }
   }

   private static String getFileContentType(String key) {
      String contentType = "application/octet-stream";
      if (key.contains(".")) {
         contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(key);
      }

      return contentType;
   }
}

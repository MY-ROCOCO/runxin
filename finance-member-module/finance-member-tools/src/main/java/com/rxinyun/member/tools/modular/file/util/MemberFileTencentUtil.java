package com.rxinyun.member.tools.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class MemberFileTencentUtil {
   private static final Logger log = LoggerFactory.getLogger(MemberFileTencentUtil.class);
   private static COSClient client;
   private static String defaultBucketName;
   private static TransferManager transferManager;
   private static final String RUNXIN_FILE_TENCENT_SECRET_ID_KEY = "RUNXIN_FILE_TENCENT_SECRET_ID";
   private static final String RUNXIN_FILE_TENCENT_SECRET_KEY_KEY = "RUNXIN_FILE_TENCENT_SECRET_KEY";
   private static final String RUNXIN_FILE_TENCENT_REGION_ID_KEY = "RUNXIN_FILE_TENCENT_REGION_ID";
   private static final String RUNXIN_FILE_TENCENT_DEFAULT_BUCKET_NAME = "RUNXIN_FILE_TENCENT_DEFAULT_BUCKET_NAME";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String secretId = devConfigApi.getValueByKey("RUNXIN_FILE_TENCENT_SECRET_ID");
      if (ObjectUtil.isEmpty(secretId)) {
         throw new CommonException("腾讯云文件操作客户端未正确配置：secretId为空", new Object[0]);
      } else {
         String secretKey = devConfigApi.getValueByKey("RUNXIN_FILE_TENCENT_SECRET_KEY");
         if (ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("腾讯云文件操作客户端未正确配置：secretKey为空", new Object[0]);
         } else {
            String regionId = devConfigApi.getValueByKey("RUNXIN_FILE_TENCENT_REGION_ID");
            if (ObjectUtil.isEmpty(regionId)) {
               throw new CommonException("腾讯云文件操作客户端未正确配置：regionId为空", new Object[0]);
            } else {
               defaultBucketName = devConfigApi.getValueByKey("RUNXIN_FILE_TENCENT_DEFAULT_BUCKET_NAME");
               if (ObjectUtil.isEmpty(defaultBucketName)) {
                  throw new CommonException("腾讯云文件操作客户端未正确配置：defaultBucketName为空", new Object[0]);
               } else {
                  Region region = new Region(regionId);
                  COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
                  ClientConfig clientConfig = new ClientConfig(region);
                  client = new COSClient(cred, clientConfig);
                  ExecutorService threadPool = Executors.newFixedThreadPool(32);
                  transferManager = new TransferManager(client, threadPool);
                  TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
                  transferManagerConfiguration.setMultipartUploadThreshold(10485760L);
                  transferManagerConfiguration.setMinimumUploadPartSize(10485760L);
                  transferManager.setConfiguration(transferManagerConfiguration);
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

   public static COSClient getClient() {
      initClient();
      return client;
   }

   public static boolean doesBucketExist(String bucketName) {
      try {
         initClient();
         return client.doesBucketExist(bucketName);
      } catch (CosClientException var2) {
         throw new CommonException(var2.getMessage(), new Object[0]);
      }
   }

   public static void setBucketAcl(String bucketName, MemberFileBucketAuthEnum MemberFileBucketAuthEnum) {
      try {
         initClient();
         if (MemberFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PRIVATE)) {
            client.setBucketAcl(bucketName, CannedAccessControlList.Private);
         } else if (MemberFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ)) {
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
         } else if (MemberFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
         }
      } catch (CosClientException var3) {
         throw new CommonException(var3.getMessage(), new Object[0]);
      }
   }

   public static boolean isExistingFile(String bucketName, String key) {
      try {
         initClient();
         client.getObjectMetadata(bucketName, key);
         return true;
      } catch (CosClientException var3) {
         return false;
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
      } catch (CosClientException var8) {
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
      } catch (CosClientException var7) {
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
      COSObjectInputStream cosObjectInput = null;

      byte[] var5;
      try {
         initClient();
         GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
         COSObject cosObject = client.getObject(getObjectRequest);
         cosObjectInput = cosObject.getObjectContent();
         var5 = IoUtil.readBytes(cosObjectInput);
      } catch (CosClientException var9) {
         throw new CommonException(var9.getMessage(), new Object[0]);
      } finally {
         IoUtil.close(cosObjectInput);
      }

      return var5;
   }

   public static void setFileAcl(String bucketName, String key, MemberFileBucketAuthEnum MemberFileBucketAuthEnum) {
      try {
         initClient();
         if (MemberFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PRIVATE)) {
            client.setObjectAcl(bucketName, key, CannedAccessControlList.Private);
         } else if (MemberFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ)) {
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
         } else if (MemberFileBucketAuthEnum.equals(MemberFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicReadWrite);
         }
      } catch (CosClientException var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }
   }

   public static void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
      try {
         initClient();
         transferManager.copy(originBucketName, originFileKey, newBucketName, newFileKey);
      } catch (CosClientException var5) {
         throw new CommonException(var5.getMessage(), new Object[0]);
      }
   }

   public static String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
      GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
      Date expirationDate = new Date(System.currentTimeMillis() + timeoutMillis);
      request.setExpiration(expirationDate);

      URL url;
      try {
         initClient();
         url = client.generatePresignedUrl(request);
      } catch (CosClientException var7) {
         throw new CommonException(var7.getMessage(), new Object[0]);
      }

      return url.toString();
   }

   public static String getFileAuthUrl(String bucketName, String key) {
      URL url;
      try {
         initClient();
         url = client.getObjectUrl(bucketName, key);
      } catch (CosClientException var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }

      return url.toString();
   }

   public static void deleteFile(String bucketName, String key) {
      try {
         initClient();
         client.deleteObject(bucketName, key);
      } catch (CosClientException var3) {
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

package com.rxinyun.admin.dev.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.dev.modular.file.enums.DevFileBucketAuthEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.BucketExistsArgs.Builder;
import io.minio.http.Method;
import jakarta.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class DevFileMinIoUtil {
   private static final Logger log = LoggerFactory.getLogger(DevFileMinIoUtil.class);
   private static MinioClient client;
   private static String defaultBucketName;
   private static final String RUNXIN_FILE_MINIO_ACCESS_KEY_KEY = "RUNXIN_FILE_MINIO_ACCESS_KEY";
   private static final String RUNXIN_FILE_MINIO_SECRET_KEY_KEY = "RUNXIN_FILE_MINIO_SECRET_KEY";
   private static final String RUNXIN_FILE_MINIO_END_POINT_KEY = "RUNXIN_FILE_MINIO_END_POINT";
   private static final String RUNXIN_FILE_MINIO_DEFAULT_BUCKET_NAME = "RUNXIN_FILE_MINIO_DEFAULT_BUCKET_NAME";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String accessKey = devConfigApi.getValueByKey("RUNXIN_FILE_MINIO_ACCESS_KEY");
      if (ObjectUtil.isEmpty(accessKey)) {
         throw new CommonException("MINIO文件操作客户端未正确配置：accessKey为空", new Object[0]);
      } else {
         String secretKey = devConfigApi.getValueByKey("RUNXIN_FILE_MINIO_SECRET_KEY");
         if (ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("MINIO文件操作客户端未正确配置：secretKey为空", new Object[0]);
         } else {
            String endpoint = devConfigApi.getValueByKey("RUNXIN_FILE_MINIO_END_POINT");
            if (ObjectUtil.isEmpty(endpoint)) {
               throw new CommonException("MINIO文件操作客户端未正确配置：endpoint为空", new Object[0]);
            } else {
               defaultBucketName = devConfigApi.getValueByKey("RUNXIN_FILE_MINIO_DEFAULT_BUCKET_NAME");
               if (ObjectUtil.isEmpty(defaultBucketName)) {
                  throw new CommonException("MINIO文件操作客户端未正确配置：defaultBucketName为空", new Object[0]);
               } else {
                  client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
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
   }

   public static MinioClient getClient() {
      return client;
   }

   public static boolean doesBucketExist(String bucketName) {
      try {
         initClient();
         BucketExistsArgs bucketExistsArgs = (BucketExistsArgs)((Builder)BucketExistsArgs.builder().bucket(bucketName)).build();
         client.bucketExists(bucketExistsArgs);
         return false;
      } catch (Exception var2) {
         throw new CommonException(var2.getMessage(), new Object[0]);
      }
   }

   public static void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
      setFileAcl(bucketName, "*", devFileBucketAuthEnum);
   }

   public static boolean isExistingFile(String bucketName, String key) {
      try {
         initClient();
         GetObjectArgs getObjectArgs = (GetObjectArgs)((io.minio.GetObjectArgs.Builder)((io.minio.GetObjectArgs.Builder)GetObjectArgs.builder()
                  .bucket(bucketName))
               .object(key))
            .build();
         InputStream object = client.getObject(getObjectArgs);
         return !ObjectUtil.isEmpty(object);
      } catch (Exception var4) {
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
         PutObjectArgs putObjectArgs = (PutObjectArgs)((io.minio.PutObjectArgs.Builder)((io.minio.PutObjectArgs.Builder)PutObjectArgs.builder()
                  .bucket(bucketName))
               .object(key))
            .contentType(getFileContentType(key))
            .stream(byteArrayInputStream, bytes.length, -1L)
            .build();
         client.putObject(putObjectArgs);
      } catch (Exception var8) {
         throw new CommonException(var8.getMessage(), new Object[0]);
      } finally {
         IoUtil.close(byteArrayInputStream);
      }
   }

   public static void storageFile(String bucketName, String key, InputStream inputStream) {
      try {
         initClient();
         PutObjectArgs putObjectArgs = (PutObjectArgs)((io.minio.PutObjectArgs.Builder)((io.minio.PutObjectArgs.Builder)PutObjectArgs.builder()
                  .bucket(bucketName))
               .object(key))
            .contentType(getFileContentType(key))
            .stream(inputStream, inputStream.available(), -1L)
            .build();
         client.putObject(putObjectArgs);
      } catch (Exception var7) {
         throw new CommonException(var7.getMessage(), new Object[0]);
      } finally {
         IoUtil.close(inputStream);
      }
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, File file) {
      storageFile(bucketName, key, file);
      setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, MultipartFile multipartFile) {
      storageFile(bucketName, key, multipartFile);
      setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, byte[] bytes) {
      storageFile(bucketName, key, bytes);
      setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static String storageFileWithReturnUrl(String bucketName, String key, InputStream inputStream) {
      storageFile(bucketName, key, inputStream);
      setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
      return getFileAuthUrl(bucketName, key);
   }

   public static byte[] getFileBytes(String bucketName, String key) {
      try {
         initClient();
         GetObjectArgs getObjectArgs = (GetObjectArgs)((io.minio.GetObjectArgs.Builder)((io.minio.GetObjectArgs.Builder)GetObjectArgs.builder()
                  .bucket(bucketName))
               .object(key))
            .build();
         InputStream inputStream = client.getObject(getObjectArgs);
         return IoUtil.readBytes(inputStream);
      } catch (Exception var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }
   }

   public static void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
      try {
         JSONObject configObject = JSONUtil.createObj().set("Version", "2012-10-17");
         JSONArray statementArray = JSONUtil.createArray();
         JSONArray actionArray = JSONUtil.createArray();
         if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
            actionArray.put("s3:GetObject");
         }

         if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
            actionArray.put("s3:GetObject");
            actionArray.put("s3:PutObject");
         }

         JSONObject statementObject = JSONUtil.createObj();
         statementObject.set("Effect", "Allow")
            .set("Principal", JSONUtil.createObj().set("AWS", JSONUtil.createArray().put("*")))
            .set("Action", actionArray)
            .set("Resource", JSONUtil.createArray().put("arn:aws:s3:::" + bucketName + "/*"));
         statementArray.put(statementObject);
         configObject.set("Statement", statementArray);
         String config = JSONUtil.toJsonStr(configObject);
         SetBucketPolicyArgs setBucketPolicyArgs = (SetBucketPolicyArgs)((io.minio.SetBucketPolicyArgs.Builder)SetBucketPolicyArgs.builder().bucket(bucketName))
            .config(config)
            .build();
         client.setBucketPolicy(setBucketPolicyArgs);
      } catch (Exception var9) {
         throw new CommonException(var9.getMessage(), new Object[0]);
      }
   }

   public static void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
      try {
         initClient();
         CopySource copySource = (CopySource)((io.minio.CopySource.Builder)((io.minio.CopySource.Builder)CopySource.builder().bucket(originBucketName))
               .object(originFileKey))
            .build();
         CopyObjectArgs copyObjectArgs = (CopyObjectArgs)((io.minio.CopyObjectArgs.Builder)((io.minio.CopyObjectArgs.Builder)CopyObjectArgs.builder()
                  .source(copySource)
                  .bucket(newBucketName))
               .object(newFileKey))
            .build();
         client.copyObject(copyObjectArgs);
      } catch (Exception var6) {
         throw new CommonException(var6.getMessage(), new Object[0]);
      }
   }

   public static String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
      try {
         initClient();
         GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = (GetPresignedObjectUrlArgs)((io.minio.GetPresignedObjectUrlArgs.Builder)((io.minio.GetPresignedObjectUrlArgs.Builder)GetPresignedObjectUrlArgs.builder()
                  .bucket(bucketName))
               .object(key))
            .method(Method.GET)
            .expiry(timeoutMillis.intValue())
            .build();
         return client.getPresignedObjectUrl(getPresignedObjectUrlArgs);
      } catch (Exception var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }
   }

   public static String getFileAuthUrl(String bucketName, String key) {
      try {
         initClient();
         DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
         return devConfigApi.getValueByKey("RUNXIN_FILE_MINIO_END_POINT") + "/" + bucketName + "/" + key;
      } catch (Exception var3) {
         throw new CommonException(var3.getMessage(), new Object[0]);
      }
   }

   public static void deleteFile(String bucketName, String key) {
      try {
         RemoveObjectArgs removeObjectArgs = (RemoveObjectArgs)((io.minio.RemoveObjectArgs.Builder)((io.minio.RemoveObjectArgs.Builder)RemoveObjectArgs.builder()
                  .bucket(bucketName))
               .object(key))
            .build();
         client.removeObject(removeObjectArgs);
      } catch (Exception var3) {
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

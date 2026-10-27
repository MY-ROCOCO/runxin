package com.rxinyun.admin.dev.modular.update.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Sets;
import com.rxinyun.admin.dev.modular.config.param.DevConfigKeyEditParam;
import com.rxinyun.admin.dev.modular.config.service.DevConfigService;
import com.rxinyun.admin.dev.modular.update.config.UpdateConfig;
import com.rxinyun.admin.dev.modular.update.enums.FileTypeEnum;
import com.rxinyun.admin.dev.modular.update.param.CheckUpdateParam;
import com.rxinyun.admin.dev.modular.update.param.DownloadParam;
import com.rxinyun.admin.dev.modular.update.param.InstallParam;
import com.rxinyun.admin.dev.modular.update.result.DownloadResult;
import com.rxinyun.admin.dev.modular.update.result.InstallResult;
import com.rxinyun.admin.dev.modular.update.result.UpdateCheckResult;
import com.rxinyun.admin.dev.modular.update.result.UpdateProgressResult;
import com.rxinyun.admin.dev.modular.update.service.UpdateService;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.exception.CommonException;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateServiceImpl implements UpdateService {
   private static final Logger log = LoggerFactory.getLogger(UpdateServiceImpl.class);
   private static final String UPDATE_SERVER_URL = "https://api.reckonkvm.com";
   private static final Integer PRODUCT_ID = 3;
   private static final String UPDATE_INSTALL_FRONT_ENABLED_KEY = "UPDATE_INSTALL_FRONT_ENABLED";
   private final UpdateConfig updateConfig;
   private UpdateProgressResult updateProgress;
   private final String PROGRESS_REDIS_KEY = "update:progress";
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private DevConfigService devConfigService;
   private static final ExecutorService downloadExecutor = Executors.newFixedThreadPool(3);

   public UpdateServiceImpl(UpdateConfig updateConfig) {
      this.updateConfig = updateConfig;
   }

   private void saveProgressToRedis() {
      try {
         this.commonCacheOperator.put("update:progress", this.updateProgress);
      } catch (Exception var2) {
         log.error("保存更新进度信息到Redis失败", var2);
      }
   }

   private void initProgress(int type) {
      this.updateProgress = UpdateProgressResult.builder().progress(0).type(type).currentDownloading(null).status("IDLE").errorMessage(null).build();
      this.saveProgressToRedis();
   }

   @Override
   public UpdateProgressResult getUpdateProgress() {
      return this.updateProgress;
   }

   @Override
   public UpdateCheckResult checkUpdate(CheckUpdateParam param) {
      try {
         String url = "https://api.reckonkvm.com/member/product/releases/check-update";
         String systemVersion = this.devConfigService.getValueDetailByKey("RUNXIN_SYS_VERSION");
         log.info("默认版本号：{}", systemVersion);
         if (ObjectUtil.isEmpty(systemVersion)) {
            throw new CommonException("未配置版本号", new Object[0]);
         } else {
            url = url + "?currentVersion=" + systemVersion + "&productId=" + PRODUCT_ID;
            String response = HttpUtil.get(url);
            log.info("原始响应内容: {}", response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject data = jsonResponse.getJSONObject("data");
            if (data == null) {
               return UpdateCheckResult.builder().hasUpdate(false).currentVersion(systemVersion).build();
            } else {
               boolean hasUpdate = data.getBool("hasUpdate", false);
               String currentDescription = data.getStr("currentDescription");
               String nextVersion = data.getStr("nextVersion");
               String nextDescription = data.getStr("nextDescription");
               String latestVersion = data.getStr("latestVersion");
               String latestDescription = data.getStr("latestDescription");
               return !hasUpdate
                  ? UpdateCheckResult.builder()
                     .hasUpdate(false)
                     .currentVersion(systemVersion)
                     .currentDescription(currentDescription)
                     .nextVersion(nextVersion)
                     .nextDescription(nextDescription)
                     .latestVersion(latestVersion)
                     .latestDescription(latestDescription)
                     .description("您已是最新版本！")
                     .build()
                  : UpdateCheckResult.builder()
                     .hasUpdate(hasUpdate)
                     .currentVersion(systemVersion)
                     .currentDescription(currentDescription)
                     .nextVersion(nextVersion)
                     .nextDescription(nextDescription)
                     .latestVersion(latestVersion)
                     .latestDescription(latestDescription)
                     .build();
            }
         }
      } catch (Exception var13) {
         log.error("检查更新失败", var13);
         return UpdateCheckResult.builder().hasUpdate(false).currentVersion(param.getCurrentVersion()).build();
      }
   }

   @Override
   public DownloadResult download(DownloadParam param) {
      if (ObjectUtil.isEmpty(param.getVersion())) {
         return DownloadResult.builder().success(false).errorMessage("未配置版本号").build();
      } else {
         downloadExecutor.submit(() -> this.executeDownload(param));
         return DownloadResult.builder().success(true).errorMessage("下载已启动，请通过进度接口查询状态").build();
      }
   }

   private void executeDownload(DownloadParam param) {
      try {
         this.initProgress(1);
         String url = "https://api.reckonkvm.com/member/product/releases/by-version";
         url = url + "?currentVersion=" + param.getVersion() + "&productId=" + PRODUCT_ID;
         String response = HttpUtil.get(url);
         log.info("原始响应内容: {}", response);
         JSONObject jsonResponse = new JSONObject(response);
         JSONObject data = jsonResponse.getJSONObject("data");
         if (data == null) {
            synchronized (this) {
               this.updateProgress.setStatus("FAILED");
               this.updateProgress.setErrorMessage("更新失败");
               this.saveProgressToRedis();
               return;
            }
         }

         String version = data.getStr("version");
         String frontDownUrl = data.getStr("frontDownUrl");
         String adminFrontDownUrl = data.getStr("adminFrontDownUrl");
         String backDownUrl = data.getStr("backDownUrl");
         String downloadDir = this.devConfigService.getValueByKey("DOWNLOAD_DIR");
         if (StrUtil.isBlank(downloadDir)) {
            throw new Exception("下载目录未配置");
         }

         File dir = new File(downloadDir + "/" + version);
         if (!dir.exists()) {
            dir.mkdirs();
         } else {
            log.info("清空目录: {}", dir.getAbsolutePath());
            FileUtil.clean(dir);
         }

         boolean allSuccess = true;
         if (null != frontDownUrl) {
            try {
               this.downloadFrontPackage(dir, version, frontDownUrl);
            } catch (Exception var28) {
               Exception e = var28;
               allSuccess = false;
               log.error("前端包下载失败", var28);
               synchronized (this) {
                  this.updateProgress.setStatus("error");
                  this.updateProgress.setErrorMessage("前端包下载失败: " + e.getMessage());
                  this.saveProgressToRedis();
               }
            }
         }

         if (null != adminFrontDownUrl) {
            try {
               this.downloadAdminFrontPackage(dir, version, adminFrontDownUrl);
            } catch (Exception var26) {
               Exception e = var26;
               allSuccess = false;
               log.error("管理后台前端包下载失败", var26);
               synchronized (this) {
                  this.updateProgress.setStatus("error");
                  this.updateProgress.setErrorMessage("管理后台前端包下载失败: " + e.getMessage());
                  this.saveProgressToRedis();
               }
            }
         }

         if (null != backDownUrl) {
            try {
               this.downloadBackPackage(dir, version, backDownUrl);
            } catch (Exception var24) {
               Exception e = var24;
               allSuccess = false;
               log.error("后端包下载失败", var24);
               synchronized (this) {
                  this.updateProgress.setStatus("error");
                  this.updateProgress.setErrorMessage("后端包下载失败: " + e.getMessage());
                  this.saveProgressToRedis();
               }
            }
         }

         if (allSuccess) {
            synchronized (this) {
               this.updateProgress.setStatus("COMPLETED");
               this.saveProgressToRedis();
            }
         }
      } catch (Exception var29) {
         Exception e = var29;
         log.error("异步下载更新包失败", var29);
         synchronized (this) {
            this.updateProgress.setStatus("error");
            this.updateProgress.setErrorMessage("下载失败: " + e.getMessage());
            this.saveProgressToRedis();
         }
      }
   }

   private DownloadResult downloadFrontPackage(File dir, String version, String param) {
      DownloadResult var9;
      try {
         this.updateProgress.setCurrentDownloading("FRONTEND");
         this.updateProgress.setStatus("DOWNLOADING");
         this.updateProgress.setProgress(0);
         this.updateProgress.setErrorMessage(null);
         this.saveProgressToRedis();
         String path = new URL(param).getPath();
         String originalFileName = path.substring(path.lastIndexOf(47) + 1);
         String fileName = "front-" + version + "-" + originalFileName;
         String filePath = dir.getAbsolutePath() + "/" + fileName;
         boolean success = this.downloadFileWithProgress(param, filePath, progress -> {
            this.updateProgress.setProgress(progress);
            this.saveProgressToRedis();
         });
         if (!success) {
            this.updateProgress.setStatus("FAILED");
            this.updateProgress.setErrorMessage("前端包下载文件失败");
            this.saveProgressToRedis();
            return DownloadResult.builder().success(false).errorMessage("前端包下载文件失败").build();
         }

         this.updateProgress.setStatus("COMPLETED");
         this.updateProgress.setProgress(100);
         this.saveProgressToRedis();
         var9 = DownloadResult.builder().success(true).filePath(filePath).fileType(FileTypeEnum.FRONT.getCode()).build();
      } catch (Exception var13) {
         log.error("下载前端包失败", var13);
         this.updateProgress.setStatus("error");
         this.updateProgress.setErrorMessage("前端包下载失败: " + var13.getMessage());
         this.saveProgressToRedis();
         return DownloadResult.builder().success(false).errorMessage("前端包下载失败: " + var13.getMessage()).build();
      } finally {
         if (!"completed".equals(this.updateProgress.getStatus())) {
            this.updateProgress.setCurrentDownloading(null);
            this.saveProgressToRedis();
         }
      }

      return var9;
   }

   private DownloadResult downloadAdminFrontPackage(File dir, String version, String param) {
      DownloadResult var9;
      try {
         this.updateProgress.setCurrentDownloading("ADMIN_FRONTEND");
         this.updateProgress.setStatus("DOWNLOADING");
         this.updateProgress.setProgress(0);
         this.updateProgress.setErrorMessage(null);
         this.saveProgressToRedis();
         String path = new URL(param).getPath();
         String originalFileName = path.substring(path.lastIndexOf(47) + 1);
         String fileName = "admin-front-" + version + "-" + originalFileName;
         String filePath = dir.getAbsolutePath() + "/" + fileName;
         boolean success = this.downloadFileWithProgress(param, filePath, progress -> {
            this.updateProgress.setProgress(progress);
            this.saveProgressToRedis();
         });
         if (!success) {
            this.updateProgress.setStatus("FAILED");
            this.updateProgress.setErrorMessage("管理后台前端包下载文件失败");
            this.saveProgressToRedis();
            return DownloadResult.builder().success(false).errorMessage("管理后台前端包下载文件失败").build();
         }

         this.updateProgress.setStatus("COMPLETED");
         this.updateProgress.setProgress(100);
         this.saveProgressToRedis();
         var9 = DownloadResult.builder().success(true).filePath(filePath).fileType(FileTypeEnum.ADMIN_FRONT.getCode()).build();
      } catch (Exception var13) {
         log.error("下载管理后台前端包失败", var13);
         this.updateProgress.setStatus("error");
         this.updateProgress.setErrorMessage("管理后台前端包下载失败: " + var13.getMessage());
         this.saveProgressToRedis();
         return DownloadResult.builder().success(false).errorMessage("管理后台前端包下载失败: " + var13.getMessage()).build();
      } finally {
         if (!"completed".equals(this.updateProgress.getStatus())) {
            this.updateProgress.setCurrentDownloading(null);
            this.saveProgressToRedis();
         }
      }

      return var9;
   }

   private DownloadResult downloadBackPackage(File dir, String version, String param) {
      DownloadResult var9;
      try {
         this.updateProgress.setCurrentDownloading("BACKEND");
         this.updateProgress.setStatus("DOWNLOADING");
         this.updateProgress.setProgress(0);
         this.updateProgress.setErrorMessage(null);
         this.saveProgressToRedis();
         String path = new URL(param).getPath();
         String originalFileName = path.substring(path.lastIndexOf(47) + 1);
         String fileName = "back-" + version + "-" + originalFileName;
         String filePath = dir.getAbsolutePath() + "/" + fileName;
         boolean success = this.downloadFileWithProgress(param, filePath, progress -> {
            this.updateProgress.setProgress(progress);
            this.saveProgressToRedis();
         });
         if (!success) {
            this.updateProgress.setStatus("FAILED");
            this.updateProgress.setErrorMessage("后端包下载文件失败");
            this.saveProgressToRedis();
            return DownloadResult.builder().success(false).errorMessage("后端包下载文件失败").build();
         }

         this.updateProgress.setStatus("COMPLETED");
         this.updateProgress.setProgress(100);
         this.saveProgressToRedis();
         var9 = DownloadResult.builder().success(true).filePath(filePath).fileType(FileTypeEnum.BACK.getCode()).build();
      } catch (Exception var13) {
         log.error("下载后端包失败", var13);
         this.updateProgress.setStatus("error");
         this.updateProgress.setErrorMessage("后端包下载失败: " + var13.getMessage());
         this.saveProgressToRedis();
         return DownloadResult.builder().success(false).errorMessage("后端包下载失败: " + var13.getMessage()).build();
      } finally {
         if (!"completed".equals(this.updateProgress.getStatus())) {
            this.updateProgress.setCurrentDownloading(null);
            this.saveProgressToRedis();
         }
      }

      return var9;
   }

   private boolean downloadFileWithProgress(String downloadUrl, String filePath, UpdateServiceImpl.ProgressCallback progressCallback) {
      int maxRetries = 3;
      int retryDelayMs = 2000;
      int retryCount = 0;
      int lastReportedProgress = 0;

      while (retryCount < maxRetries) {
         try {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.connect();
            int fileLength = connection.getContentLength();

            int currentProgress;
            try (
               InputStream input = connection.getInputStream();
               FileOutputStream output = new FileOutputStream(filePath);
            ) {
               byte[] data = new byte[8192];
               long total = 0L;

               int count;
               while ((count = input.read(data)) != -1) {
                  total += count;
                  output.write(data, 0, count);
                  if (fileLength > 0) {
                     currentProgress = (int)(total * 100L / fileLength);
                     if (currentProgress >= 100 || currentProgress - lastReportedProgress >= 1) {
                        progressCallback.onProgress(currentProgress);
                        lastReportedProgress = currentProgress;
                     }
                  }
               }

               if (lastReportedProgress < 100) {
                  progressCallback.onProgress(100);
               }

            }

            return true;
         } catch (Exception var23) {
            log.error("文件下载失败(尝试 {}/{}), 原因: {}", new Object[]{++retryCount, maxRetries, var23.getMessage()});
            if (retryCount >= maxRetries) {
               return false;
            }

            try {
               Thread.sleep(retryDelayMs);
            } catch (InterruptedException var18) {
               Thread.currentThread().interrupt();
               return false;
            }
         }
      }

      return false;
   }

   @Override
   public InstallResult install(InstallParam param) {
      try {
         this.initProgress(2);
         String version = param.getVersion();
         String downloadDir = this.devConfigService.getValueByKey("DOWNLOAD_DIR");
         if (StrUtil.isBlank(downloadDir)) {
            throw new Exception("下载目录未配置");
         } else {
            downloadDir = downloadDir + "/" + version;
            File dir = new File(downloadDir);
            if (!dir.exists()) {
               return InstallResult.builder().success(false).errorMessage("找不到版本 " + version + " 的下载文件").build();
            } else {
               if (this.isInstallFrontEnabled()) {
                  File[] frontFiles = dir.listFiles((d, name) -> name.startsWith("front-"));
                  if (frontFiles != null && frontFiles.length > 0) {
                     this.installFrontPackage(frontFiles[0].getAbsolutePath());
                  }
               } else {
                  log.info("配置 {} 已关闭，本次跳过用户端前端安装", "UPDATE_INSTALL_FRONT_ENABLED");
               }

               File[] adminFrontFiles = dir.listFiles((d, name) -> name.startsWith("admin-front-"));
               if (adminFrontFiles != null && adminFrontFiles.length > 0) {
                  this.installAdminFrontPackage(adminFrontFiles[0].getAbsolutePath());
               }

               File[] backFiles = dir.listFiles((d, name) -> name.startsWith("back-"));
               if (backFiles != null && backFiles.length > 0) {
                  this.installBackPackage(backFiles[0].getAbsolutePath());
               }

               try {
                  this.commonCacheOperator.remove(new String[]{"update:progress"});
                  log.info("安装完成，已删除Redis进度key");
               } catch (Exception var9) {
                  log.error("删除Redis进度key失败", var9);
               }

               try {
                  DevConfigKeyEditParam devConfigKeyEditParam = new DevConfigKeyEditParam();
                  devConfigKeyEditParam.setConfigKey("RUNXIN_SYS_VERSION");
                  devConfigKeyEditParam.setConfigValue(version);
                  this.devConfigService.updateByConfigKey(devConfigKeyEditParam);
               } catch (Exception var8) {
                  log.error("更新系统版本号配置失败", var8);
               }

               return InstallResult.builder().success(true).version(version).build();
            }
         }
      } catch (Exception var10) {
         log.error("安装更新失败", var10);
         return InstallResult.builder().success(false).errorMessage("安装失败: " + var10.getMessage()).build();
      }
   }

   private boolean isInstallFrontEnabled() {
      String configValue = this.devConfigService.getValueByKey("UPDATE_INSTALL_FRONT_ENABLED");
      return StrUtil.isBlank(configValue) || "1".equals(configValue);
   }

   private void installFrontPackage(String filePath) {
      try {
         File zipFile = new File(filePath);
         if (zipFile.exists() && zipFile.length() != 0L) {
            String fileName = zipFile.getName();
            String[] parts = fileName.split("-");
            if (parts.length < 3) {
               throw new Exception("前端包文件名格式不正确，应为: front-{version}-{originalName}");
            } else {
               String version = parts[1];
               String frontInstallDir = this.devConfigService.getValueByKey("FRONT_INSTALL_DIR");
               if (StrUtil.isBlank(frontInstallDir)) {
                  throw new Exception("前端安装目录未配置");
               } else {
                  Path tempDir = Paths.get(frontInstallDir, ".temp-" + version);
                  if (!Files.exists(tempDir)) {
                     Files.createDirectories(tempDir);
                  } else {
                     FileUtil.clean(tempDir.toFile());
                  }

                  log.info("开始解压前端包[版本:{}]: {} 到临时目录: {}", new Object[]{version, filePath, tempDir});
                  this.unzipSkipMapFiles(filePath, tempDir);
                  Path extractedOutput = tempDir.resolve(".output/server/index.mjs");
                  if (!Files.exists(extractedOutput)) {
                     throw new Exception("压缩包中 .output 结构不正确");
                  } else {
                     Path outputDir = Paths.get(frontInstallDir, ".output");
                     if (Files.exists(outputDir)) {
                        log.info("清空原.output目录: {}", outputDir);
                        FileUtil.clean(outputDir.toFile());
                     } else {
                        Files.createDirectories(outputDir);
                     }

                     log.info("开始复制文件从临时目录到.output目录");
                     Path tempOutputDir = tempDir.resolve(".output");
                     Files.walk(tempOutputDir).forEach(source -> {
                        try {
                           Path dest = outputDir.resolve(tempOutputDir.relativize(source));
                           if (Files.isDirectory(source)) {
                              Files.createDirectories(dest);
                           } else {
                              Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                           }
                        } catch (IOException var4x) {
                           throw new RuntimeException("复制文件失败: " + source, var4x);
                        }
                     });
                     synchronized (this) {
                        this.updateProgress.setType(2);
                        this.updateProgress.setStatus("INSTALLING");
                        this.updateProgress.setProgress(100);
                        this.saveProgressToRedis();
                     }

                     log.info("准备重启Nuxt应用");
                     ProcessBuilder pb = new ProcessBuilder("pm2", "restart", "all").directory(new File(frontInstallDir + "/.output/server")).inheritIO();
                     Process process = pb.start();
                     int exitCode = process.waitFor();
                     if (exitCode != 0) {
                        throw new Exception("重启Nuxt应用失败，退出码: " + exitCode);
                     } else {
                        synchronized (this) {
                           this.updateProgress.setStatus("COMPLETED");
                           this.saveProgressToRedis();
                        }

                        log.info("前端包[版本:{}]安装完成", version);
                     }
                  }
               }
            }
         } else {
            throw new Exception("前端包文件不存在或为空");
         }
      } catch (Exception var20) {
         Exception e = var20;
         log.error("安装前端包失败", var20);
         synchronized (this) {
            this.updateProgress.setStatus("error");
            this.updateProgress.setErrorMessage("安装前端包失败: " + e.getMessage());
            this.saveProgressToRedis();
         }

         throw new RuntimeException("安装前端包失败: " + var20.getMessage());
      }
   }

   private void unzipSkipMapFiles(String zipFilePath, Path targetDir) throws IOException {
      ZipEntry entry;
      try (
         InputStream fis = new FileInputStream(zipFilePath);
         ZipInputStream zis = new ZipInputStream(fis);
      ) {
         while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName.endsWith(".map")) {
               zis.closeEntry();
            } else {
               Path newPath = targetDir.resolve(entryName);
               if (entry.isDirectory()) {
                  Files.createDirectories(newPath);
               } else {
                  Path parent = newPath.getParent();
                  if (parent != null && !Files.exists(parent)) {
                     Files.createDirectories(parent);
                  }

                  try (OutputStream os = Files.newOutputStream(
                        newPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
                     )) {
                     byte[] buffer = new byte[8192];

                     int len;
                     while ((len = zis.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                     }
                  }
               }

               zis.closeEntry();
            }
         }
      }
   }

   private void installAdminFrontPackage(String filePath) throws Exception {
      try {
         File zipFile = new File(filePath);
         if (zipFile.exists() && zipFile.length() != 0L) {
            String adminFrontInstallDir = this.devConfigService.getValueByKey("ADMIN_FRONT_INSTALL_DIR");
            if (StrUtil.isBlank(adminFrontInstallDir)) {
               throw new Exception("管理后台前端安装目录未配置");
            } else {
               synchronized (this) {
                  this.updateProgress.setType(2);
                  this.updateProgress.setStatus("INSTALLING");
                  this.updateProgress.setCurrentDownloading("ADMIN_FRONTEND");
                  this.saveProgressToRedis();
               }

               String destZipPath = adminFrontInstallDir + "/" + zipFile.getName();
               Files.copy(zipFile.toPath(), Paths.get(destZipPath), StandardCopyOption.REPLACE_EXISTING);
               int retryCount = 0;

               while (retryCount < 3) {
                  try {
                     this.unzipSkipMapFiles(destZipPath, Paths.get(adminFrontInstallDir));
                     break;
                  } catch (Exception var12) {
                     if (++retryCount >= 3) {
                        throw new Exception("解压管理后台前端包失败: " + var12.getMessage());
                     }

                     try {
                        Thread.sleep(1000L);
                     } catch (InterruptedException var10) {
                        log.warn("解压管理后台前端包等待被中断，继续重试", var10);
                        Thread.currentThread().interrupt();
                        throw new Exception("解压管理后台前端包过程被中断");
                     }
                  }
               }

               FileUtil.del(destZipPath);
               synchronized (this) {
                  this.updateProgress.setProgress(100);
                  this.saveProgressToRedis();
               }
            }
         } else {
            throw new Exception("管理后台前端包文件不存在或为空");
         }
      } catch (Exception var13) {
         log.error("安装管理后台前端包失败", var13);
         throw new Exception("安装管理后台前端包失败: " + var13.getMessage());
      }
   }

   private void installBackPackage(String filePath) throws Exception {
      try {
         log.info("开始安装后端包: {}", filePath);
         File jarFile = new File(filePath);
         if (jarFile.exists() && jarFile.length() != 0L) {
            String backInstallDir = this.devConfigService.getValueByKey("BACK_INSTALL_DIR");
            if (StrUtil.isBlank(backInstallDir)) {
               throw new Exception("后端安装目录未在数据库配置");
            } else {
               Path currentJarPath = Paths.get(backInstallDir, "app.jar");
               if (Files.exists(currentJarPath)) {
                  Path backupDir = Paths.get(backInstallDir, "backup");
                  if (!Files.exists(backupDir)) {
                     Files.createDirectories(backupDir);
                  }

                  Path backupPath = backupDir.resolve("app-" + System.currentTimeMillis() + ".jar");
                  Files.copy(currentJarPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                  log.info("成功备份当前jar包到: {}", backupPath);
                  File[] backups = backupDir.toFile().listFiles((dir, name) -> name.startsWith("app-") && name.endsWith(".jar"));
                  if (backups != null && backups.length > 3) {
                     Arrays.sort(backups, Comparator.comparingLong(File::lastModified));

                     for (int i = 0; i < backups.length - 3; i++) {
                        FileUtil.del(backups[i]);
                     }
                  }
               }

               Files.copy(jarFile.toPath(), currentJarPath, StandardCopyOption.REPLACE_EXISTING);
               log.info("成功更新jar包到: {}", currentJarPath);
               synchronized (this) {
                  this.updateProgress.setType(2);
                  this.updateProgress.setStatus("INSTALLING");
                  this.updateProgress.setCurrentDownloading("BACKEND");
                  this.saveProgressToRedis();
               }

               this.restartServiceAsync(backInstallDir, currentJarPath.toString());
               synchronized (this) {
                  this.updateProgress.setProgress(100);
                  this.saveProgressToRedis();
               }
            }
         } else {
            throw new Exception("后端jar文件不存在或为空");
         }
      } catch (Exception var13) {
         log.error("执行部署脚本失败", var13);
         throw new Exception("服务更新失败: " + var13.getMessage());
      }
   }

   private void restartServiceAsync(String installDir, String jarPath) throws Exception {
      String ymlConfigPath = this.devConfigService.getValueByKey("BACK_YML_CONFIG_PATH");
      if (StrUtil.isBlank(ymlConfigPath)) {
         throw new Exception("yml配置文件路径(BACK_YML_CONFIG_PATH)未配置");
      } else {
         String javaOpts = this.devConfigService.getValueByKey("JAVA_OPTS");
         String javaOut = "app.log";
         String pidFile = "app.pid";
         Path javaOutPath = Paths.get(installDir, javaOut);
         String scriptContent = String.format(
            "#!/bin/bash\ncd \"%s\" || exit 1\necho 'Stopping existing process...'\nif [ -f \"%s\" ]; then\n  kill $(cat \"%s\") 2>/dev/null || true\n  rm -f \"%s\"\nfi\npkill -f '%s' 2>/dev/null || true\nsleep 5\necho 'Clearing application log...'\nrm -f \"%s\"\necho 'Starting new process...'\nnohup java %s -Dspring.config.location=\"%s\" -jar \"%s\" > \"%s\" 2>&1 &\nJAVA_PID=$!\necho $JAVA_PID > \"%s\"\necho 'Process started with PID: $JAVA_PID'\nexit 0",
            installDir,
            pidFile,
            pidFile,
            pidFile,
            jarPath,
            javaOutPath,
            javaOpts != null ? javaOpts : "",
            ymlConfigPath,
            jarPath,
            javaOutPath,
            Paths.get(installDir, pidFile).toString()
         );
         Path scriptPath = Paths.get(installDir, "restart_service.sh");

         try {
            Files.write(scriptPath, scriptContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            try {
               Files.setPosixFilePermissions(
                  scriptPath,
                  Sets.newHashSet(new PosixFilePermission[]{PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE})
               );
            } catch (UnsupportedOperationException var13) {
               log.warn("无法设置文件权限（可能是在Windows系统）");
            }
         } catch (IOException var14) {
            throw new Exception("创建重启脚本失败: " + var14.getMessage());
         }

         ProcessBuilder pb = new ProcessBuilder();
         pb.directory(new File(installDir));
         if (System.getProperty("os.name").toLowerCase().contains("win")) {
            pb.command("cmd.exe", "/c", scriptPath.toString());
         } else {
            pb.command("bash", scriptPath.toString());
         }

         pb.redirectErrorStream(true);
         pb.redirectOutput(Redirect.to(new File(installDir, "restart_output.log")));

         try {
            this.updateProgress.setStatus("COMPLETED");
            this.updateProgress.setProgress(100);
            this.saveProgressToRedis();
            Process process = pb.start();
            log.info("启动服务重启进程，PID: {}", process.pid());
            CompletableFuture.runAsync(() -> {
               try {
                  int exitCode = process.waitFor();
                  if (exitCode != 0) {
                     synchronized (this) {
                        log.error("服务重启失败，退出码: {}", exitCode);
                        this.updateProgress.setStatus("FAILED");
                        this.updateProgress.setErrorMessage("服务重启失败，退出码: " + exitCode);
                        this.saveProgressToRedis();
                     }
                  } else {
                     log.info("服务重启脚本已执行完成");
                  }
               } catch (InterruptedException var9x) {
                  Thread.currentThread().interrupt();
                  log.error("重启进程监控被中断", var9x);

                  try {
                     synchronized (this) {
                        this.updateProgress.setStatus("FAILED");
                        this.updateProgress.setErrorMessage("重启进程监控被中断");
                        this.saveProgressToRedis();
                     }
                  } catch (Throwable var7x) {
                     log.warn("进程即将退出，保存失败状态到 Redis 失败", var7x);
                  }
               }
            });
         } catch (IOException var12) {
            throw new Exception("启动重启进程失败: " + var12.getMessage());
         }
      }
   }

   @FunctionalInterface
   private interface ProgressCallback {
      void onProgress(int progress);
   }
}

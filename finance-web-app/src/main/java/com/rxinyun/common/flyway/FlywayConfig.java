package com.rxinyun.common.flyway;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
   @Value("${spring.datasource.dynamic.datasource.master.url}")
   private String dbUrl;
   @Value("${spring.datasource.dynamic.datasource.master.username}")
   private String dbUser;
   @Value("${spring.datasource.dynamic.datasource.master.password}")
   private String dbPassword;
   @Value("${spring.flyway.backup.enabled:true}")
   private boolean backupEnabled;
   @Value("${spring.flyway.backup.skip-empty-db:true}")
   private boolean skipEmptyDb;

   public FlywayConfig() {
   }

   @Bean
   public DatabaseBackupCallback databaseBackupCallback() {
      if (!this.backupEnabled) {
         return null;
      } else {
         String userHome = System.getProperty("user.home");
         String backupDir = userHome + File.separator + "runxin_backups";
         return new DatabaseBackupCallback(this.dbUrl, this.dbUser, this.dbPassword, backupDir, this.skipEmptyDb);
      }
   }
}

package com.rxinyun.common.flyway;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseBackupCallback implements Callback {
   private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupCallback.class);
   private final String dbUrl;
   private final String dbUser;
   private final String dbPassword;
   private final String backupDir;
   private final boolean skipEmptyDb;

   public DatabaseBackupCallback(String dbUrl, String dbUser, String dbPassword, String backupDir, boolean skipEmptyDb) {
      this.dbUrl = dbUrl;
      this.dbUser = dbUser;
      this.dbPassword = dbPassword;
      this.backupDir = backupDir;
      this.skipEmptyDb = skipEmptyDb;
   }

   public boolean supports(Event event, Context context) {
      return event == Event.BEFORE_MIGRATE || event == Event.BEFORE_EACH_MIGRATE;
   }

   public boolean canHandleInTransaction(Event event, Context context) {
      return true;
   }

   public void handle(Event event, Context context) {
      if (event == Event.BEFORE_MIGRATE) {
         this.backupDatabase();
      }
   }

   public String getCallbackName() {
      return "database-backup-callback";
   }

   private boolean isDatabaseEmpty(Connection connection) throws SQLException {
      DatabaseMetaData metaData = connection.getMetaData();

      boolean var4;
      try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
         var4 = !tables.next();
      }

      return var4;
   }

   private void backupDatabase() {
      Connection connection = null;
      Statement stmt = null;

      try {
         String cleanUrl = this.dbUrl.split("\\?")[0];
         String dbName = cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1).replaceAll("[^a-zA-Z0-9_]", "_");
         File dir = new File(this.backupDir);
         if (!dir.exists()) {
            dir.mkdirs();
         }

         connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
         if (!this.skipEmptyDb || !this.isDatabaseEmpty(connection)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFile = this.backupDir + File.separator + "backup_" + dbName + "_" + timeStamp + ".sql";
            stmt = connection.createStatement();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(dbName, null, "%", new String[]{"TABLE"});

            try (PrintWriter writer = new PrintWriter(new FileWriter(backupFile))) {
               while (tables.next()) {
                  String tableName = tables.getString("TABLE_NAME");
                  writer.println("-- Table structure for table `" + tableName + "`");
                  writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");
                  ResultSet createTableRs = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`");
                  if (createTableRs.next()) {
                     writer.println(createTableRs.getString(2) + ";");
                  }

                  createTableRs.close();
                  writer.println();
               }

               tables = metaData.getTables(dbName, null, "%", new String[]{"TABLE"});

               while (tables.next()) {
                  String tableName = tables.getString("TABLE_NAME");
                  writer.println("-- Data for table `" + tableName + "`");
                  ResultSet dataRs = stmt.executeQuery("SELECT * FROM `" + tableName + "`");
                  ResultSetMetaData rsmd = dataRs.getMetaData();
                  int columnCount = rsmd.getColumnCount();

                  while (dataRs.next()) {
                     writer.print("INSERT INTO `" + tableName + "` VALUES (");

                     for (int i = 1; i <= columnCount; i++) {
                        if (i > 1) {
                           writer.print(", ");
                        }

                        Object value = dataRs.getObject(i);
                        if (value == null) {
                           writer.print("NULL");
                        } else {
                           String strValue = value.toString();
                           strValue = strValue.replace("'", "''");
                           writer.print("'" + strValue + "'");
                        }
                     }

                     writer.println(");");
                  }

                  dataRs.close();
                  writer.println();
               }

               logger.info("数据库备份成功，文件保存在: {}", backupFile);
               return;
            }
         }

         logger.info("数据库为空且配置了跳过空数据库备份，跳过备份");
      } catch (Exception var31) {
         logger.error("数据库备份过程中发生异常", var31);
         return;
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }

            if (connection != null) {
               connection.close();
            }
         } catch (SQLException var28) {
            logger.error("关闭数据库连接时发生异常", var28);
         }
      }
   }
}

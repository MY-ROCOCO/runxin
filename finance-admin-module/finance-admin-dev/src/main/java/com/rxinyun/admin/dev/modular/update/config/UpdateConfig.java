package com.rxinyun.admin.dev.modular.update.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
   prefix = "update"
)
public class UpdateConfig {
   private UpdateConfig.Server server = new UpdateConfig.Server();
   private UpdateConfig.Download download = new UpdateConfig.Download();
   private UpdateConfig.Install install = new UpdateConfig.Install();

   public UpdateConfig.Server getServer() {
      return this.server;
   }

   public UpdateConfig.Download getDownload() {
      return this.download;
   }

   public UpdateConfig.Install getInstall() {
      return this.install;
   }

   public void setServer(final UpdateConfig.Server server) {
      this.server = server;
   }

   public void setDownload(final UpdateConfig.Download download) {
      this.download = download;
   }

   public void setInstall(final UpdateConfig.Install install) {
      this.install = install;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UpdateConfig other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$server = this.getServer();
         Object other$server = other.getServer();
         if (this$server == null ? other$server == null : this$server.equals(other$server)) {
            Object this$download = this.getDownload();
            Object other$download = other.getDownload();
            if (this$download == null ? other$download == null : this$download.equals(other$download)) {
               Object this$install = this.getInstall();
               Object other$install = other.getInstall();
               return this$install == null ? other$install == null : this$install.equals(other$install);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof UpdateConfig;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $server = this.getServer();
      result = result * 59 + ($server == null ? 43 : $server.hashCode());
      Object $download = this.getDownload();
      result = result * 59 + ($download == null ? 43 : $download.hashCode());
      Object $install = this.getInstall();
      return result * 59 + ($install == null ? 43 : $install.hashCode());
   }

   @Override
   public String toString() {
      return "UpdateConfig(server=" + this.getServer() + ", download=" + this.getDownload() + ", install=" + this.getInstall() + ")";
   }

   public static class Download {
      private String dir = "/update";

      public String getDir() {
         return this.dir;
      }

      public void setDir(final String dir) {
         this.dir = dir;
      }

      @Override
      public boolean equals(final Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof UpdateConfig.Download other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$dir = this.getDir();
            Object other$dir = other.getDir();
            return this$dir == null ? other$dir == null : this$dir.equals(other$dir);
         }
      }

      protected boolean canEqual(final Object other) {
         return other instanceof UpdateConfig.Download;
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $dir = this.getDir();
         return result * 59 + ($dir == null ? 43 : $dir.hashCode());
      }

      @Override
      public String toString() {
         return "UpdateConfig.Download(dir=" + this.getDir() + ")";
      }
   }

   public static class Install {
      private String dir = "./";
      private String backupDir = "./backup";
      private boolean autoRestart = false;

      public String getDir() {
         return this.dir;
      }

      public String getBackupDir() {
         return this.backupDir;
      }

      public boolean isAutoRestart() {
         return this.autoRestart;
      }

      public void setDir(final String dir) {
         this.dir = dir;
      }

      public void setBackupDir(final String backupDir) {
         this.backupDir = backupDir;
      }

      public void setAutoRestart(final boolean autoRestart) {
         this.autoRestart = autoRestart;
      }

      @Override
      public boolean equals(final Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof UpdateConfig.Install other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.isAutoRestart() != other.isAutoRestart()) {
            return false;
         } else {
            Object this$dir = this.getDir();
            Object other$dir = other.getDir();
            if (this$dir == null ? other$dir == null : this$dir.equals(other$dir)) {
               Object this$backupDir = this.getBackupDir();
               Object other$backupDir = other.getBackupDir();
               return this$backupDir == null ? other$backupDir == null : this$backupDir.equals(other$backupDir);
            } else {
               return false;
            }
         }
      }

      protected boolean canEqual(final Object other) {
         return other instanceof UpdateConfig.Install;
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + (this.isAutoRestart() ? 79 : 97);
         Object $dir = this.getDir();
         result = result * 59 + ($dir == null ? 43 : $dir.hashCode());
         Object $backupDir = this.getBackupDir();
         return result * 59 + ($backupDir == null ? 43 : $backupDir.hashCode());
      }

      @Override
      public String toString() {
         return "UpdateConfig.Install(dir=" + this.getDir() + ", backupDir=" + this.getBackupDir() + ", autoRestart=" + this.isAutoRestart() + ")";
      }
   }

   public static class Server {
      private String url = "https://api.reckonkvm.com";
      private String stopCommand = "kill -9 $(ps aux | grep 'app.jar' | grep -v grep | awk '{print $2}')";
      private int commandTimeout = 30;
      private int stopWaitTime = 5;

      public String getUrl() {
         return this.url;
      }

      public String getStopCommand() {
         return this.stopCommand;
      }

      public int getCommandTimeout() {
         return this.commandTimeout;
      }

      public int getStopWaitTime() {
         return this.stopWaitTime;
      }

      public void setUrl(final String url) {
         this.url = url;
      }

      public void setStopCommand(final String stopCommand) {
         this.stopCommand = stopCommand;
      }

      public void setCommandTimeout(final int commandTimeout) {
         this.commandTimeout = commandTimeout;
      }

      public void setStopWaitTime(final int stopWaitTime) {
         this.stopWaitTime = stopWaitTime;
      }

      @Override
      public boolean equals(final Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof UpdateConfig.Server other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getCommandTimeout() != other.getCommandTimeout()) {
            return false;
         } else if (this.getStopWaitTime() != other.getStopWaitTime()) {
            return false;
         } else {
            Object this$url = this.getUrl();
            Object other$url = other.getUrl();
            if (this$url == null ? other$url == null : this$url.equals(other$url)) {
               Object this$stopCommand = this.getStopCommand();
               Object other$stopCommand = other.getStopCommand();
               return this$stopCommand == null ? other$stopCommand == null : this$stopCommand.equals(other$stopCommand);
            } else {
               return false;
            }
         }
      }

      protected boolean canEqual(final Object other) {
         return other instanceof UpdateConfig.Server;
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getCommandTimeout();
         result = result * 59 + this.getStopWaitTime();
         Object $url = this.getUrl();
         result = result * 59 + ($url == null ? 43 : $url.hashCode());
         Object $stopCommand = this.getStopCommand();
         return result * 59 + ($stopCommand == null ? 43 : $stopCommand.hashCode());
      }

      @Override
      public String toString() {
         return "UpdateConfig.Server(url="
            + this.getUrl()
            + ", stopCommand="
            + this.getStopCommand()
            + ", commandTimeout="
            + this.getCommandTimeout()
            + ", stopWaitTime="
            + this.getStopWaitTime()
            + ")";
      }
   }
}

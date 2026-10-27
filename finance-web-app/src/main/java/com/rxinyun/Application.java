package com.rxinyun;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {
   private static final Logger log = LoggerFactory.getLogger(Application.class);

   public Application() {
   }

   public static void main(String[] args) {
      try {
         ConfigurableApplicationContext context = new SpringApplicationBuilder(new Class[]{Application.class}).bannerMode(Mode.OFF).run(args);
         Environment env = context.getEnvironment();
         log.info(
            "\n----------------------------------------------------------\nApplication is running! Access URLs:\nLocal:    http://localhost:{}\nDoc:      http://localhost:{}/doc.html\n----------------------------------------------------------",
            env.getProperty("server.port"),
            env.getProperty("server.port")
         );
      } catch (Throwable var3) {
         throw var3;
      }
   }

   @GetMapping({"/"})
   public String index() {
      return "WELCOME";
   }

   static {
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
      System.setProperty("user.timezone", "Asia/Shanghai");
      System.setProperty("druid.mysql.usePingMethod", "false");
      System.setProperty("file.encoding", "UTF-8");
      System.setProperty("console.encoding", "UTF-8");

      try {
         System.setOut(new PrintStream(System.out, true, "UTF-8"));
         System.setErr(new PrintStream(System.err, true, "UTF-8"));
      } catch (UnsupportedEncodingException var1) {
      }
   }
}

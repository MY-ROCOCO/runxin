package com.rxinyun.admin.production.modular.dns.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class DnsTokenUtil {
   private static final byte[] AES_KEY = "runxin_dns_key16".getBytes(StandardCharsets.UTF_8);
   private static final String CALLBACK_SALT = "runxin_dns_callback";

   private DnsTokenUtil() {
   }

   public static String generateToken() {
      return RandomUtil.randomString(32);
   }

   public static String encryptApiToken(String token) {
      AES aes = SecureUtil.aes(AES_KEY);
      return aes.encryptBase64(token);
   }

   public static String decryptApiToken(String encryptedToken) {
      AES aes = SecureUtil.aes(AES_KEY);
      return aes.decryptStr(encryptedToken);
   }

   public static String maskToken(String token) {
      if (StrUtil.isBlank(token)) {
         return "";
      } else {
         return token.length() <= 8 ? "****" : token.substring(0, 4) + "****" + token.substring(token.length() - 4);
      }
   }

   public static String hashCallbackToken(String token) {
      return SecureUtil.sha256("runxin_dns_callback:" + token);
   }

   public static boolean matchesCallbackToken(String rawToken, String storedHash) {
      return StrUtil.hasBlank(new CharSequence[]{rawToken, storedHash})
         ? false
         : MessageDigest.isEqual(hashCallbackToken(rawToken).getBytes(StandardCharsets.UTF_8), storedHash.getBytes(StandardCharsets.UTF_8));
   }
}

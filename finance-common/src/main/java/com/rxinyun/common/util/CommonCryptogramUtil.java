package com.rxinyun.common.util;

import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonCryptogramUtil {
   private static final Logger log = LoggerFactory.getLogger(CommonCryptogramUtil.class);
   private static final String PUBLIC_KEY = "04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54";
   private static final String PRIVATE_KEY = "3037723d47292171677ec8bd7dc9af696c7472bc5f251b2cec07e65fdef22e25";
   private static final String KEY = "0123456789abcdeffedcba9876543210";

   public static String doSm2Encrypt(String str) {
      return Sm2.doEncrypt(
         str, "04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54"
      );
   }

   public static String doSm2Decrypt(String str) {
      return Sm2.doDecrypt(str, "3037723d47292171677ec8bd7dc9af696c7472bc5f251b2cec07e65fdef22e25");
   }

   public static String doSm4CbcEncrypt(String str) {
      Sm4Options sm4Options4 = new Sm4Options();
      sm4Options4.setMode("cbc");
      sm4Options4.setIv("fedcba98765432100123456789abcdef");
      return Sm4.encrypt(str, "0123456789abcdeffedcba9876543210", sm4Options4);
   }

   public static String doSm4CbcDecrypt(String str) {
      Sm4Options sm4Options8 = new Sm4Options();
      sm4Options8.setMode("cbc");
      sm4Options8.setIv("fedcba98765432100123456789abcdef");
      String docString = Sm4.decrypt(str, "0123456789abcdeffedcba9876543210", sm4Options8);
      if ("".equals(docString)) {
         log.warn(">>> 字段解密失败，返回原文值：{}", str);
         return str;
      } else {
         return docString;
      }
   }

   public static String doSignature(String str) {
      return Sm2.doSignature(str, "3037723d47292171677ec8bd7dc9af696c7472bc5f251b2cec07e65fdef22e25");
   }

   public static boolean doVerifySignature(String originalStr, String str) {
      return Sm2.doVerifySignature(
         originalStr, str, "04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54"
      );
   }

   public static String doHashValue(String str) {
      return Sm3.sm3(str);
   }
}

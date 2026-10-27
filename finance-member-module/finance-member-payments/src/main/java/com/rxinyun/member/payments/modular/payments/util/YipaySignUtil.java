package com.rxinyun.member.payments.modular.payments.util;

import cn.hutool.core.util.StrUtil;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.util.DigestUtils;

public final class YipaySignUtil {
   private YipaySignUtil() {
   }

   public static String sign(Map<String, String> params, String merchantKey) {
      String signContent = buildSignContent(params) + merchantKey;
      return DigestUtils.md5DigestAsHex(signContent.getBytes(StandardCharsets.UTF_8));
   }

   public static boolean verify(Map<String, String> params, String merchantKey) {
      String sign = params.get("sign");
      return StrUtil.isBlank(sign) ? false : sign(params, merchantKey).equalsIgnoreCase(sign);
   }

   public static String buildSignContent(Map<String, String> params) {
      return params.entrySet()
         .stream()
         .filter(entry -> StrUtil.isNotBlank(entry.getValue()))
         .filter(entry -> !"sign".equals(entry.getKey()) && !"sign_type".equals(entry.getKey()))
         .sorted(Comparator.comparing(Entry::getKey))
         .map(entry -> entry.getKey() + "=" + entry.getValue())
         .collect(Collectors.joining("&"));
   }
}

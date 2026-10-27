package com.rxinyun.common.vhost;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.UniqueHostnameGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class VhostOrderOptionSupport {
   private static final Pattern DOMAIN = Pattern.compile("^(?=.{1,255}$)(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-))+$");

   private VhostOrderOptionSupport() {
   }

   public static String randomComDomain() {
      return UniqueHostnameGenerator.generateUniqueHostname(10) + ".com";
   }

   public static List<String> mergeBindDomains(List<String> current, List<String> panel, String extra) {
      List<String> names = new ArrayList<>();
      addDomain(names, CollUtil.isEmpty(current) ? null : current.get(0));
      addDomains(names, current);
      addDomain(names, extra);
      if (CollUtil.isNotEmpty(panel)) {
         for (String item : panel) {
            if (containsDomain(current, item) || sameBindDomain(item, extra)) {
               addDomain(names, item);
            }
         }
      }

      return names;
   }

   public static void assertCanRemoveDomain(List<String> current, String domain) {
      if (CollUtil.isEmpty(current)) {
         throw new CommonException("尚未绑定域名");
      } else if (sameBindDomain(current.get(0), domain)) {
         throw new CommonException("系统开通域名不能删除");
      } else if (!containsDomain(current, domain)) {
         throw new CommonException("未绑定该域名");
      }
   }

   public static List<String> removeBindDomain(List<String> current, List<String> panel, String domain) {
      assertCanRemoveDomain(current, domain);
      List<String> merged = mergeBindDomains(current, panel, null);
      merged.removeIf(item -> sameBindDomain(item, domain));
      return merged;
   }

   public static void requireTrafficSize(Integer trafficSize, Integer minSize, Integer maxSize) {
      if (trafficSize == null) {
         throw new CommonException("购买流量不能为空");
      } else if (minSize != null && trafficSize < minSize) {
         throw new CommonException("购买流量不能小于最小流量");
      } else if (maxSize != null && trafficSize > maxSize) {
         throw new CommonException("购买流量不能大于最大流量");
      }
   }

   public static String requirePhpVersion(String selected, String allowed) {
      String phpVersion = StrUtil.trim(selected);
      if (StrUtil.isBlank(phpVersion)) {
         throw new CommonException("PHP版本不能为空");
      } else if (isStatic(phpVersion)) {
         return "00";
      } else {
         List<String> options = StrUtil.splitTrim(StrUtil.blankToDefault(allowed, "").replace('，', ','), ',');
         if (CollUtil.isEmpty(options)) {
            throw new CommonException("该版本未配置PHP，仅支持纯静态");
         } else {
            for (String option : options) {
               if (matchesPhp(phpVersion, option)) {
                  return option;
               }
            }

            throw new CommonException("所选PHP版本不在当前套餐支持范围内");
         }
      }
   }

   public static boolean matchesPhpVersion(String selected, String option) {
      return StrUtil.isNotBlank(selected) && StrUtil.isNotBlank(option) && matchesPhp(selected, option);
   }

   public static boolean allowedByPackage(String selected, String allowed) {
      String phpVersion = StrUtil.trim(selected);
      if (StrUtil.isBlank(phpVersion)) {
         return false;
      } else if (!isStatic(phpVersion) && !"00".equals(phpVersion)) {
         List<String> options = StrUtil.splitTrim(StrUtil.blankToDefault(allowed, "").replace('，', ','), ',');
         if (CollUtil.isEmpty(options)) {
            return false;
         } else {
            for (String option : options) {
               if (matchesPhp(phpVersion, option)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public static String normalizeDomain(String raw) {
      String domain = StrUtil.trim(raw);
      if (StrUtil.isBlank(domain)) {
         throw new CommonException("绑定域名不能为空");
      } else {
         domain = StrUtil.removePrefixIgnoreCase(domain, "http://");
         domain = StrUtil.removePrefixIgnoreCase(domain, "https://");
         int slash = domain.indexOf(47);
         if (slash >= 0) {
            domain = domain.substring(0, slash);
         }

         domain = StrUtil.removeSuffix(domain, ".");
         String port = readPort(domain);
         domain = stripPort(domain).toLowerCase(Locale.ROOT);
         if (!DOMAIN.matcher(domain).matches() || !domain.contains(".")) {
            throw new CommonException("绑定域名格式不正确：" + domain);
         } else {
            return StrUtil.isNotBlank(port) && !"80".equals(port) ? domain + ":" + port : domain;
         }
      }
   }

   public static String stripPort(String raw) {
      String domain = StrUtil.trim(raw);
      if (StrUtil.isBlank(domain)) {
         return domain;
      } else {
         int colon = domain.lastIndexOf(58);
         return colon > 0 && domain.substring(colon + 1).chars().allMatch(Character::isDigit) ? domain.substring(0, colon) : domain;
      }
   }

   public static String readPort(String raw) {
      String domain = StrUtil.trim(raw);
      if (StrUtil.isBlank(domain)) {
         return "80";
      } else {
         int colon = domain.lastIndexOf(58);
         if (colon > 0) {
            String port = domain.substring(colon + 1);
            if (port.chars().allMatch(Character::isDigit)) {
               int value = Integer.parseInt(port);
               if (value >= 1 && value <= 65535) {
                  return String.valueOf(value);
               }

               throw new CommonException("域名端口不正确：" + port);
            }
         }

         return "80";
      }
   }

   public static boolean sameBindDomain(String left, String right) {
      return !StrUtil.isBlank(left) && !StrUtil.isBlank(right)
         ? StrUtil.equalsIgnoreCase(stripPort(left), stripPort(right)) && StrUtil.equals(readPort(left), readPort(right))
         : false;
   }

   public static boolean containsDomain(List<String> domains, String domain) {
      if (!CollUtil.isEmpty(domains) && !StrUtil.isBlank(domain)) {
         for (String item : domains) {
            if (sameBindDomain(item, domain)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static void addDomains(List<String> names, List<String> domains) {
      if (!CollUtil.isEmpty(domains)) {
         for (String item : domains) {
            addDomain(names, item);
         }
      }
   }

   private static void addDomain(List<String> names, String raw) {
      if (!StrUtil.isBlank(raw)) {
         String domain;
         try {
            domain = normalizeDomain(raw);
         } catch (CommonException var4) {
            domain = StrUtil.trim(raw).toLowerCase(Locale.ROOT);
         }

         if (!StrUtil.isBlank(domain) && !containsDomain(names, domain)) {
            names.add(domain);
         }
      }
   }

   private static boolean isStatic(String phpVersion) {
      return "00".equals(phpVersion) || phpVersion.contains("静态");
   }

   private static boolean matchesPhp(String selected, String option) {
      return StrUtil.equalsIgnoreCase(selected, option) || StrUtil.equals(digits(selected), digits(option));
   }

   private static String digits(String value) {
      return StrUtil.blankToDefault(value, "").replaceAll("\\D", "");
   }
}

package com.rxinyun.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.DnsNsRecordResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class DnsLookupUtil {
   private static final String DEFAULT_DNS_SERVER = "8.8.8.8";

   private DnsLookupUtil() {
   }

   public static List<String> queryNsRecords(String domainName, String dnsServer) {
      String domain = normalizeDomain(domainName);
      String server = StrUtil.blankToDefault(StrUtil.trim(dnsServer), "8.8.8.8");

      try {
         Hashtable<String, String> env = new Hashtable<>();
         env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         env.put("java.naming.provider.url", "dns://" + server + "/");
         Attributes attributes = new InitialDirContext(env).getAttributes(domain, new String[]{"NS"});
         Attribute nsAttribute = attributes.get("NS");
         if (nsAttribute != null && nsAttribute.size() != 0) {
            List<String> records = new ArrayList<>();
            NamingEnumeration<?> values = nsAttribute.getAll();

            while (values.hasMore()) {
               String ns = StrUtil.removeSuffix(StrUtil.trim(String.valueOf(values.next())), ".");
               if (StrUtil.isNotBlank(ns)) {
                  records.add(ns);
               }
            }

            return CollUtil.distinct(records);
         } else {
            return Collections.emptyList();
         }
      } catch (Exception var10) {
         throw new CommonException("查询域名NS记录失败：{}", var10.getMessage());
      }
   }

   public static List<DnsNsRecordResult> queryNsRecords(List<String> domainNames, String dnsServer) {
      if (CollUtil.isEmpty(domainNames)) {
         throw new CommonException("域名列表不能为空");
      } else {
         String server = StrUtil.blankToDefault(StrUtil.trim(dnsServer), "8.8.8.8");
         List<DnsNsRecordResult> results = new ArrayList<>();

         for (String domainName : domainNames) {
            DnsNsRecordResult result = new DnsNsRecordResult();
            result.setDomainName(domainName);
            result.setDnsServer(server);

            try {
               String domain = normalizeDomain(domainName);
               result.setDomainName(domain);
               result.setNsRecords(queryNsRecords(domain, server));
               result.setSuccess(true);
            } catch (Exception var8) {
               result.setNsRecords(Collections.emptyList());
               result.setSuccess(false);
               result.setErrorMessage(var8.getMessage());
            }

            results.add(result);
         }

         return results;
      }
   }

   private static String normalizeDomain(String domainName) {
      String domain = StrUtil.trim(domainName).toLowerCase();

      while (domain.endsWith(".")) {
         domain = domain.substring(0, domain.length() - 1);
      }

      if (StrUtil.isBlank(domain)) {
         throw new CommonException("域名不能为空");
      } else {
         return domain;
      }
   }
}

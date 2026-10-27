package com.rxinyun.common.vhost.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.UniqueHostnameGenerator;
import com.rxinyun.common.vhost.VhostPanelAdapter;
import com.rxinyun.common.vhost.param.VhostCreateCommand;
import com.rxinyun.common.vhost.param.VhostExpireCommand;
import com.rxinyun.common.vhost.param.VhostPasswordCommand;
import com.rxinyun.common.vhost.param.VhostPowerCommand;
import com.rxinyun.common.vhost.result.VhostCreateResult;
import com.rxinyun.common.vhost.result.VhostNetWork;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import com.rxinyun.common.vhost.result.VhostSystemTotal;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BaotaVhostPanelAdapter implements VhostPanelAdapter {
   private static final Logger log = LoggerFactory.getLogger(BaotaVhostPanelAdapter.class);
   private static final String VERSION = "BAOTA";
   private final OkHttpClient httpClient = this.buildClient();

   @Override
   public String controlledVersion() {
      return "BAOTA";
   }

   @Override
   public VhostCreateResult createHost(VhostCreateCommand command) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         return VhostCreateResult.fail("主控面板地址或密钥为空");
      } else if (StrUtil.isBlank(command.getDomain())) {
         return VhostCreateResult.fail("绑定域名不能为空");
      } else {
         String domain = command.getDomain().trim();
         this.fillCreateAccounts(command, domain);
         String sitePath = "/www/wwwroot/" + this.siteDir(command.getHostName(), domain);
         BaotaVhostPanelAdapter.PhpSpec phpSpec = this.resolvePhp(command.getPhpVersion());
         JSONObject webname = new JSONObject();
         webname.put("domain", domain);
         webname.put("domainlist", new String[0]);
         webname.put("count", 0);
         FormBody body = this.signedForm(command.getApiKey())
            .add("action", "AddSite")
            .add("webname", webname.toJSONString())
            .add("path", sitePath)
            .add("type", phpSpec.type)
            .add("version", phpSpec.version)
            .add("port", "80")
            .add("type_id", "0")
            .add("ps", StrUtil.blankToDefault(command.getRemark(), StrUtil.blankToDefault(command.getHostName(), domain)))
            .add("ftp", "true")
            .add("ftp_username", command.getFtpUser())
            .add("ftp_password", command.getFtpPassword())
            .add("sql", "true")
            .add("codeing", "utf8mb4")
            .add("datauser", command.getDbUser())
            .add("datapassword", command.getDbPassword())
            .build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=AddSite";
         log.info(
            "宝塔创建网站: url={}, domain={}, path={}, ftpUser={}, dbUser={}, phpType={}, phpVersion={}",
            new Object[]{url, domain, sitePath, command.getFtpUser(), command.getDbUser(), phpSpec.type, phpSpec.version}
         );

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            VhostCreateResult existing;
            label93: {
               VhostCreateResult var19;
               label94: {
                  try {
                     String respBody = response.body() == null ? "" : response.body().string();
                     log.info("宝塔创建网站响应: httpCode={}, body={}", response.code(), this.maskSecrets(respBody));
                     VhostCreateResult result = this.parse(domain, response.code(), respBody);
                     if (result.isSuccess() && result.accountsReady()) {
                        existing = result;
                        break label93;
                     }

                     existing = this.findExistingHost(command);
                     if (existing != null && StrUtil.isNotBlank(existing.getPanelHostId())) {
                        this.applyCommandAccounts(existing, command);
                        existing.setSuccess(existing.accountsReady());
                        existing.setMessage("网站已存在，跳过重复创建");
                        var19 = existing;
                        break label94;
                     }

                     this.applyCommandAccounts(result, command);
                     var19 = result;
                  } catch (Throwable var14) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var19;
               }

               if (response != null) {
                  response.close();
               }

               return var19;
            }

            if (response != null) {
               response.close();
            }

            return existing;
         } catch (Exception var15) {
            log.warn("宝塔创建网站异常: url={}, message={}", url, var15.getMessage());
            VhostCreateResult existingx = this.findExistingHost(command);
            if (existingx != null && StrUtil.isNotBlank(existingx.getPanelHostId())) {
               this.applyCommandAccounts(existingx, command);
               existingx.setSuccess(existingx.accountsReady());
               existingx.setMessage("网站已存在，跳过重复创建");
               return existingx;
            } else {
               VhostCreateResult failed = VhostCreateResult.fail("调用宝塔失败：" + var15.getMessage());
               this.applyCommandAccounts(failed, command);
               return failed;
            }
         }
      }
   }

   @Override
   public VhostCreateResult findExistingHost(VhostCreateCommand command) {
      if (command != null && !StrUtil.isBlank(command.getPanelUrl()) && !StrUtil.isBlank(command.getApiKey())) {
         String domain = StrUtil.trim(command.getDomain());
         String hostName = StrUtil.trim(command.getHostName());
         JSONArray sites = this.queryTable(command.getPanelUrl(), command.getApiKey(), "sites", StrUtil.blankToDefault(domain, hostName));
         JSONObject site = this.matchSite(sites, domain, hostName);
         if (site == null && StrUtil.isNotBlank(hostName)) {
            sites = this.queryTable(command.getPanelUrl(), command.getApiKey(), "sites", this.siteDir(hostName, domain));
            site = this.matchSite(sites, domain, hostName);
         }

         if (site == null) {
            return null;
         } else {
            VhostCreateResult result = new VhostCreateResult();
            result.setSuccess(true);
            result.setDomain(StrUtil.blankToDefault(this.text(site, "name"), domain));
            result.setPanelHostId(this.text(site, "id"));
            result.setMessage("网站已存在");
            this.fillAccounts(result, command);
            this.applyCommandAccounts(result, command);
            return StrUtil.isBlank(result.getPanelHostId()) ? null : result;
         }
      } else {
         return null;
      }
   }

   @Override
   public String setExpireTime(VhostExpireCommand command) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         return "主控面板地址或密钥为空";
      } else if (StrUtil.isBlank(command.getPanelHostId())) {
         return "主控网站ID为空";
      } else if (command.getEndTime() == null) {
         return "到期时间不能为空";
      } else {
         String edate = this.formatDate(command.getEndTime());
         FormBody body = this.signedForm(command.getApiKey()).add("action", "SetEdate").add("id", command.getPanelHostId()).add("edate", edate).build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=SetEdate";
         log.info("宝塔设置到期时间: url={}, siteId={}, edate={}", new Object[]{url, command.getPanelHostId(), edate});

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            String var13;
            label85: {
               label86: {
                  try {
                     String respBody = response.body() == null ? "" : response.body().string();
                     log.info("宝塔设置到期时间响应: httpCode={}, body={}", response.code(), respBody);
                     JSONObject json = this.parseJson(respBody);
                     if (json == null) {
                        var13 = "宝塔设置到期时间响应无法解析，HTTP " + response.code();
                        break label85;
                     }

                     if (this.truthy(this.first(json, "status"))) {
                        var13 = null;
                        break label86;
                     }

                     var13 = StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔设置到期时间失败");
                  } catch (Throwable var10) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var13;
               }

               if (response != null) {
                  response.close();
               }

               return var13;
            }

            if (response != null) {
               response.close();
            }

            return var13;
         } catch (Exception var11) {
            log.warn("宝塔设置到期时间异常: url={}, message={}", url, var11.getMessage());
            return "调用宝塔失败：" + var11.getMessage();
         }
      }
   }

   @Override
   public String startSite(VhostPowerCommand command) {
      return this.sitePower(command, "SiteStart", "启用");
   }

   @Override
   public String stopSite(VhostPowerCommand command) {
      return this.sitePower(command, "SiteStop", "停用");
   }

   @Override
   public String deleteSite(VhostPowerCommand command) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         return "主控面板地址或密钥为空";
      } else if (!StrUtil.isBlank(command.getPanelHostId()) && !StrUtil.isBlank(command.getDomain())) {
         String webname = this.stripPort(command.getDomain());
         FormBody body = this.signedForm(command.getApiKey())
            .add("action", "DeleteSite")
            .add("id", command.getPanelHostId())
            .add("webname", webname)
            .add("ftp", "1")
            .add("database", "1")
            .add("path", "1")
            .build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=DeleteSite";
         log.info("宝塔删除网站: url={}, siteId={}, webname={}", new Object[]{url, command.getPanelHostId(), webname});

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            String var13;
            label82: {
               label83: {
                  try {
                     String respBody = response.body() == null ? "" : response.body().string();
                     log.info("宝塔删除网站响应: httpCode={}, body={}", response.code(), respBody);
                     JSONObject json = this.parseJson(respBody);
                     if (json == null) {
                        var13 = "宝塔删除网站响应无法解析，HTTP " + response.code();
                        break label82;
                     }

                     if (this.truthy(this.first(json, "status"))) {
                        var13 = null;
                        break label83;
                     }

                     var13 = StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔删除网站失败");
                  } catch (Throwable var10) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var13;
               }

               if (response != null) {
                  response.close();
               }

               return var13;
            }

            if (response != null) {
               response.close();
            }

            return var13;
         } catch (Exception var11) {
            log.warn("宝塔删除网站异常: url={}, message={}", url, var11.getMessage());
            return "调用宝塔失败：" + var11.getMessage();
         }
      } else {
         return "主控网站ID和网站名称不能为空";
      }
   }

   private String sitePower(VhostPowerCommand command, String action, String label) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         return "主控面板地址或密钥为空";
      } else if (!StrUtil.isBlank(command.getPanelHostId()) && !StrUtil.isBlank(command.getDomain())) {
         FormBody body = this.signedForm(command.getApiKey())
            .add("action", action)
            .add("id", command.getPanelHostId())
            .add("name", command.getDomain())
            .build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=" + action;
         log.info("宝塔{}网站: url={}, siteId={}, domain={}", new Object[]{label, url, command.getPanelHostId(), command.getDomain()});

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            String var14;
            label82: {
               label83: {
                  try {
                     String respBody = response.body() == null ? "" : response.body().string();
                     log.info("宝塔{}网站响应: httpCode={}, body={}", new Object[]{label, response.code(), respBody});
                     JSONObject json = this.parseJson(respBody);
                     if (json == null) {
                        var14 = "宝塔" + label + "网站响应无法解析，HTTP " + response.code();
                        break label82;
                     }

                     if (this.truthy(this.first(json, "status"))) {
                        var14 = null;
                        break label83;
                     }

                     var14 = StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔" + label + "网站失败");
                  } catch (Throwable var11) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var10) {
                           var11.addSuppressed(var10);
                        }
                     }

                     throw var11;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var14;
               }

               if (response != null) {
                  response.close();
               }

               return var14;
            }

            if (response != null) {
               response.close();
            }

            return var14;
         } catch (Exception var12) {
            log.warn("宝塔{}网站异常: url={}, message={}", new Object[]{label, url, var12.getMessage()});
            return "调用宝塔失败：" + var12.getMessage();
         }
      } else {
         return "主控网站ID和域名不能为空";
      }
   }

   @Override
   public List<VhostPhpVersion> listPhpVersions(String panelUrl, String apiKey) {
      if (!StrUtil.isBlank(panelUrl) && !StrUtil.isBlank(apiKey)) {
         FormBody body = this.signedForm(apiKey).add("action", "GetPHPVersion").build();
         String url = StrUtil.removeSuffix(panelUrl.trim(), "/") + "/site?action=GetPHPVersion";
         log.info("宝塔获取PHP版本: url={}", url);

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            Object var17;
            try {
               String respBody = response.body() == null ? "" : response.body().string();
               log.info("宝塔获取PHP版本响应: httpCode={}, body={}", response.code(), respBody);
               JSONArray array = this.parsePhpArray(respBody);
               if (array == null) {
                  JSONObject json = this.parseJson(respBody);
                  throw new CommonException(
                     json == null ? "宝塔获取PHP版本响应无法解析" : StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔获取PHP版本失败")
                  );
               }

               List<VhostPhpVersion> versions = new ArrayList<>();

               for (int i = 0; i < array.size(); i++) {
                  JSONObject item = array.getJSONObject(i);
                  if (item != null) {
                     VhostPhpVersion version = new VhostPhpVersion();
                     version.setVersion(this.text(item, "version"));
                     version.setName(this.text(item, "name"));
                     version.setStatus(this.truthy(this.first(item, "status")));
                     versions.add(version);
                  }
               }

               var17 = versions;
            } catch (Throwable var13) {
               if (response != null) {
                  try {
                     response.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (response != null) {
               response.close();
            }

            return (List<VhostPhpVersion>)var17;
         } catch (CommonException var14) {
            throw var14;
         } catch (Exception var15) {
            log.warn("宝塔获取PHP版本异常: url={}, message={}", url, var15.getMessage());
            throw new CommonException("调用宝塔失败：" + var15.getMessage());
         }
      } else {
         throw new CommonException("主控面板地址或密钥为空");
      }
   }

   @Override
   public VhostSystemTotal getSystemTotal(String panelUrl, String apiKey) {
      JSONObject json = this.postSystem(panelUrl, apiKey, "GetSystemTotal", "系统基础统计");
      VhostSystemTotal result = new VhostSystemTotal();
      result.setSystem(this.text(json, "system"));
      result.setVersion(this.text(json, "version"));
      result.setTime(this.text(json, "time"));
      result.setCpuNum(this.intValue(json, "cpuNum"));
      result.setCpuRealUsed(this.decimalValue(json, "cpuRealUsed"));
      result.setMemTotal(this.longValue(json, "memTotal"));
      result.setMemRealUsed(this.longValue(json, "memRealUsed"));
      result.setMemFree(this.longValue(json, "memFree"));
      result.setMemCached(this.longValue(json, "memCached"));
      result.setMemBuffers(this.longValue(json, "memBuffers"));
      return result;
   }

   @Override
   public VhostNetWork getNetWork(String panelUrl, String apiKey) {
      JSONObject json = this.postSystem(panelUrl, apiKey, "GetNetWork", "实时状态");
      VhostNetWork result = new VhostNetWork();
      result.setDownTotal(this.longValue(json, "downTotal"));
      result.setUpTotal(this.longValue(json, "upTotal"));
      result.setDownPackets(this.longValue(json, "downPackets"));
      result.setUpPackets(this.longValue(json, "upPackets"));
      result.setDown(this.decimalValue(json, "down"));
      result.setUp(this.decimalValue(json, "up"));
      result.setCpu(this.toDecimalList(json.get("cpu")));
      JSONObject memJson = json.getJSONObject("mem");
      if (memJson != null) {
         VhostNetWork.Mem mem = new VhostNetWork.Mem();
         mem.setMemFree(this.longValue(memJson, "memFree"));
         mem.setMemTotal(this.longValue(memJson, "memTotal"));
         mem.setMemCached(this.longValue(memJson, "memCached"));
         mem.setMemBuffers(this.longValue(memJson, "memBuffers"));
         mem.setMemRealUsed(this.longValue(memJson, "memRealUsed"));
         result.setMem(mem);
      }

      return result;
   }

   @Override
   public String setPhpVersion(VhostPowerCommand command) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         return "主控面板地址或密钥为空";
      } else if (!StrUtil.isBlank(command.getDomain()) && !StrUtil.isBlank(command.getPhpVersion())) {
         String version = this.resolvePhp(command.getPhpVersion()).version();
         FormBody body = this.signedForm(command.getApiKey())
            .add("action", "SetPHPVersion")
            .add("siteName", command.getDomain())
            .add("version", version)
            .build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=SetPHPVersion";
         log.info("宝塔切换PHP版本: url={}, domain={}, version={}", new Object[]{url, command.getDomain(), version});

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            String var13;
            label82: {
               label83: {
                  try {
                     String respBody = response.body() == null ? "" : response.body().string();
                     log.info("宝塔切换PHP版本响应: httpCode={}, body={}", response.code(), respBody);
                     JSONObject json = this.parseJson(respBody);
                     if (json == null) {
                        var13 = "宝塔切换PHP版本响应无法解析，HTTP " + response.code();
                        break label82;
                     }

                     if (this.truthy(this.first(json, "status"))) {
                        var13 = null;
                        break label83;
                     }

                     var13 = StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔切换PHP版本失败");
                  } catch (Throwable var10) {
                     if (response != null) {
                        try {
                           response.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var13;
               }

               if (response != null) {
                  response.close();
               }

               return var13;
            }

            if (response != null) {
               response.close();
            }

            return var13;
         } catch (Exception var11) {
            log.warn("宝塔切换PHP版本异常: url={}, message={}", url, var11.getMessage());
            return "调用宝塔失败：" + var11.getMessage();
         }
      } else {
         return "网站域名和PHP版本不能为空";
      }
   }

   @Override
   public List<String> addDomain(VhostPowerCommand command) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         throw new CommonException("主控面板地址或密钥为空");
      } else if (!StrUtil.isBlank(command.getPanelHostId()) && !StrUtil.isBlank(command.getDomain()) && !StrUtil.isBlank(command.getBindDomain())) {
         String target = this.formatAddDomain(command.getBindDomain());
         String webname = this.stripPort(command.getDomain());
         FormBody body = this.signedForm(command.getApiKey())
            .add("action", "AddDomain")
            .add("id", command.getPanelHostId())
            .add("webname", webname)
            .add("domain", target)
            .build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=AddDomain";
         log.info("宝塔添加域名: url={}, siteId={}, webname={}, domain={}", new Object[]{url, command.getPanelHostId(), webname, target});

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            List var15;
            label83: {
               try {
                  String respBody = response.body() == null ? "" : response.body().string();
                  log.info("宝塔添加域名响应: httpCode={}, body={}", response.code(), respBody);
                  JSONObject json = this.parseJson(respBody);
                  if (json == null) {
                     throw new CommonException("宝塔添加域名响应无法解析，HTTP " + response.code());
                  }

                  List<String> domains = this.readDomainNames(json.get("domains"));
                  if (!domains.isEmpty()) {
                     var15 = domains;
                     break label83;
                  }

                  if (Boolean.FALSE.equals(json.getBoolean("status"))) {
                     throw new CommonException(StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔添加域名失败"));
                  }

                  var15 = domains;
               } catch (Throwable var12) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (response != null) {
                  response.close();
               }

               return var15;
            }

            if (response != null) {
               response.close();
            }

            return var15;
         } catch (CommonException var13) {
            throw var13;
         } catch (Exception var14) {
            log.warn("宝塔添加域名异常: url={}, message={}", url, var14.getMessage());
            throw new CommonException("调用宝塔失败：" + var14.getMessage());
         }
      } else {
         throw new CommonException("网站ID、主域名和新域名不能为空");
      }
   }

   @Override
   public List<String> deleteDomain(VhostPowerCommand command) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         throw new CommonException("主控面板地址或密钥为空");
      } else if (!StrUtil.isBlank(command.getPanelHostId()) && !StrUtil.isBlank(command.getDomain()) && !StrUtil.isBlank(command.getBindDomain())) {
         String target = this.stripPort(command.getBindDomain());
         String port = this.readPort(command.getBindDomain());
         String webname = this.stripPort(command.getDomain());
         FormBody body = this.signedForm(command.getApiKey())
            .add("action", "DelDomain")
            .add("id", command.getPanelHostId())
            .add("webname", webname)
            .add("domain", target)
            .add("port", port)
            .build();
         String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/site?action=DelDomain";
         log.info("宝塔删除域名: url={}, siteId={}, webname={}, domain={}, port={}", new Object[]{url, command.getPanelHostId(), webname, target, port});

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            List var16;
            label83: {
               try {
                  String respBody = response.body() == null ? "" : response.body().string();
                  log.info("宝塔删除域名响应: httpCode={}, body={}", response.code(), respBody);
                  JSONObject json = this.parseJson(respBody);
                  if (json == null) {
                     throw new CommonException("宝塔删除域名响应无法解析，HTTP " + response.code());
                  }

                  List<String> domains = this.readDomainNames(json.get("domains"));
                  if (!domains.isEmpty()) {
                     var16 = domains;
                     break label83;
                  }

                  if (Boolean.FALSE.equals(json.getBoolean("status"))) {
                     throw new CommonException(StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔删除域名失败"));
                  }

                  var16 = domains;
               } catch (Throwable var13) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                     }
                  }

                  throw var13;
               }

               if (response != null) {
                  response.close();
               }

               return var16;
            }

            if (response != null) {
               response.close();
            }

            return var16;
         } catch (CommonException var14) {
            throw var14;
         } catch (Exception var15) {
            log.warn("宝塔删除域名异常: url={}, message={}", url, var15.getMessage());
            throw new CommonException("调用宝塔失败：" + var15.getMessage());
         }
      } else {
         throw new CommonException("网站ID、主域名和要删除的域名不能为空");
      }
   }

   @Override
   public String changeFtpPassword(VhostPasswordCommand command) {
      return this.changeAccountPassword(command, "ftps", "/ftp?action=SetUserPassword", "SetUserPassword", "ftp_username", "new_password");
   }

   @Override
   public String changeDbPassword(VhostPasswordCommand command) {
      return this.changeAccountPassword(command, "databases", "/database?action=ResDatabasePassword", "ResDatabasePassword", "name", "password");
   }

   private String changeAccountPassword(VhostPasswordCommand command, String table, String path, String action, String userField, String passField) {
      if (command == null || StrUtil.isBlank(command.getPanelUrl()) || StrUtil.isBlank(command.getApiKey())) {
         return "主控面板地址或密钥为空";
      } else if (StrUtil.isBlank(command.getUsername())) {
         return "账号尚未开通完成";
      } else {
         String password = StrUtil.trim(command.getPassword());
         if (!StrUtil.isBlank(password) && password.length() >= 6) {
            JSONObject row = this.findAccountByUsername(command, table, command.getUsername());
            if (row == null) {
               return "ftps".equals(table) ? "面板未找到对应FTP账号" : "面板未找到对应数据库账号";
            } else {
               String id = this.text(row, "id");
               String username = StrUtil.blankToDefault(this.text(row, "name", "username"), command.getUsername());
               if (StrUtil.isBlank(id)) {
                  return "面板账号ID为空";
               } else {
                  FormBody body = this.signedForm(command.getApiKey())
                     .add("action", action)
                     .add("id", id)
                     .add(userField, username)
                     .add(passField, password)
                     .build();
                  String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + path;
                  log.info("宝塔修改{}密码: url={}, id={}, username={}", new Object[]{table, url, id, username});

                  try {
                     Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

                     String var21;
                     label101: {
                        label102: {
                           try {
                              String respBody = response.body() == null ? "" : response.body().string();
                              log.info("宝塔修改{}密码响应: httpCode={}, body={}", new Object[]{table, response.code(), this.maskSecrets(respBody)});
                              JSONObject json = this.parseJson(respBody);
                              if (json == null) {
                                 var21 = "宝塔修改密码响应无法解析，HTTP " + response.code();
                                 break label101;
                              }

                              if (this.truthy(this.first(json, "status"))) {
                                 var21 = null;
                                 break label102;
                              }

                              var21 = StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔修改密码失败");
                           } catch (Throwable var18) {
                              if (response != null) {
                                 try {
                                    response.close();
                                 } catch (Throwable var17) {
                                    var18.addSuppressed(var17);
                                 }
                              }

                              throw var18;
                           }

                           if (response != null) {
                              response.close();
                           }

                           return var21;
                        }

                        if (response != null) {
                           response.close();
                        }

                        return var21;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return var21;
                  } catch (Exception var19) {
                     log.warn("宝塔修改{}密码异常: url={}, message={}", new Object[]{table, url, var19.getMessage()});
                     return "调用宝塔失败：" + var19.getMessage();
                  }
               }
            }
         } else {
            return "新密码不能少于6位";
         }
      }
   }

   private JSONObject findAccountByUsername(VhostPasswordCommand command, String table, String username) {
      String dir = this.siteDir(command.getHostName(), command.getDomain());
      JSONObject matched = this.matchAccountByName(this.queryTable(command.getPanelUrl(), command.getApiKey(), table, username), username);
      if (matched != null) {
         return matched;
      } else {
         matched = this.matchAccount(
            this.queryTable(command.getPanelUrl(), command.getApiKey(), table, username), command.getPanelHostId(), command.getDomain(), dir
         );
         if (matched != null) {
            return matched;
         } else {
            matched = this.matchAccount(
               this.queryTable(command.getPanelUrl(), command.getApiKey(), table, dir), command.getPanelHostId(), command.getDomain(), dir
            );
            return matched != null
               ? matched
               : this.matchAccount(this.queryTable(command.getPanelUrl(), command.getApiKey(), table, ""), command.getPanelHostId(), command.getDomain(), dir);
         }
      }
   }

   private JSONObject matchAccountByName(JSONArray rows, String username) {
      if (rows != null && !rows.isEmpty() && !StrUtil.isBlank(username)) {
         for (int i = 0; i < rows.size(); i++) {
            JSONObject item = rows.getJSONObject(i);
            if (item != null
               && (StrUtil.equalsIgnoreCase(this.text(item, "name"), username) || StrUtil.equalsIgnoreCase(this.text(item, "username"), username))) {
               return item;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void fillCreateAccounts(VhostCreateCommand command, String domain) {
      String account = this.panelAccount(command.getHostName(), domain);
      if (StrUtil.isBlank(command.getFtpUser())) {
         command.setFtpUser(account);
      }

      if (StrUtil.isBlank(command.getFtpPassword())) {
         command.setFtpPassword(UniqueHostnameGenerator.generateUniqueHostname(12));
      }

      if (StrUtil.isBlank(command.getDbUser())) {
         command.setDbUser(account);
      }

      if (StrUtil.isBlank(command.getDbPassword())) {
         command.setDbPassword(UniqueHostnameGenerator.generateUniqueHostname(12));
      }
   }

   private void applyCommandAccounts(VhostCreateResult result, VhostCreateCommand command) {
      if (result != null && command != null) {
         if (StrUtil.isBlank(result.getFtpUser())) {
            result.setFtpUser(command.getFtpUser());
            result.setFtpPassword(command.getFtpPassword());
         }

         if (StrUtil.isBlank(result.getDbUser())) {
            result.setDbName(command.getDbUser());
            result.setDbUser(command.getDbUser());
            result.setDbPassword(command.getDbPassword());
         } else if (StrUtil.isBlank(result.getDbName())) {
            result.setDbName(result.getDbUser());
         }
      }
   }

   private String panelAccount(String hostName, String domain) {
      String name = this.siteDir(hostName, domain);
      if (StrUtil.isBlank(name)) {
         name = UniqueHostnameGenerator.generateUniqueHostname(10);
      }

      name = name.replace('.', '_').replace('-', '_');
      if (!name.isEmpty() && Character.isDigit(name.charAt(0))) {
         name = "u" + name;
      }

      return name;
   }

   private String siteDir(String hostName, String domain) {
      String name = StrUtil.trim(hostName);
      if (StrUtil.startWithIgnoreCase(name, "vh_")) {
         String suffix = StrUtil.subSuf(name, 3);
         if (StrUtil.isNotBlank(suffix)) {
            return suffix;
         }
      }

      return StrUtil.blankToDefault(name, domain);
   }

   private String formatAddDomain(String raw) {
      String domain = this.stripPort(raw);
      String port = this.readPort(raw);
      return !StrUtil.isBlank(port) && !"80".equals(port) ? domain + ":" + port : domain;
   }

   private String readPort(String raw) {
      String text = StrUtil.trim(raw);
      if (StrUtil.isBlank(text)) {
         return "80";
      } else {
         int colon = text.lastIndexOf(58);
         return colon > 0 ? text.substring(colon + 1) : "80";
      }
   }

   private String stripPort(String raw) {
      String domain = StrUtil.trim(raw);
      if (StrUtil.isBlank(domain)) {
         return domain;
      } else {
         int colon = domain.lastIndexOf(58);
         return colon > 0 ? domain.substring(0, colon) : domain;
      }
   }

   private List<String> readDomainNames(Object raw) {
      List<String> names = new ArrayList<>();
      if (raw instanceof JSONArray array) {
         for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            String name = null;
            if (item instanceof JSONObject object) {
               name = this.text(object, "name", "domain");
               String port = this.text(object, "port");
               if (StrUtil.isNotBlank(name) && StrUtil.isNotBlank(port) && !"80".equals(port) && !name.contains(":")) {
                  name = name + ":" + port;
               }
            } else if (item != null) {
               name = StrUtil.trim(String.valueOf(item));
            }

            if (StrUtil.isNotBlank(name) && !names.contains(name)) {
               names.add(name);
            }
         }

         return names;
      } else {
         return names;
      }
   }

   private JSONArray parsePhpArray(String respBody) {
      if (StrUtil.isBlank(respBody)) {
         return null;
      } else {
         String text = respBody.trim();

         try {
            if (text.startsWith("[")) {
               return JSON.parseArray(text);
            } else {
               JSONObject json = JSON.parseObject(text);
               if (json == null) {
                  return null;
               } else {
                  Object data = this.first(json, "data", "message");
                  if (data instanceof JSONArray array) {
                     return array;
                  } else {
                     return data instanceof String str && str.trim().startsWith("[") ? JSON.parseArray(str) : null;
                  }
               }
            }
         } catch (Exception var6) {
            return null;
         }
      }
   }

   private JSONObject matchSite(JSONArray sites, String domain, String hostName) {
      if (sites != null && !sites.isEmpty()) {
         String pathSuffix = StrUtil.isBlank(hostName) ? null : this.siteDir(hostName, domain);

         for (int i = 0; i < sites.size(); i++) {
            JSONObject item = sites.getJSONObject(i);
            if (item != null) {
               String name = this.text(item, "name");
               String path = this.text(item, "path");
               String ps = this.text(item, "ps");
               if (StrUtil.isNotBlank(domain) && StrUtil.equalsIgnoreCase(name, domain)) {
                  return item;
               }

               if (StrUtil.isNotBlank(hostName) && (StrUtil.equalsIgnoreCase(ps, hostName) || StrUtil.equalsIgnoreCase(name, hostName))) {
                  return item;
               }

               if (StrUtil.isNotBlank(pathSuffix) && StrUtil.endWith(path, "/" + pathSuffix)) {
                  return item;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void fillAccounts(VhostCreateResult result, VhostCreateCommand command) {
      String siteId = result.getPanelHostId();
      String domain = command.getDomain();
      String dir = this.siteDir(command.getHostName(), domain);
      JSONObject ftp = this.findAccount(command, "ftps", siteId, domain, dir);
      if (ftp != null) {
         result.setFtpUser(this.text(ftp, "name", "username"));
         result.setFtpPassword(this.readPassword(command, "ftps", ftp));
      }

      JSONObject db = this.findAccount(command, "databases", siteId, domain, dir);
      if (db != null) {
         result.setDbName(this.text(db, "name"));
         result.setDbUser(StrUtil.blankToDefault(this.text(db, "username", "name"), result.getDbName()));
         result.setDbPassword(this.readPassword(command, "databases", db));
      }

      log.info(
         "宝塔回写账号: siteId={}, ftpUser={}, dbUser={}, ftpPass={}, dbPass={}",
         new Object[]{siteId, result.getFtpUser(), result.getDbUser(), StrUtil.isNotBlank(result.getFtpPassword()), StrUtil.isNotBlank(result.getDbPassword())}
      );
   }

   private JSONObject findAccount(VhostCreateCommand command, String table, String siteId, String domain, String dir) {
      JSONArray rows = this.queryTable(command.getPanelUrl(), command.getApiKey(), table, StrUtil.blankToDefault(dir, domain));
      JSONObject matched = this.matchAccount(rows, siteId, domain, dir);
      if (matched != null) {
         return matched;
      } else {
         rows = this.queryTable(command.getPanelUrl(), command.getApiKey(), table, domain);
         matched = this.matchAccount(rows, siteId, domain, dir);
         return matched != null ? matched : this.matchAccount(this.queryTable(command.getPanelUrl(), command.getApiKey(), table, ""), siteId, domain, dir);
      }
   }

   private JSONObject matchAccount(JSONArray rows, String siteId, String domain, String dir) {
      if (rows != null && !rows.isEmpty()) {
         String domainKey = StrUtil.blankToDefault(domain, "").replace('.', '_');

         for (int i = 0; i < rows.size(); i++) {
            JSONObject item = rows.getJSONObject(i);
            if (item != null) {
               String pid = this.text(item, "pid");
               String name = this.text(item, "name", "username");
               String path = this.text(item, "path");
               if (StrUtil.isNotBlank(siteId) && StrUtil.equals(pid, siteId)) {
                  return item;
               }

               if (StrUtil.isNotBlank(dir) && StrUtil.containsIgnoreCase(path, dir)) {
                  return item;
               }

               if (StrUtil.isNotBlank(domainKey) && StrUtil.containsIgnoreCase(name, domainKey)) {
                  return item;
               }

               if (StrUtil.isNotBlank(dir) && StrUtil.containsIgnoreCase(name, dir)) {
                  return item;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private String readPassword(VhostCreateCommand command, String table, JSONObject row) {
      String password = this.text(row, "password", "passwd");
      if (StrUtil.isNotBlank(password) && !password.contains("*")) {
         return password;
      } else {
         String id = this.text(row, "id");
         if (StrUtil.isBlank(id)) {
            return password;
         } else {
            FormBody body = this.signedForm(command.getApiKey()).add("action", "getKey").add("table", table).add("id", id).build();
            String url = StrUtil.removeSuffix(command.getPanelUrl().trim(), "/") + "/data?action=getKey";

            try {
               Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

               String trimmed;
               label84: {
                  String var12;
                  label85: {
                     String json;
                     try {
                        String respBody = response.body() == null ? "" : response.body().string();
                        log.info("宝塔读取{}密码: id={}, httpCode={}", new Object[]{table, id, response.code()});
                        if (StrUtil.isBlank(respBody)) {
                           trimmed = password;
                           break label84;
                        }

                        trimmed = respBody.trim();
                        if (trimmed.startsWith("{")) {
                           JSONObject jsonx = this.parseJson(trimmed);
                           var12 = jsonx == null ? password : StrUtil.blankToDefault(this.text(jsonx, "msg", "data", "password"), password);
                           break label85;
                        }

                        json = StrUtil.removePrefix(StrUtil.removeSuffix(trimmed, "\""), "\"");
                     } catch (Throwable var14) {
                        if (response != null) {
                           try {
                              response.close();
                           } catch (Throwable var13) {
                              var14.addSuppressed(var13);
                           }
                        }

                        throw var14;
                     }

                     if (response != null) {
                        response.close();
                     }

                     return json;
                  }

                  if (response != null) {
                     response.close();
                  }

                  return var12;
               }

               if (response != null) {
                  response.close();
               }

               return trimmed;
            } catch (Exception var15) {
               log.warn("宝塔读取{}密码失败: id={}, message={}", new Object[]{table, id, var15.getMessage()});
               return password;
            }
         }
      }
   }

   private JSONArray queryTable(String panelUrl, String apiKey, String table, String search) {
      FormBody body = this.signedForm(apiKey)
         .add("action", "getData")
         .add("table", table)
         .add("limit", "20")
         .add("p", "1")
         .add("search", StrUtil.blankToDefault(search, ""))
         .add("type", "-1")
         .build();
      String url = StrUtil.removeSuffix(panelUrl.trim(), "/") + "/data?action=getData";

      try {
         Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

         Object var15;
         label59: {
            JSONArray var16;
            try {
               String respBody = response.body() == null ? "" : response.body().string();
               log.info("宝塔查询{}: url={}, search={}, httpCode={}", new Object[]{table, url, search, response.code()});
               JSONObject json = this.parseJson(respBody);
               if (json == null) {
                  var15 = null;
                  break label59;
               }

               var16 = json.get("data") instanceof JSONArray array ? array : null;
            } catch (Throwable var13) {
               if (response != null) {
                  try {
                     response.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (response != null) {
               response.close();
            }

            return var16;
         }

         if (response != null) {
            response.close();
         }

         return (JSONArray)var15;
      } catch (Exception var14) {
         log.warn("宝塔查询{}异常: url={}, message={}", new Object[]{table, url, var14.getMessage()});
         return null;
      }
   }

   private JSONObject postSystem(String panelUrl, String apiKey, String action, String label) {
      if (!StrUtil.isBlank(panelUrl) && !StrUtil.isBlank(apiKey)) {
         FormBody body = this.signedForm(apiKey).add("action", action).build();
         String url = StrUtil.removeSuffix(panelUrl.trim(), "/") + "/system?action=" + action;
         log.info("宝塔获取{}: url={}", label, url);

         try {
            Response response = this.httpClient.newCall(new Builder().url(url).post(body).build()).execute();

            JSONObject var11;
            try {
               String respBody = response.body() == null ? "" : response.body().string();
               log.info("宝塔获取{}响应: httpCode={}, body={}", new Object[]{label, response.code(), respBody});
               JSONObject json = this.parseJson(respBody);
               if (json == null) {
                  throw new CommonException("宝塔获取" + label + "响应无法解析");
               }

               if (json.containsKey("status") && !this.truthy(this.first(json, "status"))) {
                  throw new CommonException(StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "宝塔获取" + label + "失败"));
               }

               JSONObject data = json.getJSONObject("data");
               var11 = data == null || !data.containsKey("system") && !data.containsKey("cpu") && !data.containsKey("downTotal") ? json : data;
            } catch (Throwable var13) {
               if (response != null) {
                  try {
                     response.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (response != null) {
               response.close();
            }

            return var11;
         } catch (CommonException var14) {
            throw var14;
         } catch (Exception var15) {
            log.warn("宝塔获取{}异常: url={}, message={}", new Object[]{label, url, var15.getMessage()});
            throw new CommonException("调用宝塔失败：" + var15.getMessage());
         }
      } else {
         throw new CommonException("主控面板地址或密钥为空");
      }
   }

   private Integer intValue(JSONObject json, String key) {
      Long value = this.longValue(json, key);
      return value == null ? null : value.intValue();
   }

   private Long longValue(JSONObject json, String key) {
      if (json != null && json.containsKey(key) && json.get(key) != null) {
         Object value = json.get(key);
         if (value instanceof Number number) {
            return number.longValue();
         } else {
            String text = StrUtil.trim(String.valueOf(value));
            if (StrUtil.isBlank(text)) {
               return null;
            } else {
               try {
                  return new BigDecimal(text).longValue();
               } catch (Exception var6) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   private BigDecimal decimalValue(JSONObject json, String key) {
      if (json != null && json.containsKey(key) && json.get(key) != null) {
         Object value = json.get(key);
         if (value instanceof BigDecimal decimal) {
            return decimal;
         } else if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
         } else {
            String text = StrUtil.trim(String.valueOf(value));
            if (StrUtil.isBlank(text)) {
               return null;
            } else {
               try {
                  return new BigDecimal(text);
               } catch (Exception var6) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   private List<BigDecimal> toDecimalList(Object value) {
      if (value instanceof JSONArray array && !array.isEmpty()) {
         List<BigDecimal> result = new ArrayList<>();

         for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            if (item instanceof BigDecimal decimal) {
               result.add(decimal);
            } else if (item instanceof Number number) {
               result.add(BigDecimal.valueOf(number.doubleValue()));
            } else if (item != null && StrUtil.isNotBlank(String.valueOf(item))) {
               try {
                  result.add(new BigDecimal(String.valueOf(item).trim()));
               } catch (Exception var9) {
                  result.add(null);
               }
            } else {
               result.add(null);
            }
         }

         return result;
      } else {
         return null;
      }
   }

   private okhttp3.FormBody.Builder signedForm(String apiKey) {
      String requestTime = String.valueOf(System.currentTimeMillis() / 1000L);
      String requestToken = SecureUtil.md5(requestTime + SecureUtil.md5(apiKey));
      return new okhttp3.FormBody.Builder().add("request_time", requestTime).add("request_token", requestToken);
   }

   private JSONObject parseJson(String respBody) {
      if (StrUtil.isBlank(respBody)) {
         return null;
      } else {
         try {
            return JSON.parseObject(respBody);
         } catch (Exception var3) {
            return null;
         }
      }
   }

   private String formatDate(Date endTime) {
      return new SimpleDateFormat("yyyy-MM-dd").format(endTime);
   }

   private VhostCreateResult parse(String domain, int httpCode, String respBody) {
      JSONObject json = null;
      if (StrUtil.isNotBlank(respBody)) {
         try {
            json = JSON.parseObject(respBody);
         } catch (Exception var10) {
            json = null;
         }
      }

      if (json == null) {
         return VhostCreateResult.fail("宝塔响应无法解析，HTTP " + httpCode);
      } else {
         VhostCreateResult result = new VhostCreateResult();
         result.setDomain(domain);
         result.setPanelHostId(this.text(json, "siteId", "site_id"));
         result.setFtpUser(this.text(json, "ftpUser", "ftp_user"));
         result.setFtpPassword(this.text(json, "ftpPass", "ftpPassword", "ftp_pass"));
         result.setDbUser(this.text(json, "databaseUser", "database_user", "dbUser"));
         result.setDbPassword(this.text(json, "databasePass", "databasePassword", "database_pass", "dbPass"));
         result.setDbName(StrUtil.blankToDefault(this.text(json, "databaseName", "database_name", "dbName"), result.getDbUser()));
         boolean siteOk = this.truthy(this.first(json, "siteStatus", "site_status")) || StrUtil.isNotBlank(result.getPanelHostId());
         boolean ftpOk = this.truthy(this.first(json, "ftpStatus", "ftp_status"))
            && StrUtil.isNotBlank(result.getFtpUser())
            && StrUtil.isNotBlank(result.getFtpPassword());
         boolean dbOk = this.truthy(this.first(json, "databaseStatus", "database_status"))
            && StrUtil.isNotBlank(result.getDbUser())
            && StrUtil.isNotBlank(result.getDbPassword());
         String message = StrUtil.blankToDefault(this.text(json, "msg", "message", "error"), "");
         if (siteOk && ftpOk && dbOk) {
            result.setSuccess(true);
            result.setMessage(StrUtil.blankToDefault(message, "创建成功"));
            return result;
         } else {
            result.setSuccess(false);
            if (siteOk && (!ftpOk || !dbOk)) {
               result.setMessage("网站已创建，但未返回完整的FTP或数据库信息" + (StrUtil.isBlank(message) ? "" : "：" + message));
               return result;
            } else {
               result.setMessage(StrUtil.blankToDefault(message, "宝塔创建网站失败"));
               return result;
            }
         }
      }
   }

   private BaotaVhostPanelAdapter.PhpSpec resolvePhp(String phpVersion) {
      String raw = StrUtil.trim(phpVersion);
      if (!StrUtil.isBlank(raw) && !raw.contains("静态") && !"00".equals(raw)) {
         String version;
         if (raw.contains(".")) {
            String[] parts = raw.split("\\.");
            String major = parts[0].replaceAll("\\D", "");
            String minor = parts.length > 1 ? parts[1].replaceAll("\\D", "") : "0";
            if (minor.length() > 1) {
               minor = minor.substring(0, 1);
            }

            version = major + (StrUtil.isBlank(minor) ? "0" : minor);
         } else {
            String digits = raw.replaceAll("\\D", "");
            if (digits.length() >= 2) {
               version = digits.substring(0, 2);
            } else {
               if (digits.length() != 1) {
                  return new BaotaVhostPanelAdapter.PhpSpec("", "00");
               }

               version = digits + "0";
            }
         }

         return !StrUtil.isBlank(version) && !"00".equals(version)
            ? new BaotaVhostPanelAdapter.PhpSpec("PHP", version)
            : new BaotaVhostPanelAdapter.PhpSpec("", "00");
      } else {
         return new BaotaVhostPanelAdapter.PhpSpec("", "00");
      }
   }

   private String text(JSONObject json, String... keys) {
      Object value = this.first(json, keys);
      return value == null ? null : StrUtil.trim(String.valueOf(value));
   }

   private Object first(JSONObject json, String... keys) {
      for (String key : keys) {
         if (json.containsKey(key) && json.get(key) != null && StrUtil.isNotBlank(String.valueOf(json.get(key)))) {
            return json.get(key);
         }
      }

      return null;
   }

   private boolean truthy(Object value) {
      if (value instanceof Boolean bool) {
         return bool;
      } else if (value instanceof Number number) {
         return number.intValue() == 1;
      } else if (value == null) {
         return false;
      } else {
         String text = String.valueOf(value);
         return "true".equalsIgnoreCase(text) || "1".equals(text);
      }
   }

   private String maskSecrets(String body) {
      return StrUtil.isBlank(body)
         ? body
         : body.replaceAll("(?i)\"(ftpPass|ftpPassword|ftp_pass|databasePass|databasePassword|database_pass|dbPass)\"\\s*:\\s*\"[^\"]*\"", "\"$1\":\"***\"");
   }

   private OkHttpClient buildClient() {
      try {
         TrustManager[] trustAll = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
               return new X509Certificate[0];
            }
         }};
         SSLContext sslContext = SSLContext.getInstance("TLS");
         sslContext.init(null, trustAll, new SecureRandom());
         return new okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAll[0])
            .hostnameVerifier((hostname, session) -> true)
            .connectTimeout(15L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .build();
      } catch (Exception var3) {
         throw new IllegalStateException("初始化宝塔客户端失败", var3);
      }
   }

   private record PhpSpec(String type, String version) {
   }
}

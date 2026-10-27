package com.rxinyun.common.monitor.ai;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MonitorAiDiagnosisService {
   private static final Logger log = LoggerFactory.getLogger(MonitorAiDiagnosisService.class);
   private static final String DASHSCOPE_CHAT_COMPLETIONS_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
   private static final String DEFAULT_OPENAI_BASE_URL = "https://api.openai.com/v1";
   private static final String PROVIDER_ALIYUN_DASHSCOPE = "ALIYUN_DASHSCOPE";
   private static final String PROVIDER_OPENAI = "OPENAI";
   private static final String DEFAULT_DASHSCOPE_MODEL = "qwen-plus";
   private static final String DEFAULT_OPENAI_MODEL = "gpt-4o-mini";
   private static final int REQUEST_TIMEOUT = 30000;
   private static final int MAX_MONITOR_DATA_LENGTH = 20000;
   @Resource
   private JdbcTemplate jdbcTemplate;

   public CommonResult<MonitorAiDiagnosisResult> diagnose(Object requestBody) {
      if (ObjectUtil.isEmpty(requestBody)) {
         return CommonResult.error("监控数据不能为空");
      } else {
         CommonResult<MonitorAiDiagnosisService.AiProviderSettings> providerResult = this.resolveProvider("诊断");
         if (providerResult.getData() == null) {
            return CommonResult.error(providerResult.getMsg());
         } else {
            String monitorData = this.buildMonitorDataText(requestBody);
            if (StrUtil.isBlank(monitorData)) {
               return CommonResult.error("监控数据不能为空");
            } else {
               try {
                  MonitorAiDiagnosisService.AiProviderSettings settings = providerResult.getData();
                  JSONArray messages = new JSONArray();
                  messages.add(this.buildMessage("system", "你是云服务器资源监控诊断助手。请根据CPU、内存、磁盘、网络流量、IO、时间序列等监控数据判断风险，输出简洁、可执行的中文诊断结果。只返回合法JSON，不要Markdown。"));
                  messages.add(this.buildMessage("user", this.buildUserPrompt(monitorData)));
                  String content = this.callChatCompletions(settings, messages, 0.2, true, "诊断");
                  MonitorAiDiagnosisResult result = this.parseAiResult(content);
                  return CommonResult.data(result);
               } catch (Exception var8) {
                  log.error("云服务器监控AI诊断失败", var8);
                  return CommonResult.error("AI诊断失败：" + var8.getMessage());
               }
            }
         }
      }
   }

   public CommonResult<MonitorAiChatResult> chat(Object requestBody) {
      if (ObjectUtil.isEmpty(requestBody)) {
         return CommonResult.error("对话内容不能为空");
      } else {
         CommonResult<MonitorAiDiagnosisService.AiProviderSettings> providerResult = this.resolveProvider("对话");
         if (providerResult.getData() == null) {
            return CommonResult.error(providerResult.getMsg());
         } else {
            String message = this.buildChatMessageText(requestBody);
            if (StrUtil.isBlank(message)) {
               return CommonResult.error("对话内容不能为空");
            } else {
               try {
                  MonitorAiDiagnosisService.AiProviderSettings settings = providerResult.getData();
                  JSONArray messages = new JSONArray();
                  messages.add(this.buildMessage("system", "你是云服务器运维助手，回答资源监控、性能排查、异常分析和处理建议相关问题。请用简洁中文回答。"));
                  messages.add(this.buildMessage("user", message));
                  String content = this.callChatCompletions(settings, messages, 0.4, false, "对话");
                  MonitorAiChatResult result = new MonitorAiChatResult();
                  result.setAnswer(content);
                  result.setGeneratedAt(DateUtil.now());
                  return CommonResult.data(result);
               } catch (Exception var8) {
                  log.error("云服务器监控AI对话失败", var8);
                  return CommonResult.error("AI对话失败：" + var8.getMessage());
               }
            }
         }
      }
   }

   private CommonResult<MonitorAiDiagnosisService.AiProviderSettings> resolveProvider(String scene) {
      String provider = StrUtil.blankToDefault(this.readDevConfig("RUNXIN_MONITOR_AI_PROVIDER"), "ALIYUN_DASHSCOPE");
      MonitorAiDiagnosisService.AiProviderSettings settings = new MonitorAiDiagnosisService.AiProviderSettings();
      if ("OPENAI".equalsIgnoreCase(provider)) {
         settings.apiKey = this.readDevConfig("OPENAI_API_KEY");
         if (StrUtil.isBlank(settings.apiKey)) {
            return CommonResult.error("AI" + scene + "未配置：请设置 OPENAI_API_KEY");
         } else {
            settings.model = StrUtil.blankToDefault(this.readDevConfig("OPENAI_MODEL"), "gpt-4o-mini");
            settings.chatCompletionsUrl = this.buildOpenAiChatCompletionsUrl(this.readDevConfig("OPENAI_BASE_URL"));
            return CommonResult.data(settings);
         }
      } else if ("ALIYUN_DASHSCOPE".equalsIgnoreCase(provider)) {
         settings.apiKey = this.readDevConfig("DASHSCOPE_API_KEY");
         if (StrUtil.isBlank(settings.apiKey)) {
            return CommonResult.error("AI" + scene + "未配置：请设置 DASHSCOPE_API_KEY");
         } else {
            settings.model = StrUtil.blankToDefault(this.readDevConfig("DASHSCOPE_MODEL"), "qwen-plus");
            settings.chatCompletionsUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
            return CommonResult.data(settings);
         }
      } else {
         return CommonResult.error("暂不支持的AI" + scene + "供应商：" + provider);
      }
   }

   private String buildOpenAiChatCompletionsUrl(String baseUrl) {
      String url = StrUtil.blankToDefault(baseUrl, "https://api.openai.com/v1").trim();

      while (url.endsWith("/")) {
         url = url.substring(0, url.length() - 1);
      }

      return StrUtil.endWithIgnoreCase(url, "/chat/completions") ? url : url + "/chat/completions";
   }

   private String readDevConfig(String configKey) {
      try {
         String value = (String)this.jdbcTemplate
            .queryForObject(
               "SELECT CONFIG_VALUE FROM dev_config WHERE CONFIG_KEY = ? AND CATEGORY <> 'SYS_BASE' AND DELETE_FLAG = 'NOT_DELETE' LIMIT 1",
               String.class,
               new Object[]{configKey}
            );
         return StrUtil.nullToEmpty(value).trim();
      } catch (EmptyResultDataAccessException var3) {
         return null;
      } catch (Exception var4) {
         log.warn("读取AI诊断私有配置失败，configKey={}", configKey, var4);
         return null;
      }
   }

   private String buildMonitorDataText(Object requestBody) {
      Object monitorData = requestBody;
      if (requestBody instanceof Map<?, ?> map && map.containsKey("monitorData") && ObjectUtil.isNotEmpty(map.get("monitorData"))) {
         monitorData = map.get("monitorData");
      }

      String text = JSON.toJSONString(monitorData);
      if (text.length() > 20000) {
         text = text.substring(0, 20000) + "...";
      }

      return text;
   }

   private String buildChatMessageText(Object requestBody) {
      Object message = requestBody;
      if (requestBody instanceof Map<?, ?> map) {
         if (ObjectUtil.isNotEmpty(map.get("message"))) {
            message = map.get("message");
         } else if (ObjectUtil.isNotEmpty(map.get("question"))) {
            message = map.get("question");
         } else if (ObjectUtil.isNotEmpty(map.get("content"))) {
            message = map.get("content");
         }
      }

      String text = message instanceof String ? Convert.toStr(message) : JSON.toJSONString(message);
      if (text.length() > 20000) {
         text = text.substring(0, 20000) + "...";
      }

      return text;
   }

   private String callChatCompletions(
      MonitorAiDiagnosisService.AiProviderSettings settings, JSONArray messages, double temperature, boolean jsonObject, String scene
   ) {
      JSONObject requestJson = new JSONObject(true);
      requestJson.put("model", settings.model);
      requestJson.put("temperature", temperature);
      requestJson.put("messages", messages);
      if (jsonObject) {
         JSONObject responseFormat = new JSONObject(true);
         responseFormat.put("type", "json_object");
         requestJson.put("response_format", responseFormat);
      }

      HttpResponse response = ((HttpRequest)HttpRequest.post(settings.chatCompletionsUrl).header(Header.AUTHORIZATION, "Bearer " + settings.apiKey))
         .contentType(ContentType.JSON.getValue())
         .body(requestJson.toJSONString())
         .timeout(30000)
         .execute();

      String var15;
      try {
         String responseBody = response.body();
         if (!response.isOk()) {
            throw new IllegalStateException("AI" + scene + "请求失败，HTTP状态码：" + response.getStatus());
         }

         JSONObject responseJson = JSON.parseObject(responseBody);
         JSONObject error = responseJson.getJSONObject("error");
         if (error != null) {
            throw new IllegalStateException(StrUtil.blankToDefault(error.getString("message"), "AI" + scene + "返回错误"));
         }

         JSONArray choices = responseJson.getJSONArray("choices");
         if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("AI未返回" + scene + "内容");
         }

         JSONObject message = choices.getJSONObject(0).getJSONObject("message");
         String content = message == null ? null : message.getString("content");
         if (StrUtil.isBlank(content)) {
            throw new IllegalStateException("AI返回内容为空");
         }

         var15 = content;
      } catch (Throwable var17) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var16) {
               var17.addSuppressed(var16);
            }
         }

         throw var17;
      }

      if (response != null) {
         response.close();
      }

      return var15;
   }

   private JSONObject buildMessage(String role, String content) {
      JSONObject message = new JSONObject(true);
      message.put("role", role);
      message.put("content", content);
      return message;
   }

   private String buildUserPrompt(String monitorData) {
      return "请分析下面的云服务器监控数据，返回JSON结构：{\"level\":\"normal|warning|critical\",\"summary\":\"一句话摘要\",\"anomalies\":[\"异常点\"],\"suggestions\":[\"处理建议\"]}。如果没有明显异常，level填normal，anomalies可以为空数组。监控数据：\n"
         + monitorData;
   }

   private MonitorAiDiagnosisResult parseAiResult(String content) {
      JSONObject jsonObject = JSON.parseObject(this.extractJsonObject(content));
      MonitorAiDiagnosisResult result = new MonitorAiDiagnosisResult();
      result.setLevel(StrUtil.blankToDefault(jsonObject.getString("level"), "warning"));
      result.setSummary(StrUtil.blankToDefault(jsonObject.getString("summary"), content));
      result.setAnomalies(this.readStringList(jsonObject, "anomalies"));
      result.setSuggestions(this.readStringList(jsonObject, "suggestions"));
      result.setGeneratedAt(DateUtil.now());
      return result;
   }

   private List<String> readStringList(JSONObject jsonObject, String key) {
      List<String> values = new ArrayList<>();
      JSONArray array = jsonObject.getJSONArray(key);
      if (array == null) {
         return values;
      } else {
         for (Object item : array) {
            values.add(Convert.toStr(item));
         }

         return values;
      }
   }

   private String extractJsonObject(String content) {
      String trimmedContent = StrUtil.trim(content);
      if (StrUtil.startWith(trimmedContent, "{") && StrUtil.endWith(trimmedContent, "}")) {
         return trimmedContent;
      } else {
         int startIndex = trimmedContent.indexOf(123);
         int endIndex = trimmedContent.lastIndexOf(125);
         if (startIndex >= 0 && endIndex > startIndex) {
            return trimmedContent.substring(startIndex, endIndex + 1);
         } else {
            throw new IllegalStateException("AI返回内容不是合法JSON");
         }
      }
   }

   private static class AiProviderSettings {
      private String apiKey;
      private String model;
      private String chatCompletionsUrl;
   }
}

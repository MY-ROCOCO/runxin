package com.rxinyun.core.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rxinyun.common.annotation.CommonNoRepeat;
import com.rxinyun.common.annotation.CommonWrapper;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonDeleteFlagEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.listener.CommonDataChangeEventCenter;
import com.rxinyun.common.listener.CommonDataChangeListener;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.pojo.CommonWrapperInterface;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonJoinPointUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.CommonTimeFormatUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.core.handler.GlobalExceptionUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.core.util.StpClientUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@MapperScan(
   basePackages = {"com.rxinyun.**.mapper"}
)
public class GlobalConfigure implements WebMvcConfigurer {
   private static final Logger log = LoggerFactory.getLogger(GlobalConfigure.class);
   private static final String COMMON_REPEAT_SUBMIT_CACHE_KEY = "common-repeatSubmit:";
   public static final String[] NO_LOGIN_PATH_ARR = new String[]{
      "/",
      "/admin/dev/update/status",
      "/favicon.ico",
      "/doc.html",
      "/webjars/**",
      "/v3/api-docs/**",
      "/druid/**",
      "/mobile/**",
      "/member/tools/config/sysBaseList",
      "/member/auth/c/getPicCaptcha",
      "/member/auth/c/getPhoneValidCode",
      "/member/auth/c/doLogin",
      "/member/auth/c/doLoginByPhone",
      "/member/auth/switch-login",
      "/member/auth/c/register",
      "/member/auth/c/getRegisterEmailValidCode",
      "/member/auth/c/registerByEmail",
      "/member/auth/c/getResetPasswordEmailValidCode",
      "/member/auth/c/resetPasswordByEmail",
      "/member/pay/notify",
      "/admin/auth/b/getPicCaptcha",
      "/admin/auth/b/getPhoneValidCode",
      "/admin/auth/b/doLogin",
      "/admin/auth/b/doLoginByPhone",
      "/auth/third/render",
      "/auth/third/callback",
      "/admin/dev/config/sysBaseList",
      "/admin/dev/dict/tree",
      "/admin/dev/file/download",
      "/member/dev/file/download",
      "/api/callback",
      "/api/dns/callback",
      "/api/order/**",
      "/api/product/**",
      "/dev/telegram/webhook",
      "/admin/sys/userCenter/getPicCaptcha",
      "/admin/sys/userCenter/findPasswordGetPhoneValidCode",
      "/admin/sys/userCenter/findPasswordGetEmailValidCode",
      "/admin/sys/userCenter/findPasswordByPhone",
      "/admin/sys/userCenter/findPasswordByEmail",
      "/member/server/calculate",
      "/member/vhost/calculate",
      "/member/marketing/coupon/claimable-list"
   };
   private static final String[] SUPER_PERMISSION_PATH_ARR = new String[]{
      "/auth/session/**",
      "/auth/third/page",
      "/client/user/**",
      "/sys/org/**",
      "/sys/position/**",
      "/sys/button/**",
      "/sys/menu/**",
      "/sys/module/**",
      "/sys/role/**",
      "/sys/user/**",
      "/sys/index/bizDataCount",
      "/sys/index/opDataCount",
      "/sys/index/toolDataCount",
      "/dev/config/**",
      "/dev/dict/**",
      "/dev/email/page",
      "/dev/email/delete",
      "/dev/email/detail",
      "/dev/file/page",
      "/dev/file/list",
      "/dev/file/delete",
      "/dev/file/detail",
      "/dev/job/**",
      "/dev/log/**",
      "/dev/message/page",
      "/dev/message/delete",
      "/dev/message/detail",
      "/dev/monitor/**",
      "/dev/sms/page",
      "/dev/sms/delete",
      "/dev/sms/detail",
      "/dev/slideshow/**",
      "/mobile/menu/**",
      "/mobile/module/**"
   };
   private static final String[] CLIENT_USER_PERMISSION_PATH_ARR = new String[]{"/member/auth/client/**", "/client/c/**"};

   public GlobalConfigure() {
   }

   @Bean
   public SaServletFilter getSaServletFilter() {
      return new SaServletFilter()
         .addInclude(new String[]{"/**"})
         .setAuth(
            r -> {
               SaRouter.match(new String[]{"/admin/**"})
                  .notMatch(CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR))
                  .notMatch(CollectionUtil.newArrayList(CLIENT_USER_PERMISSION_PATH_ARR))
                  .check(r1 -> StpUtil.checkLogin());
               SaRouter.match(new String[]{"/member/**"})
                  .notMatch(CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR))
                  .match(CollectionUtil.newArrayList(CLIENT_USER_PERMISSION_PATH_ARR))
                  .check(r1 -> StpClientUtil.checkLogin());
            }
         )
         .setBeforeAuth(
            obj -> {
               SaHolder.getResponse()
                  .setHeader("X-XSS-Protection", "1; mode=block")
                  .setHeader("X-Content-Type-Options", "nosniff")
                  .setHeader("Access-Control-Allow-Origin", "*")
                  .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                  .setHeader("Access-Control-Max-Age", "3600")
                  .setHeader("Access-Control-Allow-Headers", "*");
               SaRouter.match(new SaHttpMethod[]{SaHttpMethod.OPTIONS}).free(r -> {}).back();
            }
         )
         .setError(e -> {
            log.error("授权验证失败", e);
            SaResponse saResponse = SaHolder.getResponse();
            saResponse.setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON + ";charset=UTF-8");
            if (e instanceof CommonException ce) {
               return JSONUtil.toJsonStr(new CommonResult().setCode(ce.getCode()).setMsg(ce.getMessage()));
            } else {
               CommonResult<String> result = GlobalExceptionUtil.getCommonResult((Exception)e);
               return JSONUtil.toJsonStr(result);
            }
         });
   }

   @Primary
   @Bean
   public RedisTemplate<String, Object> redisTemplate(@Autowired(required = false) RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
      RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
      redisTemplate.setKeySerializer(stringRedisSerializer);
      redisTemplate.setHashKeySerializer(stringRedisSerializer);
      Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(objectMapper, Object.class);
      redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.afterPropertiesSet();
      return redisTemplate;
   }

   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler(new String[]{"doc.html"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/"});
      registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"});
   }

   @Bean
   public MybatisPlusInterceptor mybatisPlusInterceptor() {
      MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
      mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
      return mybatisPlusInterceptor;
   }

   @Resource
   public void registerListenerList(List<CommonDataChangeListener> dataChangeListenerList) {
      CommonDataChangeEventCenter.registerListenerList(dataChangeListenerList);
   }

   @Component
   @Aspect
   public static class CommonNoRepeatAop {
      public CommonNoRepeatAop() {
      }

      @Pointcut("@annotation(com.rxinyun.common.annotation.CommonNoRepeat)")
      private void noRepeatPointcut() {
      }

      @Before("noRepeatPointcut()")
      public void doBefore(JoinPoint joinPoint) {
         MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
         Method method = methodSignature.getMethod();
         CommonNoRepeat commonNoRepeat = method.getAnnotation(CommonNoRepeat.class);
         HttpServletRequest request = CommonServletUtil.getRequest();
         String url = request.getRequestURI();
         CommonCacheOperator commonCacheOperator = (CommonCacheOperator)SpringUtil.getBean(CommonCacheOperator.class);
         JSONObject jsonObject = JSONUtil.createObj();
         jsonObject.set("repeatParam", CommonJoinPointUtil.getArgsJsonString(joinPoint));
         jsonObject.set("repeatTime", DateUtil.current());
         String cacheKey = "common-repeatSubmit:" + CommonIpAddressUtil.getIp(request) + ":";
         Object loginId = StpUtil.getLoginIdDefaultNull();
         if (ObjectUtil.isNotEmpty(loginId)) {
            cacheKey = cacheKey + Convert.toStr(loginId) + ":" + url;
         } else {
            cacheKey = cacheKey + url;
         }

         Object cacheObj = commonCacheOperator.get(cacheKey);
         if (ObjectUtil.isNotEmpty(cacheObj)) {
            JSONObject cacheJsonObject = JSONUtil.parseObj(cacheObj);
            if (cacheJsonObject.containsKey(url)) {
               JSONObject existRepeatJsonObject = cacheJsonObject.getJSONObject(url);
               if (jsonObject.getStr("repeatParam").equals(existRepeatJsonObject.getStr("repeatParam"))) {
                  long interval = jsonObject.getLong("repeatTime") - existRepeatJsonObject.getLong("repeatTime");
                  if (interval < commonNoRepeat.interval()) {
                     long secondsParam = (commonNoRepeat.interval() - interval) / 1000L;
                     if (secondsParam > 0L) {
                        throw new CommonException("请求过于频繁，请" + CommonTimeFormatUtil.formatSeconds(secondsParam) + "后再试", new Object[0]);
                     }
                  }
               }
            }
         }

         commonCacheOperator.put(cacheKey, JSONUtil.createObj().set(url, jsonObject), 3600L);
      }
   }

   @Component
   @Aspect
   public static class CommonWrapperAop {
      public CommonWrapperAop() {
      }

      @Pointcut("@annotation(com.rxinyun.common.annotation.CommonWrapper)")
      private void wrapperPointcut() {
      }

      @Around("wrapperPointcut()")
      public Object doWrapper(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
         Object proceedResult = proceedingJoinPoint.proceed();
         return this.processWrapping(proceedingJoinPoint, proceedResult);
      }

      private Object processWrapping(ProceedingJoinPoint proceedingJoinPoint, Object originResult) throws IllegalAccessException, InstantiationException {
         MethodSignature methodSignature = (MethodSignature)proceedingJoinPoint.getSignature();
         Method method = methodSignature.getMethod();
         CommonWrapper commonWrapper = method.getAnnotation(CommonWrapper.class);
         Class<? extends CommonWrapperInterface<?>>[] baseWrapperClasses = commonWrapper.value();
         if (ObjectUtil.isEmpty(baseWrapperClasses)) {
            return originResult;
         } else if (!(originResult instanceof CommonResult commonResult)) {
            return originResult;
         } else {
            Object beWrapped = commonResult.getData();
            if (ObjectUtil.isBasicType(beWrapped)) {
               throw new CommonException("被包装的值不能是基本类型", new Object[0]);
            } else {
               if (beWrapped instanceof Page page) {
                  ArrayList<Map<String, Object>> maps = new ArrayList<>();

                  for (Object wrappedItem : page.getRecords()) {
                     maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                  }

                  page.setRecords(maps);
                  commonResult.setData(page);
               } else if (beWrapped instanceof Collection collection) {
                  List<Map<String, Object>> maps = new ArrayList<>();

                  for (Object wrappedItem : collection) {
                     maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                  }

                  commonResult.setData(maps);
               } else if (ArrayUtil.isArray(beWrapped)) {
                  Object[] objects = this.objToArray(beWrapped);
                  ArrayList<Map<String, Object>> maps = new ArrayList<>();

                  for (Object wrappedItem : objects) {
                     maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                  }

                  commonResult.setData(maps);
               } else {
                  commonResult.setData(this.wrapPureObject(beWrapped, baseWrapperClasses));
               }

               return commonResult;
            }
         }
      }

      private JSONObject wrapPureObject(Object originModel, Class<? extends CommonWrapperInterface<?>>[] baseWrapperClasses) {
         JSONObject jsonObject = JSONUtil.parseObj(originModel);

         try {
            for (Class<? extends CommonWrapperInterface<?>> commonWrapperClass : baseWrapperClasses) {
               CommonWrapperInterface commonWrapperInterface = commonWrapperClass.newInstance();
               Map<String, Object> incrementFieldsMap = commonWrapperInterface.doWrap(originModel);
               jsonObject.putAll(incrementFieldsMap);
            }

            return jsonObject;
         } catch (Exception var10) {
            throw new CommonException("原始对象包装过程，字段转化异常：{}", new Object[]{var10.getMessage()});
         }
      }

      private Object[] objToArray(Object object) {
         int length = Array.getLength(object);
         Object[] result = new Object[length];

         for (int i = 0; i < result.length; i++) {
            result[i] = Array.get(object, i);
         }

         return result;
      }
   }

   @Component
   public static class CustomDbIdProvider implements DatabaseIdProvider {
      public CustomDbIdProvider() {
      }

      public String getDatabaseId(DataSource dataSource) throws SQLException {
         Connection conn = null;

         String var4;
         try {
            conn = dataSource.getConnection();
            String url = conn.getMetaData().getURL().toLowerCase();
            if (url.contains("jdbc:oracle")) {
               return "oracle";
            }

            if (url.contains("jdbc:postgresql")) {
               return "pgsql";
            }

            if (url.contains("jdbc:mysql")) {
               return "mysql";
            }

            if (url.contains("jdbc:dm")) {
               return "dm";
            }

            if (!url.contains("jdbc:kingbase")) {
               return "mysql";
            }

            var4 = "kingbase";
         } finally {
            JdbcUtils.closeConnection(conn);
         }

         return var4;
      }
   }

   @Component
   public static class CustomMetaObjectHandler implements MetaObjectHandler {
      private static final String DELETE_FLAG = "deleteFlag";
      private static final String CREATE_USER = "createUser";
      private static final String CREATE_TIME = "createTime";
      private static final String UPDATE_USER = "updateUser";
      private static final String UPDATE_TIME = "updateTime";

      public CustomMetaObjectHandler() {
      }

      public void insertFill(MetaObject metaObject) {
         try {
            Object deleteFlag = metaObject.getValue("deleteFlag");
            if (ObjectUtil.isNull(deleteFlag)) {
               this.setFieldValByName("deleteFlag", EnumUtil.toString(CommonDeleteFlagEnum.NOT_DELETE), metaObject);
            }
         } catch (ReflectionException var5) {
         }

         try {
            Object createUser = metaObject.getValue("createUser");
            if (ObjectUtil.isNull(createUser)) {
               this.setFieldValByName("createUser", this.getUserId(), metaObject);
            }
         } catch (ReflectionException var4) {
         }

         try {
            Object createTime = metaObject.getValue("createTime");
            if (ObjectUtil.isNull(createTime)) {
               this.setFieldValByName("createTime", TimeUtil.toDate(TimeUtil.now()), metaObject);
            }
         } catch (ReflectionException var3) {
         }
      }

      public void updateFill(MetaObject metaObject) {
         try {
            this.setFieldValByName("updateUser", this.getUserId(), metaObject);
         } catch (ReflectionException var4) {
         }

         try {
            this.setFieldValByName("updateTime", TimeUtil.toDate(TimeUtil.now()), metaObject);
         } catch (ReflectionException var3) {
         }
      }

      private String getUserId() {
         try {
            try {
               String loginId = StpUtil.getLoginIdAsString();
               return ObjectUtil.isNotEmpty(loginId) ? loginId : "-1";
            } catch (Exception var3) {
               String clientLoginId = StpClientUtil.getLoginIdAsString();
               return ObjectUtil.isNotEmpty(clientLoginId) ? clientLoginId : "-1";
            }
         } catch (Exception var4) {
            return "-1";
         }
      }
   }
}

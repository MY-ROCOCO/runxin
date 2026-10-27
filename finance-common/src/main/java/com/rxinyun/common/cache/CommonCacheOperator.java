package com.rxinyun.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class CommonCacheOperator {
   private static final String CACHE_KEY_PREFIX = "Cache:";
   @Resource
   private RedisTemplate<String, Object> redisTemplate;

   public void put(String key, Object value) {
      this.redisTemplate.boundValueOps("Cache:" + key).set(value);
   }

   public void put(String key, Object value, long timeoutSeconds) {
      this.redisTemplate.boundValueOps("Cache:" + key).set(value, timeoutSeconds, TimeUnit.SECONDS);
   }

   public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
      return this.redisTemplate.opsForValue().setIfAbsent("Cache:" + lockKey, requestId, expireTime, TimeUnit.MILLISECONDS);
   }

   public boolean releaseDistributedLock(String lockKey, String requestId) {
      Object currentValue = this.redisTemplate.opsForValue().get("Cache:" + lockKey);
      if (currentValue != null && currentValue.toString().equals(requestId)) {
         this.redisTemplate.delete("Cache:" + lockKey);
         return true;
      } else {
         return false;
      }
   }

   public Object get(String key) {
      return this.redisTemplate.boundValueOps("Cache:" + key).get();
   }

   public Object getAndRemove(String key) {
      DefaultRedisScript<Object> script = new DefaultRedisScript(
         "local value = redis.call('GET', KEYS[1]); if value then redis.call('DEL', KEYS[1]); end; return value", Object.class
      );
      return this.redisTemplate.execute(script, Collections.singletonList("Cache:" + key), new Object[0]);
   }

   public void remove(String... key) {
      ArrayList<String> keys = CollectionUtil.toList(key);
      List<String> withPrefixKeys = keys.stream().map(i -> "Cache:" + i).collect(Collectors.toList());
      this.redisTemplate.delete(withPrefixKeys);
   }

   public Collection<String> getAllKeys() {
      Set<String> keys = this.redisTemplate.keys("Cache:*");
      return (Collection<String>)(keys != null
         ? keys.stream().map(key -> StrUtil.removePrefix(key, "Cache:")).collect(Collectors.toSet())
         : CollectionUtil.newHashSet(new String[0]));
   }

   public Collection<Object> getAllValues() {
      Set<String> keys = this.redisTemplate.keys("Cache:*");
      return (Collection<Object>)(keys != null ? this.redisTemplate.opsForValue().multiGet(keys) : CollectionUtil.newArrayList(new Object[0]));
   }

   public Map<String, Object> getAllKeyValues() {
      Collection<String> allKeys = this.getAllKeys();
      HashMap<String, Object> results = MapUtil.newHashMap();

      for (String key : allKeys) {
         results.put(key, this.get(key));
      }

      return results;
   }

   public void removeBatch(String pattern) {
      Set<String> keys = this.redisTemplate.keys("Cache:" + pattern);
      if (keys != null) {
         this.redisTemplate.delete(keys);
      }
   }
}

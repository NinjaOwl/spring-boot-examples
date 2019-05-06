package com.neo.util;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author: jiangfw
 * @Date: 2019-04-30
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private CacheManager cacheManager;

    /**
     * 缓存数据。
     *
     * @param key key
     * @param value value
     */
    public void putCache(String key, Object value) {
        logger.info("putCache key:{}", key);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取缓存信息
     *
     * @param key key
     * @return value
     */
    public Object getCacheByKey(String key) {
        if (null == key) {
            return null;
        }
        logger.info("getCacheByKey key:{}", key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取缓存信息
     *
     * @param cacheName cacheName
     * @param key key
     * @param type value type
     * @return value
     */
    public <T> T getCacheByCacheNameAndKey(String cacheName, String key, Class<T> type) {
        if (StringUtils.isEmpty(cacheName) || StringUtils.isEmpty(key)) {
            return null;
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (null == cache) {
            logger.info("getCacheByCacheNameAndKey cacheName:{} is not exist.", cacheName);
            return null;
        }
        logger.info("getCacheByCacheNameAndKey cacheName:{},key:{}", cacheName, key);
        return cache.get(key, type);
    }

    /**
     * 删除缓存
     *
     * @param key key
     * @return boolean
     */
    public Boolean removeCache(String key) {
        if (null == key) {
            return true;
        }
        logger.info("removeCache key:{}", key);
        return redisTemplate.delete(key);
    }

    /**
     * 根据cacheName删除缓存。
     *
     * @param cacheName cacheName
     * @param cacheKey cacheKey 可以为空，如果为空则情况所有缓存。
     * @return boolean
     */
    public void cleanCache(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            if (StringUtils.isEmpty(cacheKey)) {
                cache.clear();
            } else {
                cache.evict(cacheKey);
            }
        }
        logger.info("cleanCache cacheName:{}, key:{}", cacheName, cacheKey);
    }

    /**
     * 缓存数据。
     *
     * @param key key
     * @param value value
     * @param timeout 过期时间 秒
     */
    private void putWithTimeout(Object key, Object value, long timeout) {
        logger.info("putWithTimeout key:{}", key);
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }
}
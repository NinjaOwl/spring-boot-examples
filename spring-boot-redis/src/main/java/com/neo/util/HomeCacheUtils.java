package com.neo.util;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @Author: jiangfw
 * @Date: 2019-04-30
 */
@Component
@CacheConfig(cacheNames = "home", cacheManager = "homeCacheManager")
public class HomeCacheUtils {

    @Cacheable(key = "#p0", condition = "#key != null")
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @CachePut(key = "#p0")
    public Object put(Object key, Object value) {
        return value;
    }
}
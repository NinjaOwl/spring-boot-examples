package com.neo.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;


@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 用户的ID号(customerIds),数据量统计数据缓存(recordCount) 过期时间，单位：分钟
     */
    @Value("${cache.redis.customerIds.expiretime}")
    private int customerIdsExpireTime;

    /**
     * home 缓存的过期时间
     */
    @Value("${cache.redis.home.expiretime}")
    private int homeExpireTime;

    /**
     * 自定义缓存key的生成策略。
     *
     * @return KeyGenerator
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) ->
                target.getClass().getSimpleName()
                        + "_"
                        + method.getName()
                        + "_"
                        + StringUtils.arrayToDelimitedString(params, "_");
    }

    /**
     * 配置redisTemplate的序列化形式
     *
     * <p>注意:报错特定类型的数据一定要手动创建，不要用匿名方式，否则在反序列化的时候会出现错误。
     *
     * <p>比如：
     *
     * <pre class="code">
     * List<String> a = new ArrayList<>();
     * a.add("foo");
     * </pre>
     *
     * <p>而不是：
     *
     * <pre class="code">
     * List<String> a = new ArrayList<>(){{
     * add("foo");
     * }};
     * </pre>
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return RedisTemplate
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(valueSerializer());
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        return redisTemplate;
    }

    /**
     * 设置默认的CacheManager，缓存过期时间30分钟，兼容EhCache缓存时的customerIds和recordCount缓存
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return CacheManager
     */
    @Primary
    @Bean("cacheManager")
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 缓存配置对象
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig();

        redisCacheConfiguration =
                redisCacheConfiguration
                        // 设置缓存的默认超时时间：30分钟
                        .entryTtl(Duration.ofMinutes(customerIdsExpireTime))
                        // 如果是空值，不缓存
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                // 设置key序列化器
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(keySerializer()))
                        .serializeValuesWith(
                                // 设置value序列化器
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer((valueSerializer())));

        return RedisCacheManager.builder(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    /**
     * 设置home CacheManager，缓存过期时间8小时，兼容EhCache缓存时的home缓存
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return CacheManager
     */
    @Bean("homeCacheManager")
    public CacheManager homeCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 缓存配置对象
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig();

        redisCacheConfiguration =
                redisCacheConfiguration
                        // 设置缓存的默认超时时间：30分钟
                        .entryTtl(Duration.ofMinutes(homeExpireTime))
                        // 如果是空值，不缓存
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                // 设置key序列化器
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(keySerializer()))
                        .serializeValuesWith(
                                // 设置value序列化器
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer((valueSerializer())));

        return RedisCacheManager.builder(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    /**
     * keySerializer
     *
     * @return RedisSerializer
     */
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * valueSerializer
     *
     * @return RedisSerializer
     */
    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
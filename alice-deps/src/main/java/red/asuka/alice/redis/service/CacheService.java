package red.asuka.alice.redis.service;

import red.asuka.alice.redis.cache.RedisCacheProperties;
import red.asuka.alice.redis.cache.prefix.DefaultRedisCachePrefix;
import red.asuka.alice.redis.cache.prefix.RedisCachePrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Tomonori
 * @Date: 2019/12/9 16:24
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public abstract class CacheService<S> extends BaseService<S> {

    /**
     * RedisCachePrefix处理分隔
     */
    protected static final RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();
    protected static final String UNDERLINE = "_";
    protected static final String COLON = ":";

    @Autowired
    CacheManager cacheManager;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    //自定义的部分redis属性信息
    @Autowired
    protected RedisCacheProperties redisCacheProperties;

    @Override
    protected void post() {
        super.post();
    }

    /**
     * 持久化一个键
     * @param nameSpace
     * @param key
     * @return
     */
    protected boolean cacheKey(String nameSpace, Object key) {
        StringBuilder builder = new StringBuilder(nameSpace);
        builder.append(COLON).append(key);
        return redisTemplate.persist(builder.toString());
    }

    /**
     * 重置key过期时间
     * @param key
     * @param expiredTime
     */
    protected void expire(String key, Long expiredTime) {
        stringRedisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);
    }

    /**
     * 命名空间拼接
     * @param baseSpace
     * @param tailSpace
     * @return
     */
    protected String nameSpace(String baseSpace, Object tailSpace) {
        StringBuilder builder = new StringBuilder(baseSpace);
        builder.append(UNDERLINE).append(tailSpace);
        return builder.toString();
    }

    /**
     * 获取缓存key
     * @param
     * @return
     */
    protected String getCacheKey(String nameSpace,Object key){
        StringBuilder builder = new StringBuilder(nameSpace);
        builder.append(COLON).append(key);
        return builder.toString();
    }
}

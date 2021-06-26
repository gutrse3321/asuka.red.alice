package red.asuka.alice.redis.cache.lock;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.redis.cache.RedisCacheKeyUtils;
import red.asuka.alice.redis.cache.lock.exception.RedisCacheException;
import red.asuka.alice.redis.cache.prefix.DefaultRedisCachePrefix;
import red.asuka.alice.redis.cache.prefix.RedisCachePrefix;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Random;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 15:40
 * @Desc: Redis锁具体操作类
 */
@Component
public class RedisLock implements Serializable {

    private static final long MILLI_NANO_TIME = 2000 * 1000l;
    private static final int LOCKED = 1;
    private static final Random RANDOM = new Random();
    private static final RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();

    private RedisTemplate redisTemplate;

    public RedisLock(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 加速
     * @param cacheKeyUtils
     * @param timeOut 取锁超时时间
     * @param expire 锁自动过期时间
     * @return
     * @throws Exception
     */
    public Boolean lock(RedisCacheKeyUtils cacheKeyUtils, long timeOut, long expire) throws Exception {
        try {
            Object execute = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                long nanoTime = System.nanoTime();
                long lockTimeOut = timeOut * MILLI_NANO_TIME;

                try {
                    while (System.nanoTime() - nanoTime < lockTimeOut) {
                        if (connection.setNX(cacheKeyUtils.getKeyBytes(), redisTemplate.getValueSerializer().serialize(LOCKED))) {
                            connection.expire(cacheKeyUtils.getKeyBytes(), expire);
                            return true;
                        }
                        //线程堵塞
                        Thread.sleep(3, RANDOM.nextInt(30));
                    }
                    return false;
                } catch (Exception e) {
                    //解决线程异常
                    //抛出缓存锁异常，回滚代码
                    throw new RedisCacheException(e.getMessage(), EXPF.exception(ErrorCode.CacheLockError, true));
                }
            });
            return (Boolean) execute;
        } catch (RedisCacheException e) {
            throw (Exception) e.getCause();
        }
    }

    public void unlock(RedisCacheKeyUtils cacheKeyUtils) {
        redisTemplate.execute(connection -> {
            connection.del(cacheKeyUtils.getKeyBytes());
            return null;
        }, true);
    }

    /**
     * 构造redis缓存key操作类
     * @param prefix
     * @param key
     * @return
     */
    public RedisCacheKeyUtils madeCacheKeyUtils(String prefix, Object key) {
        return new RedisCacheKeyUtils(key)
                .usePrefix(cachePrefix.prefix(prefix))
                .withKeySerializer(redisTemplate.getKeySerializer());
    }

}

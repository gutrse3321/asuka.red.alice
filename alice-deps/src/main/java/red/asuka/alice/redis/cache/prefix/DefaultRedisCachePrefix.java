package red.asuka.alice.redis.cache.prefix;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 17:17
 * @Desc: 具体实现RedisCachePrefix处理分隔
 */
public class DefaultRedisCachePrefix implements RedisCachePrefix {

    private final RedisSerializer serializer;
    private final String delimiter;

    /**
     * 无构造参数默认值
     */
    public DefaultRedisCachePrefix() {
        this(":");
    }

    public DefaultRedisCachePrefix(String delimiter) {
        this.serializer = new StringRedisSerializer();
        this.delimiter = delimiter;
    }

    @Override
    public byte[] prefix(String cacheName) {
        return this.serializer.serialize(this.delimiter != null ? cacheName.concat(this.delimiter) : cacheName.concat(":"));
    }
}

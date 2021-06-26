package red.asuka.alice.redis.cache.prefix;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 17:15
 * @Desc: 用于Redis中key和namespace中间分隔 分组
 */
public interface RedisCachePrefix {
    byte[] prefix(String var1);
}

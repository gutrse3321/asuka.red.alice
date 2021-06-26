package red.asuka.alice.redis.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 17:24
 * @Desc: redis缓存 key操作类
 */
public class RedisCacheKeyUtils {

    @Getter
    private final Object keyElement;
    private byte[] prefix;
    @Setter
    private RedisSerializer serializer;

    public RedisCacheKeyUtils(Object keyElement) {
        Assert.notNull(keyElement, "redis cache keyElement not be null!");
        this.keyElement = keyElement;
    }

    /**
     * redis序列化
     * @return
     */
    private byte[] serializerKeyElement() {
        return this.serializer == null && this.keyElement instanceof byte[] ?
                (byte[]) this.keyElement : this.serializer.serialize(this.keyElement);
    }

    /**
     * 获取key 字节数组
     * @return
     */
    public byte[] getKeyBytes() {
        //获取序列化后的key字节数组
        byte[] rawKey = this.serializerKeyElement();
        //判断是否有分隔符，没有就直接返回
        if (!this.hasPrefix()) {
            return rawKey;
        } else {
            //Arrays.copyOf 这里是增加数组this.prefix.length +rawKey.length这么长的长度，Arrays.copyOf这里第二个参数是包含了this.prefix的长度再加上后面的长度一起的
            //也就是只增加了rawKey的长度
            byte[] prefixedKey = Arrays.copyOf(this.prefix, this.prefix.length + rawKey.length);
            //这里从0开始拷贝进来
            System.arraycopy(rawKey, 0, prefixedKey, this.prefix.length, rawKey.length);
            return prefixedKey;
        }
    }

    /**
     * 判断是否有分隔符
     * @return
     */
    public boolean hasPrefix() {
        return this.prefix != null && this.prefix.length > 0;
    }

    public RedisCacheKeyUtils usePrefix(byte[] prefix) {
        this.prefix = prefix;
        return this;
    }

    public RedisCacheKeyUtils withKeySerializer(RedisSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

}

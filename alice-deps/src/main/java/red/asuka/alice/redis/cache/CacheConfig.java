package red.asuka.alice.redis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 14:50
 * @Desc:
 */
@Getter
@AllArgsConstructor
/**
 * @Accessors用于配置getter和setter方法的生成结果,fluent都以基础属性名声明方法
 * eg: 没设置则是public CacheConfig setNameSpace(String nameSpace), 设置了fluent为true，则是
 * public CacheConfig nameSpace(String nameSpace)
 * getter同理
 */
@Accessors(fluent = true)
public class CacheConfig {

    private String nameSpace;
    private long   expires;

    public static Builder copy(final CacheConfig config) {
        if (null == config) return null;
        return new Builder().nameSpace(config.nameSpace()).expires(config.expires());
    }

    @Setter
    //@Accessors用于配置getter和setter方法的生成结果
    @Accessors(fluent = true)
    public static class Builder {

        private String nameSpace;
        private long   expires;

        Builder() {
            //Builder此类的expires
            expires = 1800L;
        }

        public CacheConfig build() {
            return new CacheConfig(nameSpace, expires);
        }
    }
}

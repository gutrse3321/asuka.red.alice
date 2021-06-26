package red.asuka.alice.redis.cache.annotation;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 16:59
 * @Desc:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfigList {

    CacheConfiguration[] value();
}

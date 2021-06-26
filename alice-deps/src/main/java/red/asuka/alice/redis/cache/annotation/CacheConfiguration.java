package red.asuka.alice.redis.cache.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 16:57
 * @Desc: 缓存配置注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfiguration {

    @AliasFor(attribute = "nameSpace")
    String value() default "default";

    @AliasFor(attribute = "value")
    String nameSpace() default "default";

    long expTime() default 0L;
}

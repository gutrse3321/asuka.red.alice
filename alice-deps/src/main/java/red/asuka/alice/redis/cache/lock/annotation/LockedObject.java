package red.asuka.alice.redis.cache.lock.annotation;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 16:24
 * @Desc:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockedObject {
    String value() default "";
}

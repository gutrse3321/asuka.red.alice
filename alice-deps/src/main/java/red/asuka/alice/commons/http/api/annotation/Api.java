package red.asuka.alice.commons.http.api.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 14:27
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Api {

    @AliasFor(attribute = "name")
    String value() default "default";

    @AliasFor(attribute = "value")
    String name() default "default";
}

package red.asuka.alice.commons.springExtension.resolver.annotation;

import java.lang.annotation.*;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 15:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestModel {

    String value();
    boolean require() default true;
}

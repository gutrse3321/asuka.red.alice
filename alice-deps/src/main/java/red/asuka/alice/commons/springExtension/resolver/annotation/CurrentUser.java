package red.asuka.alice.commons.springExtension.resolver.annotation;

import java.lang.annotation.*;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 15:11
 *
 * 绑定当前登录的用户，不同于@ModelAttribute
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

    /**
     * 当前用户在request中的名字
     * @return
     */
    String value() default "user";

}

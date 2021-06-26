package red.asuka.alice.commons.http.api.annotation;

import red.asuka.alice.commons.http.api.config.ApiRegister;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 14:31
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import 把用到的资源注册到Bean
@Import(ApiRegister.class)
public @interface ApiScan {

    @AliasFor(attribute = "scanPackage")
    String value() default "";

    @AliasFor(attribute = "value")
    String scanPackage() default "";
}

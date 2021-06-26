package red.asuka.alice.commons.exception.annotation;

import red.asuka.alice.commons.exception.TranslationRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2020/1/7 14:30
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import 把用到的资源注册到Bean
@Import(TranslationRegister.class)
public @interface TranslationScan {

    String value() default "";
}

package red.asuka.alice.commons.hibernateExtension.annotation;

import red.asuka.alice.commons.hibernateExtension.validator.StringJsonValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-22 10:39
 *
 * 控制器接收JSON字符串类型
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StringJsonValidator.class)
public @interface StringJson {

    //这个是指的微服务下的resource包下的Resource Bundle 'ValidationMessages'下的properties文件
    String message() default "{red.asuka.alice.validator.StringJson.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        StringJson[] value();
    }
}

package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.view.AbstractView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 17:20
 * 方法参数验证异常，针对 类 类型，使用 @Valid 验证实体。
 *  Controller类在类上添加了 Validated注解，提交参数有错误将抛出
 *  {@link org.springframework.validation.BindException}抛出异常
 *  的方式不同。输出Code=501
 */
@ExpTranslation
public class BindExceptionTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception e) {
        return e instanceof BindException;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        Map<String, String> map = new HashMap<>();
        BindException exception = (BindException) context.getException();
        List<FieldError> allErrors = exception.getFieldErrors();

        for (FieldError field : allErrors) {
            String fieldName = field.getField();
            String message = field.getDefaultMessage();
            map.put(fieldName, message);
        }

        UnifyFailureView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, ErrorCode.Parameter.getCode());
        view.addStaticAttribute(THROWTYPE, getThrowType(context.getException()));
        Map<String, Object> parameter = new HashMap<>(1);
        parameter.put("size", map.size());
        view.addStaticAttribute(MESSAGE, interpolate(ErrorCode.Parameter.getTemplate(), parameter));
        view.addStaticAttribute(FIELDS, map);
        view.setRenderedAttributes(defaultRendered);
        return view;
    }
}

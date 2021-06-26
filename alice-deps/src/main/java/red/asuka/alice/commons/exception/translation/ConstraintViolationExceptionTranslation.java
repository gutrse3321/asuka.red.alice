package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import org.springframework.web.servlet.view.AbstractView;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 15:30
 *
 * 方法参数验证异常。
 *  Controller类在类上添加了 Validated注解，提交参数有错误将抛出
 *  {@link ConstraintViolationException} 与 {@link BindingResult}抛出异常
 *  的方式不同。输出Code=501
 */
@ExpTranslation
public class ConstraintViolationExceptionTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception e) {
        return e instanceof ConstraintViolationException;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        Map<String, String> map = new HashMap<>();
        ConstraintViolationException exception = (ConstraintViolationException) context.getException();
        Set<ConstraintViolation<?>> cvs = exception.getConstraintViolations();
        for (ConstraintViolation<?> cv : cvs) {
            String filedName = cv.getPropertyPath().toString();
            String[] split = filedName.split("\\.");
            filedName = split[split.length - 1];
            String message = cv.getMessage();
            map.put(filedName, message);
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

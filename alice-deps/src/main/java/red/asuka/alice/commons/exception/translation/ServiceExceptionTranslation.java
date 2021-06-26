package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.exception.extension.ExceptionInterface;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import red.asuka.alice.commons.support.StringUtility;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.springframework.web.servlet.view.AbstractView;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2020/1/17 17:43
 * @Title: 抛出异常转换器
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * ----- 处理主动抛出的异常 {@link ExceptionInterface}
 */
@ExpTranslation
public class ServiceExceptionTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception e) {
        if (e instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException exception = (UndeclaredThrowableException) e;
            Throwable undeclaredThrowable = exception.getUndeclaredThrowable();

            e = (Exception) undeclaredThrowable;
        }

        return e instanceof ExceptionInterface;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        /** 异常接口，获取值用 统一getter */
        ExceptionInterface exInterface;

        if (context.getException() instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException exception = (UndeclaredThrowableException) context.getException();
            Throwable undeclaredThrowable = exception.getUndeclaredThrowable();
            exInterface = (ExceptionInterface) undeclaredThrowable;
        } else {
            exInterface = (ExceptionInterface) context.getException();
        }

        AbstractView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, exInterface.getCode());

        String throwType = exInterface.getThrowType();
        if (StringUtility.isEmpty(throwType)) {
            throwType = getThrowType((Exception) exInterface);
        }
        view.addStaticAttribute(THROWTYPE, throwType);

        String messageTemplate = exInterface.getMessageTemplate();
        MessageInterpolatorContext interpolatorContext = createInterpolatorContext(exInterface.getMessageParameters());
        if (StringUtility.hasText(messageTemplate)) {
            view.addStaticAttribute(MESSAGE, interpolateByMIContext(messageTemplate, interpolatorContext));
        } else {
            view.addStaticAttribute(MESSAGE, exInterface.getExceptionMessage());
        }

        Map<String, String> fields = exInterface.getFields();
        if (null != fields) {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String value = entry.getValue();
                entry.setValue(interpolateByMIContext(value, interpolatorContext));
            }
        }
        view.addStaticAttribute(FIELDS, fields);

        return view;
    }
}

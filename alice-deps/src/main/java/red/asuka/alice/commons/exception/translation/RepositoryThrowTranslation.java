package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.exception.extension.ExceptionInterface;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import red.asuka.alice.commons.support.StringUtility;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.web.servlet.view.AbstractView;

import java.util.Map;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 15:38
 *
 * jpa异常捕获
 */
@ExpTranslation
public class RepositoryThrowTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception e) {
        boolean b = e instanceof InvalidDataAccessApiUsageException;
        if (b) {
            Throwable cause = e.getCause();
            return cause instanceof ExceptionInterface;
        }
        return false;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        ExceptionInterface anInterface = (ExceptionInterface) context.getException().getCause();
        AbstractView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, anInterface.getCode());
        view.addStaticAttribute(THROWTYPE, anInterface.getThrowType());
        String messageTemplate = anInterface.getMessageTemplate();
        MessageInterpolatorContext interpolatorContext = createInterpolatorContext(anInterface.getMessageParameters());
        if (StringUtility.hasText(messageTemplate)) {
            view.addStaticAttribute(MESSAGE, interpolateByMIContext(messageTemplate, interpolatorContext));
        } else {
            view.addStaticAttribute(MESSAGE, anInterface.getExceptionMessage());
        }
        Map<String, String> fields = anInterface.getFields();
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

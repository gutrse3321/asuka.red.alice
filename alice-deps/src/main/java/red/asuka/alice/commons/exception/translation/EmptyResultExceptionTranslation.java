package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 15:34
 */
@ExpTranslation
public class EmptyResultExceptionTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception e) {
        return e instanceof EmptyResultDataAccessException;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        AbstractView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, ErrorCode.ResourceNotFound.getCode());
        view.addStaticAttribute(THROWTYPE, getThrowType(context.getException()));
        view.addStaticAttribute(MESSAGE, interpolate(ErrorCode.ResourceNotFound.getTemplate(),null));
        return view;

    }
}

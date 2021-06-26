package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @Author: Tomonori
 * @Date: 2020/1/17 11:22
 * @Title: 默认异常转换器，输出Code = 500
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public class DefaultTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception e) {
        return true;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        AbstractView view = new UnifyFailureView();

        view.addStaticAttribute(CODE, ErrorCode.Server.getCode());
        view.addStaticAttribute(THROWTYPE, getThrowType(context.getException()));
        view.addStaticAttribute(MESSAGE, interpolate(ErrorCode.Server.getTemplate(), null));
        view.addStaticAttribute(DETAILMESSAGE, context.getException().getLocalizedMessage());

        return view;
    }
}

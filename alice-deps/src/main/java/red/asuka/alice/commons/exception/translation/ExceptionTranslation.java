package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.exception.TranslationContext;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 17:09
 * @Desc: 异常转换器接口
 */
public interface ExceptionTranslation {

    /**
     * 检查是否支持
     * 一般是返回传参是否是另一个异常类的实例 eg: return ? instanceof SomeException
     * @param e
     * @return
     */
    boolean support(Exception e);

    /**
     * 转换成JSON
     * @param context
     * @return
     */
    AbstractView translationToJson(TranslationContext context);
}

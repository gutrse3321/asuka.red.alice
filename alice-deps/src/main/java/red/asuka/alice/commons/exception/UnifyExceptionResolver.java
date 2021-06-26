package red.asuka.alice.commons.exception;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.annotation.TranslationScan;
import red.asuka.alice.commons.exception.translation.AbstractExceptionTranslation;
import red.asuka.alice.commons.exception.translation.DefaultTranslation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.MessageInterpolator;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2020/1/7 14:28
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Slf4j
@Configuration
/**
 * - @ConditionalOnBean         当给定的在bean存在时,则实例化当前Bean
 * - @ConditionalOnMissingBean  当给定的在bean不存在时,则实例化当前Bean
 * - @ConditionalOnClass        当给定的类名在类路径上存在，则实例化当前Bean
 * - @ConditionalOnMissingClass 当给定的类名在类路径上不存在，则实例化当前Bean
 */
@ConditionalOnClass({LocalValidatorFactoryBean.class})
@TranslationScan("red.asuka.alice.commons.exception.translation")
public class UnifyExceptionResolver implements HandlerExceptionResolver, Ordered {

    private int order = 0;

    /** 异常转换抽象类 */
    private AbstractExceptionTranslation defaultTranslation;
    private List<AbstractExceptionTranslation> translations;

    private String[] jsonp = {"callback", "jsonp"};
    private Validator validator;
    private MessageInterpolator interpolator;

    public UnifyExceptionResolver(LocalValidatorFactoryBean validator, List<AbstractExceptionTranslation> translationList) {
        this.validator = validator;
        this.translations = null == translationList ? new ArrayList<>() : translationList;

        LocalValidatorFactoryBean validatorFactoryBean = validator;
        interpolator = validatorFactoryBean.getMessageInterpolator();
        defaultTranslation = new DefaultTranslation();
        defaultTranslation.setInterpolator(interpolator);

        translations.forEach(abstractExceptionTranslation -> abstractExceptionTranslation.setInterpolator(interpolator));
    }

    /**
     * 控制统一处理 优先级大于 {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}
     * @return
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        TranslationContext translationContext = new TranslationContext(request, response, handler, e, jsonp);

        return new ModelAndView(converToView(translationContext));
    }

    /**
     * 返回视图，打印相关信息日志
     * @param context
     * @return
     */
    private AbstractView converToView(TranslationContext context) {
        AbstractView jsonView = null;

        for (AbstractExceptionTranslation translation : translations) {
            if (translation.support(context.getException())) {
                jsonView = translation.getView(context);
                break;
            }
        }

        if (null == jsonView) {
            jsonView = defaultTranslation.getView(context);
        }

        //输出错误日志，只输出大于等于500的日志
        Map<String, Object> attributes = jsonView.getStaticAttributes();
        if (attributes != null && null != attributes.get("code")) {
            Integer code = (Integer) attributes.get("code");
            String uri = context.getRequest() != null ? context.getRequest().getRequestURI() : "unknown";

            if (code >= ErrorCode.Server.getCode()) {
                log.error("【{} form={} ===> 错误码:{}, 统一异常组件捕捉的异常日志，详细信息：{}", uri, formStr(context.getRequest()), code, attributes.get("detailMessage"));
                log.error(EXPF.getExceptionMsg(context.getException()) + "】");
            }
        }

        return jsonView;
    }

    /**
     * 重新组装请求参数字符串
     * @param request
     * @return
     */
    private String formStr(HttpServletRequest request) {
        if (request == null) {
            return "{}";
        }

        String str = "{";
        Enumeration pNames = request.getParameterNames();

        while (pNames.hasMoreElements()) {
            String name = (String) pNames.nextElement();
            String value = request.getParameter(name);
            str += (name + "=" + value + ",");
        }

        return str + "}";
    }
}

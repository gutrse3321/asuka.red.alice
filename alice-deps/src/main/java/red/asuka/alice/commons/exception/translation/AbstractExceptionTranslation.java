package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.exception.ConstraintDescriptorImpl;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.MessageInterpolator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 17:08
 * @Desc: 异常转换抽象类
 */
public abstract class AbstractExceptionTranslation implements ExceptionTranslation {

    protected static final String THROWTYPE = "throwType";
    protected static final String CODE = "code";
    protected static final String MESSAGE = "message";
    protected static final String FIELDS = "fields";
    protected static final String DETAILMESSAGE = "detailMessage";

    protected static Set<String> defaultRendered;

    /**
     * 插值一个给定的约束信息
     * 实现MessageInterpolator接口的类应尽可能的容忍语法错误，且必须是线程安全的
     */
    @Getter
    @Setter
    private MessageInterpolator interpolator;

    public AbstractExceptionTranslation() {
        defaultRendered = new HashSet<>();
        defaultRendered.add(THROWTYPE);
        defaultRendered.add(CODE);
        defaultRendered.add(MESSAGE);
        defaultRendered.add(FIELDS);
        defaultRendered.add(DETAILMESSAGE);
    }

    public boolean isToJson(HttpServletRequest request) {
        return false;
    }

    public boolean isToXml(HttpServletRequest request) {
        return false;
    }

    public boolean isToHtml(HttpServletRequest request) {
        return false;
    }

    /**
     * 处理异常该返回的视图和jsonp
     * @param context
     * @return
     */
    public AbstractView getView(TranslationContext context) {
        /**
         * 传入异常转换上下文，转换为JSON的视图，需要使用继承AbstractExceptionTranslation（此类）具体实现的这个translationToJson方法
         */
        AbstractView jsonView = this.translationToJson(context);

        /**
         * UnifyFailureView设置jsonp的值
         */
        if (null != context.getJsonp() && context.getJsonp().length > 0) {
            /**
             * 判断jsonView继承的父类是UnifyFailureView
             * 是否都是会转换成UnifyFailureView类型
             */
            if (jsonView instanceof UnifyFailureView) {
                String[] jsonp = ((UnifyFailureView) jsonView).getJsonp();
                if (null == jsonp || jsonp.length == 0) {
                    ((UnifyFailureView) jsonView).setJsonp(context.getJsonp());
                }
            } else {
                Map<String, Object> staticAttributes = jsonView.getStaticAttributes();
                UnifyFailureView failureView = new UnifyFailureView(context.getJsonp());
                failureView.setAttributesMap(staticAttributes);
                jsonView = failureView;
            }
        }
        return jsonView;
    }

    /**
     * 创建插值器上下文
     * @param messageParameters
     * @return
     */
    public MessageInterpolatorContext createInterpolatorContext(Map<String, Object> messageParameters) {
        if (null == messageParameters) messageParameters = new HashMap<>(0);
        //FIXME 这是和1.5.x的版本不同，新增了最后一个参数，一定要实例化个hashMap对象，不能传null，不然疯狂空指针异常
        return new MessageInterpolatorContext(new ConstraintDescriptorImpl(messageParameters), null, null, messageParameters, new HashMap<>());
    }

    /**
     * 返回基于约束验证上下文的插值消息模板
     */
    public String interpolate(String messageTemplate, Map<String, Object> messageParameters) {
        MessageInterpolatorContext interpolatorContext = createInterpolatorContext(messageParameters);
        return interpolator.interpolate(messageTemplate, interpolatorContext);
    }

    /**
     * HibernateMessageInterpolatorContext继承MessageInterpolator.Context
     * MessageInterpolator.Context提供特定于Hibernate Validator的功能的扩展
     * @param messageTemp
     * @param miContext
     * @return
     */
    public String interpolateByMIContext(String messageTemp, HibernateMessageInterpolatorContext miContext) {
        return interpolator.interpolate(messageTemp, miContext);
    }

    protected String getThrowType(Exception e) {
        return EXPF.getThrowtype(e);
    }

    protected Throwable rootCause(Exception e) {
        Throwable rootCause = null;
        Throwable cause = e.getCause();

        while (cause != null && cause != rootCause) {
            rootCause = cause;
            cause = cause.getCause();
        }

        return rootCause;
    }

}

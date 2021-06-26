package red.asuka.alice.commons.exception;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 17:10
 * @Desc: 异常转换上下文
 */
@Getter
public class TranslationContext {

    HttpServletRequest request;
    HttpServletResponse response;
    Object handler;
    Exception exception;
    String[] jsonp;

    public TranslationContext(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        this(request, response, handler, exception, null);
    }

    public TranslationContext(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception, String[] jsonp) {
        this.request = request;
        this.response = response;
        this.handler = handler;
        this.exception = exception;
        this.jsonp = jsonp;
    }
}

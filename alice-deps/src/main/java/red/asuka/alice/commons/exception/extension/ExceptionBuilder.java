package red.asuka.alice.commons.exception.extension;

import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 15:52
 * @Desc: 异常处理统一setter接口
 */
public interface ExceptionBuilder {

    ExceptionBuilder setThrowType(String throwType);
    ExceptionBuilder setCode(int code);
    ExceptionBuilder setFields(Map<String, String> fields);
    ExceptionBuilder setMessage(String message);
    ExceptionBuilder setMessageParameters(Map<String, Object> messageParameters);

    Exception build();
}

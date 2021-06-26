package red.asuka.alice.commons.exception.extension;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 16:27
 * @Desc: RuntimeException，Spring AOP捕获到抛出的RuntimeException 表示这种异常是否出现经常不可预料，出现了的话，就中止处理
 */
public class RollbackCatchException extends RuntimeException implements ExceptionInterface {

    private final String throwType;
    private final int code;
    private final Map<String, String> fields;
    private final String messageTemplate;
    private final Map<String, Object> messageParameters;

    public RollbackCatchException(String throwType, int code, Map<String, String> fields, String message, String messageTemplate, Map<String, Object> messageParameters) {
        super(message);
        this.throwType = throwType;
        this.code = code;
        this.fields = fields;
        this.messageTemplate = messageTemplate;
        this.messageParameters = messageParameters;
    }

    /**
     * 返回实例化的Builder静态类
     * @return
     */
    public static Builder custom() {
        return new Builder();
    }

    @Override
    public String getThrowType() {
        return throwType;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public Map<String, Object> getMessageParameters() {
        return messageParameters;
    }

    @Override
    public String getExceptionMessage() {
        return super.getLocalizedMessage();
    }

    /**
     * 子类中进行setter
     * 再使用实现的方法build()，实例化上级类，并构造传参
     */
    public static class Builder implements ExceptionBuilder {

        //正则
        private static final String PATTERN = "^\\{.+\\}$";

        private String throwType;
        private int code;
        private Map<String, String> fields;
        private String messageTemplate;
        private Map<String, Object> messageParameters;
        private String exceptionMessage;

        @Override
        public ExceptionBuilder setThrowType(String throwType) {
            this.throwType = throwType;
            return this;
        }

        @Override
        public ExceptionBuilder setCode(int code) {
            this.code = code;
            return this;
        }

        @Override
        public ExceptionBuilder setFields(Map<String, String> fields) {
            this.fields = fields;
            return this;
        }

        @Override
        public ExceptionBuilder setMessage(String message) {
            Pattern compile = Pattern.compile(PATTERN);
            Matcher matcher = compile.matcher(message);
            if (matcher.matches()) {
                this.messageTemplate = message;
            } else {
                this.exceptionMessage = message;
            }
            return this;
        }

        @Override
        public ExceptionBuilder setMessageParameters(Map<String, Object> messageParameters) {
            this.messageParameters = messageParameters;
            return this;
        }

        @Override
        public Exception build() {
            /*StringBuilder builder = new StringBuilder();
            builder.append("throwType [").append(throwType).append("] ");
            builder.append("code [").append(code == 0 ? "" : code).append("] ");
            builder.append("message [").append(null == messageTemplate ? exceptionMessage : messageTemplate).append("] ");*/
            //return new ServiceRollbackException(throwType, code, fields, builder.toString(), messageTemplate, messageParameters);
            return new RollbackCatchException(throwType, code, fields, exceptionMessage, messageTemplate, messageParameters);
        }
    }
}

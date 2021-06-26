package red.asuka.alice.commons.exception.extension;

import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 15:42
 * @Desc: 异常接口，获取值用 统一getter
 */
public interface ExceptionInterface {

    /**
     * 错误定位
     * @return
     */
    String getThrowType();

    /**
     * 错误码
     * @return
     */
    int getCode();

    /**
     * 错误参数及错误原因
     * @return
     */
    Map<String, String> getFields();

    /**
     * 错误消息模板
     * @return
     */
    String getMessageTemplate();

    /**
     * 错误消息模板参数
     * @return
     */
    Map<String, Object> getMessageParameters();

    /**
     * 异常消息
     */
    String getExceptionMessage();
}

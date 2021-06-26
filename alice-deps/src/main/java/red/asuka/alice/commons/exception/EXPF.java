package red.asuka.alice.commons.exception;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.extension.ExceptionBuilder;
import red.asuka.alice.commons.exception.extension.RollbackCatchException;
import red.asuka.alice.commons.exception.extension.SimpleCatchException;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 15:16
 * @Desc: 异常面板
 */
@UtilityClass
public class EXPF {

    /**
     * ExceptionBuilder接口实现setter方法，有两个实现类，
     * 分别为不带回滚的异常和带回滚的
     * @param code
     * @param message
     * @param messageParameters
     * @param throwType
     * @param fields
     * @param rollback
     * @return
     */
    private ExceptionBuilder getBuilder(int code, String message, Map<String, Object> messageParameters, String throwType, Map<String, String> fields, boolean rollback) {
        if (rollback) {
            return RollbackCatchException.custom().setCode(code).setMessage(message).setMessageParameters(messageParameters).setThrowType(throwType).setFields(fields);
        } else {
            return SimpleCatchException.custom().setCode(code).setMessage(message).setMessageParameters(messageParameters).setThrowType(throwType).setFields(fields);
        }
    }

    public ExceptionBuilder rollbackBuilder() {
        return getBuilder(0, null, null, null, null, true);
    }

    public ExceptionBuilder simpleBuilder() {
        return getBuilder(0, null, null, null, null, false);
    }

    public Exception exception(ErrorCode code, boolean rollback) {
        return getBuilder(code.getCode(), code.getTemplate(), null, null, null, rollback).build();
    }

    public Exception exception(ErrorCode code, Map<String, Object> messageParameters, String throwType, boolean rollback) {
        return getBuilder(code.getCode(), code.getTemplate(), messageParameters, throwType, null, rollback).build();
    }

    public Exception exception(ErrorCode code, Map<String, Object> messageParameters, String throwType, Map<String, String> fields,boolean rollback) {
        return getBuilder(code.getCode(), code.getTemplate(), messageParameters, throwType, fields, rollback).build();
    }

    public Exception exception(int code, String message, boolean rollback) {
        return getBuilder(code, message, null, null, null, rollback).build();
    }

    public Exception exception(ErrorCode code, String message, boolean rollback) {
        return getBuilder(code.getCode(), message, null, null, null, rollback).build();
    }

    public Exception exception(int code, String message, String throwType, boolean rollback) {
        return getBuilder(code, message, null, throwType, null, rollback).build();
    }

    public Exception exception(int code, String message, Map<String, Object> messageParameters, String throwType, boolean rollback) {
        return getBuilder(code, message, messageParameters, throwType, null, rollback).build();
    }

    public Exception exception(int code, String message, Map<String, Object> messageParameters, String throwType, Map<String, String> fields, boolean rollback) {
        return getBuilder(code, message, messageParameters, throwType, fields, rollback).build();
    }

    public Exception E300(Map<String, String> fields, boolean rollback) {
        Map<String, Object> map = new HashMap<>();
        map.put("size", fields.size());
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), map, null, fields, rollback).build();
    }

    public Exception E300(Map<String, String> fields, String throwType, boolean rollback) {
        Map<String, Object> map = new HashMap<>();
        map.put("size", fields.size());
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), map, throwType, fields, rollback).build();
    }

    public Exception E300(Map<String, Object> messageParameter, Map<String, String> fields, boolean rollback) {
        if (null != messageParameter) {
            Object object = messageParameter.get("size");
            if (null == object) {
                messageParameter.put("size", fields.size());
            }
        }
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), messageParameter, null, fields, rollback).build();
    }

    public Exception E300(Map<String, Object> messageParameter, String shrowType, Map<String, String> fields, boolean rollback) {
        if (null != messageParameter) {
            Object object = messageParameter.get("size");
            if (null == object) {
                messageParameter.put("size", fields.size());
            }
        }
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), messageParameter, shrowType, fields, rollback).build();
    }

    public Exception E301(String throwType, boolean rollback) {
        return getBuilder(ErrorCode.Conflict.getCode(), ErrorCode.Conflict.getTemplate(), null, throwType, null, rollback).build();
    }

    public Exception E404(boolean rollback) {
        return getBuilder(ErrorCode.ResourceNotFound.getCode(), ErrorCode.ResourceNotFound.getTemplate(), null, null, null, rollback).build();
    }

    public Exception E404(String throwType, boolean rollback) {
        return getBuilder(ErrorCode.ResourceNotFound.getCode(), ErrorCode.ResourceNotFound.getTemplate(), null, throwType, null, rollback).build();
    }

    public Exception E404(Map<String, Object> messageParameter, boolean rollback) {
        return getBuilder(ErrorCode.ResourceNotFound.getCode(), ErrorCode.ResourceNotFound.getTemplate(), messageParameter, null, null, rollback).build();
    }

    public Exception E500(String throwType, boolean rollback) {
        return getBuilder(ErrorCode.Server.getCode(), ErrorCode.Server.getTemplate(), null, throwType, null, rollback).build();
    }

    /**
     * 获取异常日志
     * @param e
     * @return
     */
    public String getExceptionMsg(Exception e){
        StringBuilder sOut = new StringBuilder();
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut.append("\tat " + s + "\r\n");
        }
        return sOut.toString();
    }

    public String getThrowtype(Exception e) {
        StackTraceElement[] st = e.getStackTrace();
        StringBuilder builder = new StringBuilder("Class [");
        StackTraceElement element = st[0];
        if(st.length >= 3) {
            String className = element.getClassName();
            if(className.equals(SimpleCatchException.Builder.class.getTypeName()) || className.equals(RollbackCatchException.Builder.class.getTypeName())) {
                element = st[2];
            }
        }
        builder.append(element.getClassName()).append("] Method [").append(element.getMethodName()).append("]");
        return builder.toString();
    }
}

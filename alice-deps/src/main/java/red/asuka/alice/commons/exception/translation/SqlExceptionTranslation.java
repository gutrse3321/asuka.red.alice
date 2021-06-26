package red.asuka.alice.commons.exception.translation;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.TranslationContext;
import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.springExtension.view.UnifyFailureView;
import org.springframework.web.servlet.view.AbstractView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-23 15:40
 *
 * 数据库异常转换
 */
@ExpTranslation
public class SqlExceptionTranslation extends AbstractExceptionTranslation {

    private static List<Integer> ignoreSqlCode;

    public SqlExceptionTranslation() {
        ignoreSqlCode = new ArrayList<>();
        ignoreSqlCode.add(1062); // 悲观锁异常
        ignoreSqlCode.add(1213); // 悲观锁异常
    }


    @Override
    public boolean support(Exception e) {
        return this.rootCause(e) instanceof SQLException;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        Throwable throwable = this.rootCause(context.getException());
        SQLException sqlException = (SQLException) throwable;
        AbstractView view = new UnifyFailureView();
        if (ignoreSqlCode.contains(sqlException.getErrorCode())) {
            view.addStaticAttribute(CODE, ErrorCode.Conflict.getCode());
            view.addStaticAttribute(THROWTYPE, getThrowType(context.getException()));
            view.addStaticAttribute(MESSAGE, sqlException.getMessage());
        } else {
            view.addStaticAttribute(CODE, ErrorCode.Server.getCode());
            view.addStaticAttribute(THROWTYPE, getThrowType(context.getException()));
            view.addStaticAttribute(MESSAGE, "Code [" + sqlException.getErrorCode() + "] Message [" + sqlException.getMessage() + "]");
        }
        return view;

    }
}

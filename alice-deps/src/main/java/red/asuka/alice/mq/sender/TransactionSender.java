package red.asuka.alice.mq.sender;

import red.asuka.alice.mq.annotation.TransMessage;
import red.asuka.alice.mq.db.DBCoordinator;
import red.asuka.alice.mq.util.MQConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 15:50
 * @Desc:
 */
@Component
@Aspect
public class TransactionSender {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RabbitSender rabbitSender;
    @Autowired
    DBCoordinator dbCoordinator;
    @Autowired
    ApplicationContext applicationContext;

    /**
     * 定义注解类型的切点，只要方法上有该注解，都会进到此切面
     */
    @Pointcut("@annotation(red.asuka.alice.mq.annotation.TransMessage)")
    public void annotationSender() {
    }

    @Around("annotationSender() && @annotation(rd)")
    public void sendMsg(ProceedingJoinPoint joinPoint, TransMessage rd) throws Throwable {
        logger.info("==> custom mq annotation, rd: {}", rd);

        String exchange = rd.exchange();
        String bindingKey = rd.bindingKey();
        String dbCoordinator = rd.dbCoordinator();
        String bizName = rd.bizName() + MQConstants.DB_SPLIT + getCurrentDateTime();
        DBCoordinator coordinator = null;

        try {
            //装配DBCoordinator的实例
            coordinator = (DBCoordinator) applicationContext.getBean(dbCoordinator);
        } catch (Exception e) {
            logger.error("无消息存储类，事务执行中止");
            return;
        }

        /**
         * 发送钱暂存消息，设置消息为prepare状态
         */
        coordinator.setMsgPrepare(bizName);
        Object returnObj;

        try {
            //赋值：继续执行函数
            returnObj = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("业务执行失败，业务名称：", bizName);
            throw e;
        }

        if (returnObj == null) {
            //如果等于null，赋值空字符串
            returnObj = MQConstants.BLANK_STR;
        }
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat(MQConstants.TIME_PATTERN).format(new Date());
    }
}

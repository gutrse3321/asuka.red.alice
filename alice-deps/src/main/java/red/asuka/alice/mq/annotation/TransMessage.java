package red.asuka.alice.mq.annotation;

import red.asuka.alice.mq.util.MQConstants;

import java.lang.annotation.*;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 10:34
 * @Desc: 注解类，用于无侵入的实现分布式事务
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TransMessage {

    String exchange() default "";                              //要发送的交换机
    String bindingKey() default "";                            //要发送的key
    String bizName() default "";                               //业务编号
    String dbCoordinator() default MQConstants.DB_COORDINATOR; //消息落库的处理方式db or redis
}

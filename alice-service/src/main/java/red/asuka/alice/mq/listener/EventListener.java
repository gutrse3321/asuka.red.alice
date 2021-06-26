package red.asuka.alice.mq.listener;

import red.asuka.alice.commons.constant.QueueConstant;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.mq.MQSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-07 10:18
 */
@Slf4j
@Component
@RabbitListener(queues = QueueConstant.EVENT)
public class EventListener {

    @Autowired
    MQSender mqSender;

    @RabbitHandler
    public void doIt(String json) {
        try {
            log.info("【rabbitmq 收到了发出的消息：{}】", json);
//            mqSender.send("其他的交换机", "其他的队列", "json字符串");
        } catch (Exception e) {
            log.error(EXPF.getExceptionMsg(e));
        }
    }
}

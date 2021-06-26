package red.asuka.alice.mq;

import red.asuka.alice.commons.constant.ExchangeConstant;
import red.asuka.alice.commons.constant.QueueConstant;
import red.asuka.alice.commons.support.JsonUtility;
import red.asuka.alice.persist.model.rabbitmq.EventModel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-07 10:12
 */
@Component
public class MQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(String queue, String context) {
        this.rabbitTemplate.convertAndSend(queue, context);
    }

    public void send(String exchange,String queue, String context) {
        this.rabbitTemplate.convertAndSend(exchange,queue,context);
    }

    public void sendEvent(EventModel eventModel) throws Exception {
        send(ExchangeConstant.EVENT_EXCHANGE, QueueConstant.EVENT, JsonUtility.toString(eventModel));
    }

    public void send(String routingKey, String context, Long delayTime) {
        rabbitTemplate.convertAndSend(routingKey, context, message -> {
            message.getMessageProperties().setHeader("x-delay", delayTime);
            return message;
        });
    }

    public void send(String exchange,String routingKey, String context, Long delayTime) {
        rabbitTemplate.convertAndSend(exchange, routingKey, context, message -> {
            message.getMessageProperties().setHeader("x-delay", delayTime);
            return message;
        });
    }
}

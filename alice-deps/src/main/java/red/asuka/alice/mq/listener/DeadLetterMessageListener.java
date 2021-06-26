package red.asuka.alice.mq.listener;

import red.asuka.alice.mq.db.DBCoordinator;
import red.asuka.alice.mq.util.MQConstants;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 17:45
 * @Desc: 死信队列监听器
 */
@Component
public class DeadLetterMessageListener implements ChannelAwareMessageListener {

    private Logger logger = LoggerFactory.getLogger(DeadLetterMessageListener.class);

    @Autowired
    DBCoordinator dbCoordinator;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        String messageBody = new String(message.getBody());

        logger.warn("dead letter message: {} | tag: {}", messageBody, message.getMessageProperties().getDeliveryTag());

        //TODO 发邮件、入日志库

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        //删除redis中的缓存的消息
        dbCoordinator.delete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY, messageProperties.getMessageId());
    }
}

package red.asuka.alice.mq.listener;

import red.asuka.alice.mq.db.DBCoordinator;
import red.asuka.alice.mq.util.MQConstants;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 14:36
 * @Desc: RabbitMQ抽象消息监听，所有消息消费者必须继承此类
 */
public abstract class AbstractMessageListener implements ChannelAwareMessageListener {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DBCoordinator dbCoordinator;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 接收消息，子类必须实现该方法
     * @param message    消息对象
     * @throws Exception
     */
    public abstract void receiveMessage(Message message) throws Exception;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        //获取该消息的索引
        Long deliveryTag = messageProperties.getDeliveryTag();
        //消息重发次数(每一次+1)
        Long consumerCount = dbCoordinator.incrResendKey(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY, messageProperties.getMessageId());

        logger.info("收到消息，当前消息ID: {} 消费次数: {}", messageProperties.getMessageId(), consumerCount);

        try {
            receiveMessage(message);
            //成功的回执
            channel.basicAck(deliveryTag, false);
            //如果消费成功，将Redis中统计消息消费次数的缓存删除
            dbCoordinator.delete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY, messageProperties.getMessageId());
        } catch (Exception e) {
            logger.error("RabbitMQ 消息消费失败，", e.getMessage(), e);
            if (consumerCount >= MQConstants.MAX_CONSUMER_COUNT) {
                //入死信队列
                channel.basicReject(deliveryTag, false);
                rabbitTemplate.convertAndSend(MQConstants.DLX_EXCHANGE, MQConstants.DLX_ROUTING_KEY, message);
            } else {
                //重回到队列，重新消费，按照2的指数级递增
                Thread.sleep((long) (Math.pow(MQConstants.BASE_NUM, consumerCount) * 1000));
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }
}

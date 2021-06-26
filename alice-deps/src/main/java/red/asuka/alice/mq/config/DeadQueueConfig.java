package red.asuka.alice.mq.config;

import red.asuka.alice.mq.listener.DeadLetterMessageListener;
import red.asuka.alice.mq.util.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 17:11
 * @Desc: RabbitMQ交换机、队列的配置类
 *        定义交换机、key、queue并做好绑定
 *        同时定义每个队列的ttl，队列最大长度，Qos等等
 *        这里值绑定了死信队列
 *        建议每个队列定义自己的QueueConfig
 */
@Configuration
public class DeadQueueConfig {

    /***********************************************************
     * 死信交换机
     * @return
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(MQConstants.DLX_EXCHANGE);
    }

    /***********************************************************
     * 死信队列
     * @return
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(MQConstants.DLX_QUEUE, true, false, false);
    }

    /************************************************************
     * 通过死信路由key绑定死信交换机和死信队列
     * @return
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(MQConstants.DLX_ROUTING_KEY);
    }

    /**
     * 死信队列的监听
     * @param connectionFactory
     * @param deadLetterMessageListener
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer deadLetterListenerContainer(ConnectionFactory connectionFactory, DeadLetterMessageListener deadLetterMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(dlxQueue());
        container.setExposeListenerChannel(true);
        //监听者必须通过调用Channel.basicAck()来告知所有的消息
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new MessageListenerAdapter(deadLetterMessageListener));
        /**
         * 设置消费者能处理的最大数
         */
        container.setPrefetchCount(100);
        return container;
    }
}

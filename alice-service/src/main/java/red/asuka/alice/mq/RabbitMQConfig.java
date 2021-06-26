package red.asuka.alice.mq;

import red.asuka.alice.commons.constant.ExchangeConstant;
import red.asuka.alice.commons.constant.QueueConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-07 10:08
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 测试展示广播交换机
     * @return
     */
    @Bean
    FanoutExchange testFanoutExchange() {
        return new FanoutExchange("test.fanout.exchange");
    }

    /**
     * 测试展示队列
     * @return
     */
    @Bean
    Queue testFanoutReceive() {
        return new Queue("test.fanout.queue");
    }

    /**
     * 将队列绑定到交换机
     * @return
     */
    @Bean
    Binding testFanoutReceiveBinding() {
        return BindingBuilder.bind(testFanoutReceive()).to(testFanoutExchange());
    }

    @Bean
    Queue eventQueue() {
        return new Queue(QueueConstant.EVENT);
    }

    @Bean
    FanoutExchange eventFanoutExchange() {
        return new FanoutExchange(ExchangeConstant.EVENT_EXCHANGE);
    }

    @Bean
    Binding eventDirectBinding() {
        return BindingBuilder.bind(eventQueue()).to(eventFanoutExchange());
    }
}

package red.asuka.alice.mq.config;

import red.asuka.alice.mq.db.DBCoordinator;
import red.asuka.alice.mq.util.CompleteCorrelationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 10:38
 * @Desc: Rabbit Template 配置工厂类
 */
@Configuration
public class RabbitTemplateConfig {

    private Logger logger = LoggerFactory.getLogger(RabbitTemplateConfig.class);

    @Autowired
    ApplicationContext applicationContext;

    //选择装配的是RabbitConnectionConfig 的指定的连接工厂Bean
    @Qualifier("transConnectionFactory")
    @Autowired
    ConnectionFactory transConnectionFactory;

    boolean returnFlag = false;

    /**
     * 默认rabbitTemplate配置工厂Bean注入
     * @param connectionFactory
     * @return
     */
    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    /**
     * 自定义rabbitTemplate Bean注入
     * @return
     */
    @Bean("customRabbitTemplate")
    public RabbitTemplate customRabbitTemplate() {
        logger.info("==> custom rabbitTemplate, connectionFactory: {}", transConnectionFactory);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(transConnectionFactory);
        //mandatory 必须设置为true，ReturnCallback才会调用
        rabbitTemplate.setMandatory(true);

        //消息发送到RabbitMQ交换器后接受ack回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (returnFlag) {
                logger.error("mq发送错误，无对应的交换机，confirm回调，ack={}，correlationData={} cause={} returnFlag={}", ack, correlationData, cause, returnFlag);
            }

            logger.info("confirm回调，ack={} correlationData={} cause={}", ack, correlationData, cause);
            String msgId = correlationData.getId();

            if (ack) {
                logger.info("消息已正确投递到队列，correlationData: {}", correlationData);
                //清除重发缓存
                String dbCoordinatior = ((CompleteCorrelationData) correlationData).getCoordinator();
                //装配DBCoordinator实现
                DBCoordinator coordinator = (DBCoordinator) applicationContext.getBean(dbCoordinatior);
                //消息发送成功，删除ready状态信息
                coordinator.setMsgSuccess(msgId);
            } else {
                logger.error("消息投递至交换机失败，业务好：{}，原因：{}", correlationData.getId(), cause);
            }
        });

        //消息发送到RabbitMQ交换器，但无相应Exchange时的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageId = message.getMessageProperties().getMessageId();

            logger.error("return回调，没有找到任何匹配的队列！message id:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey:{}",
                    messageId, replyCode, replyText, exchange, routingKey);

            returnFlag = true;
        });

        return rabbitTemplate;
    }

}

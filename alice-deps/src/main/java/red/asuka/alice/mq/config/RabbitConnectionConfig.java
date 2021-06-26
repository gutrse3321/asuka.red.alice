package red.asuka.alice.mq.config;

import red.asuka.alice.commons.support.NullResolveUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 16:15
 * @Desc: Rabbit MQ连接工厂配置
 */
@Configuration
@EnableRabbit
//使RabbitProperties注入装配
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConnectionConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RabbitProperties rabbitProperties;

    public RabbitConnectionConfig(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    /**
     * 创建RabbitMQ连接工厂
     * @return
     * @throws Exception
     */
    @Bean("transConnectionFactory")
    public CachingConnectionFactory rabbitConnectionFactory() throws Exception {
        logger.info("==> custom rabbitmq connection factory");

        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        factoryBean.setHost(rabbitProperties.determineHost());
        factoryBean.setPort(rabbitProperties.determinePort());
        factoryBean.setUsername(rabbitProperties.getUsername());
        factoryBean.setPassword(rabbitProperties.getPassword());
        //factoryBean.setVirtualHost("trans.message);
        //创建单例
        factoryBean.afterPropertiesSet();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factoryBean.getObject());
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);
        NullResolveUtility.resolve(() -> rabbitProperties.getCache().getChannel().getSize())
                .ifPresent(x -> connectionFactory.setChannelCacheSize(x));
        return connectionFactory;
    }

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() throws Exception {
        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        factoryBean.setHost(rabbitProperties.determineHost());
        factoryBean.setPort(rabbitProperties.determinePort());
        factoryBean.setUsername(rabbitProperties.getUsername());
        factoryBean.setPassword(rabbitProperties.getPassword());
        factoryBean.afterPropertiesSet();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factoryBean.getObject());
        NullResolveUtility.resolve(() -> rabbitProperties.getCache().getChannel().getSize())
                .ifPresent(x -> connectionFactory.setChannelCacheSize(x));
        return connectionFactory;
    }

}

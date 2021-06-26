package red.asuka.alice.redis.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 15:59
 * @Desc:
 */
@Configuration
//自动化配置
@ConditionalOnProperty(prefix = "spring.redis.session", name = "enabled")
/**
 * EnableRedisHttpsSession:
 *     将此注解添加到@Configuration注解的类，
 *     以将SessionRepositoryFilter注入一个名为springSessionRepositoryFilter并由
 *     Redis支持的Bean。
 *     为了使用这个注解，必须提供一个RedisConnectionFactory
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RedisSessionConfiguration {
}

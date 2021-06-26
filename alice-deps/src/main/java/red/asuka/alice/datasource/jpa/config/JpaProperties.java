package red.asuka.alice.datasource.jpa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Tomonori
 * @Date: 2019/11/11 19:01
 * @Desc:
 */
@Data
/**
 * ConfigurationProperties这个注解之前不生效，使用@Component注入也可以，
 * 但实际上需要在其他类上使用EnableConfigurationProperties注解并传现在这个class（TODO 即JpaAutoConfiguration类）
 * 才能生效读取配置文件ConfigurationProperties这个注解
 * 具体可以ctrl+left click本类，看那个使用了本类
 */
@ConfigurationProperties(prefix = "appserver.jpa")
public class JpaProperties {

    private boolean enabled = false;
    private boolean enableTransaction = false;
}

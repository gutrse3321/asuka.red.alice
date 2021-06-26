package red.asuka.alice.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 11:30
 * @Desc: mybatis配置属性
 */
@Data
@ConfigurationProperties(prefix = "appserver.mybatis")
public class MybatisProperties {

    private boolean enabled;
    private String typeAliasesPackage;
    private String typeHandlersPackage;
    private String mapperLocations;
}

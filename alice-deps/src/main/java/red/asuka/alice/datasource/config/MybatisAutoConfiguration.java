package red.asuka.alice.datasource.config;

import red.asuka.alice.commons.support.StringUtility;
import red.asuka.alice.datasource.jdbc.config.JdbcAutoConfiguration;
import red.asuka.alice.datasource.jpa.config.JpaAutoConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 11:32
 * @Desc:
 */
@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
//自动化配置
@ConditionalOnProperty(value = "appserver.mybatis.enabled", havingValue = "true")
@AutoConfigureAfter({JdbcAutoConfiguration.class})
@AutoConfigureBefore({JpaAutoConfiguration.class})
public class MybatisAutoConfiguration {

    private final MybatisProperties mybatisProperties;

    public MybatisAutoConfiguration(MybatisProperties mybatisProperties) {
        this.mybatisProperties = mybatisProperties;
    }

    /**
     * https://www.cnblogs.com/borter/p/9569971.html
     * SqlSessionFactory： 它是个单个数据库映射关系经过编译后的内存镜像
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        //数据源，由jdbcAutoConfiguration中注入的bean，装配到这里
        sessionFactoryBean.setDataSource(dataSource);
        //资源服务
        sessionFactoryBean.setVfs(MybatisVFS.class);

        if (StringUtility.hasText(mybatisProperties.getMapperLocations())) {
            List<Resource> resourceList = new ArrayList<>();
            String[] split = mybatisProperties.getMapperLocations().split(",");
            //获取Spring资源解析器
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            //根据mapper的地址获取资源信息，添加到上面的list中
            for (String s : split) {
                Collections.addAll(resourceList, resolver.getResources(s));
            }

            //设置mapper位置
            sessionFactoryBean.setMapperLocations(resourceList.toArray(new Resource[resourceList.size()]));
        }

        //设置别名包
        if (StringUtility.hasText(mybatisProperties.getTypeAliasesPackage())) {
            sessionFactoryBean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        }

        //设置实际包名
        if (StringUtility.hasText(mybatisProperties.getTypeHandlersPackage())) {
            sessionFactoryBean.setTypeHandlersPackage(mybatisProperties.getTypeHandlersPackage());
        }

        //注入的实例
        return sessionFactoryBean.getObject();
    }
}

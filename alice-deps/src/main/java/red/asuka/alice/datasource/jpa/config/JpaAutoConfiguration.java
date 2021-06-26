package red.asuka.alice.datasource.jpa.config;

import red.asuka.alice.datasource.jdbc.config.JdbcAutoConfiguration;
import red.asuka.alice.datasource.jpa.bean.RepositoryFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/11/8 14:27
 * @Desc: JPA配置与注入
 */
@Configuration
//@EnableConfigurationProperties注解的作用是：使使用 @ConfigurationProperties 注解的类生效。
@EnableConfigurationProperties(JpaProperties.class)
//自定配置
@ConditionalOnProperty(value = "appserver.jpa.enabled", havingValue = "true")
//将一个配置类在另一个配置类之后加载
//JdbcAutoConfiguration配置类加载完再加载JpaAutoConfiguration配置类
@AutoConfigureAfter({JdbcAutoConfiguration.class})
@EnableJpaRepositories(basePackages = {"red.asuka.alice.persist.repository"}, repositoryFactoryBeanClass = RepositoryFactoryBean.class)
//开启事务管理
@EnableTransactionManagement
public class JpaAutoConfiguration {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        /**
         * HibernateJpaVendorAdapter
         * 公开Hibernate的持久性提供程序和继承EntityManager接口，
         * 并适应AbstractJpaVendorAdapter的通用配置设置。
         */
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true); //设置是否在日志（或控制台）中显示SQL
        hibernateJpaVendorAdapter.setGenerateDdl(false); //设置是否在初始化EntityManagerFactory后创建/更新所有相关表来生成DDL
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL); //指定要操作的目标数据库，作为Database枚举
        return hibernateJpaVendorAdapter;
    }

    /**
     * 配置实体包、jpa属性映射加入到spring的bean管理工厂中
     * @param builder
     * @param dataSource
     * @return
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean managerFactoryBean(EntityManagerFactoryBuilder builder, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManager = builder.dataSource(dataSource)
                .packages("red.asuka.alice.server.persist.entity") //扫描@Entity实体
                .persistenceUnit("entityManager")
                .build();
        entityManager.setJpaVendorAdapter(jpaVendorAdapter());
        Map<String, String> map = new HashMap<>();
        map.put("javax.persistence.validation.mode", "none");
        entityManager.setJpaPropertyMap(map);
        return entityManager;
    }

    /**
     * 开启事务
     * PlatformTransactionManager 事务管理器
     * @param managerFactoryBean
     * @return
     */
    @Bean(name = "transactionManager")
    //自动化配置
    @ConditionalOnProperty(value = "appserver.jpa.enable-transaction", havingValue = "true")
    public PlatformTransactionManager businessTransactionManager(LocalContainerEntityManagerFactoryBean managerFactoryBean) {
        return new JpaTransactionManager(managerFactoryBean.getObject());
    }
}

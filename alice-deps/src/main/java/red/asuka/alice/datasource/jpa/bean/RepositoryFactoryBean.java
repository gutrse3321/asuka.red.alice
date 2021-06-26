package red.asuka.alice.datasource.jpa.bean;

import red.asuka.alice.datasource.jpa.config.RepositoryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @Author: Tomonori
 * @Date: 2019/11/11 18:51
 * @Desc: JPA Repository 工厂Bean
 *        在JpaAutoConfiguration类中注入 @EnableJpaRepositories
 */
public class RepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable> extends JpaRepositoryFactoryBean<T, S, ID> {

    /**
     * 为给定的仓储(Repository)接口创建一个新的{@link JpaRepositoryFactoryBean}工厂 Bean
     * @param repositoryInterface 必须不为{@literal null}
     */
    public RepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * 仓储工厂实例
     * @param entityManager
     * @return
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new RepositoryFactory(entityManager);
    }
}

package red.asuka.alice.datasource.jpa.config;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * @Author: Tomonori
 * @Date: 2019/11/8 17:06
 * @Desc: JPA Repository 工厂
 */
public class RepositoryFactory extends JpaRepositoryFactory {

    public RepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleRepository.class;
    }
}

package red.asuka.alice.datasource.jpa.extension;

import red.asuka.alice.datasource.jpa.config.JpaEntityPropertyUtility;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 10:28
 * @Desc: Entity 自定义主键值
 * 使用： 在实体主键字段上和@Id注解一并使用
 * @GenericGenerator(name = "default", strategy = "red.asuka.alice.datasource.jpa.extension.WithoutGenerator")
 * IdentifierGenerator doc: https://docs.jboss.org/hibernate/orm/3.5/api/org/hibernate/id/IdentifierGenerator.html
 * 文档中也有说要和Configurable一起被实现
 */
public class WithoutGenerator implements IdentifierGenerator, Configurable {

    private static final Logger log = LoggerFactory.getLogger(WithoutGenerator.class);

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {

    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        try {
            return (Serializable) JpaEntityPropertyUtility.getValueByAnnotationId(o);
        } catch (Exception e) {
            log.error("Entity primary key must not null");
            return null;
        }
    }
}

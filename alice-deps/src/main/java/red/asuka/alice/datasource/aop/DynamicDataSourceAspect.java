package red.asuka.alice.datasource.aop;

import red.asuka.alice.datasource.jdbc.annotiation.DataSource;
import red.asuka.alice.datasource.jdbc.multiData.DynamicDataSourceRouter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

/**
 * @Author: Tomonori
 * @Date: 2019/11/8 12:02
 * @Desc: 数据源切换 JdbcAutoConfiguration中注入Bean
 */
@Aspect
@Order(3) //执行顺序
public class DynamicDataSourceAspect {

    @Autowired
    private DynamicDataSourceRouter dataSourceRouter;

    @Around("@annotation(dataSource)")
    public Object around(ProceedingJoinPoint pjp, DataSource dataSource) throws Throwable {
        dataSourceRouter.setDataSource(dataSource.key());
        try {
            return pjp.proceed();
        } finally {
            dataSourceRouter.clearDataSource();
        }
    }
}

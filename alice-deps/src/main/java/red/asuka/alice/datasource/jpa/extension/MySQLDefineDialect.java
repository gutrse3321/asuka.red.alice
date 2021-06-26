package red.asuka.alice.datasource.jpa.extension;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 10:18
 * @Desc: 使用Criteria查询时，查询语句中如果使用了自定义函数需要配置Hibernate.dialect为
 *        此类添加自定义函数方式如下
 *        registerFunction()
 *        在spring配置文件中写上带有完整包+类的地址配置
 */
public class MySQLDefineDialect extends MySQL5Dialect {

    public MySQLDefineDialect() {
        super();
//        registerFunction();
    }
}

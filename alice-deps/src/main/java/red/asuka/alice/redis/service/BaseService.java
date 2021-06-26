package red.asuka.alice.redis.service;

import com.fasterxml.jackson.databind.JsonNode;
import red.asuka.alice.commons.support.JsonUtility;
import red.asuka.alice.commons.support.SpringContextUtility;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @Author: Tomonori
 * @Date: 2019/12/9 16:25
 * @Title: 获取已初始化的Bean
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public abstract class BaseService<S> {

    protected S proxy;
    @Autowired
    protected SpringContextUtility springContextUtility;

    /**
     * @PostConstruct: 修饰非静态的void方法
     * PostConstruct注解的方法执行顺序： 构造方法 > @Autowired > @PostConstruct
     */
    @PostConstruct
    protected void post() {
        //获取继承此抽象类所实现的接口信息 eg: interface red.asuka.alice.service.IUserService
        Class<?>[] interfaces = this.getClass().getInterfaces();
        if (null == interfaces || interfaces.length == 0) return;
        //赋值已初始化的Bean
        proxy = (S) springContextUtility.getBean(interfaces[0]);
    }

    protected JsonNode getNodeList(String json) throws Exception {
        JsonNode node = JsonUtility.readTree(json);
        return node.get("list");
    }
}

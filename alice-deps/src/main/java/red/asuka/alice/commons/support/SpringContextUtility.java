package red.asuka.alice.commons.support;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: Tomonori
 * @Date: 2019/12/9 16:26
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Component
public class SpringContextUtility implements ApplicationContextAware {

    /**
     * 从ApplicationContextAware获取ApplicationContext上下文的情况，
     * 仅仅适用于当前运行的代码和已启动的Spring代码处于同一个Spring上下文，
     * 否则获取到的ApplicationContext是空
     */
    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext application) throws BeansException {
        applicationContext = application;
    }

    /**
     * 获取已初始化的Bean
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}

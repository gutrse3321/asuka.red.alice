package red.asuka.alice.commons.http.api.config;

import red.asuka.alice.commons.http.api.annotation.Api;
import red.asuka.alice.commons.http.api.annotation.ApiScan;
import red.asuka.alice.commons.support.ReflectionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.util.Set;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 14:33
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * ----- 借助ImportBeanDefinitionRegistrar接口实现bean的动态注入
 * ----- ApiRegister将在@ApiScan在HttpClientConfiguration配置类中注解的时候@Import注册到Bean
 */
public class ApiRegister implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(ApiRegister.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        /**
         * AnnotationAttributes: 注解的解析转换，通常对注解的解析之后，需要对注解的信息进行对象存储转换
         * docs: https://www.jianshu.com/p/9fb8dfb02461
         * fromMap： 根据给定的一个map返回一个注解的解析转换实例
         *
         * AnnotationMetadata.getAnnotationAttributes获取注解的属性方法，返回一个Map<String, Object>
         */
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(ApiScan.class.getName()));
        String scanPackage = annoAttrs.getString("value");

        try {
            /**
             * loadClassesByAnnotationClass: 传入一个注解类型类，获取使用这个注解的所有的类型类
             * Metadata 元数据相关doc: https://www.jianshu.com/p/83725adc2d45
             */
            Set<Class<?>> classes = ReflectionUtility.loadClassesByAnnotationClass(Api.class, scanPackage.split(","));

            classes.forEach(aClass -> {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setSynthetic(true);
                beanDefinition.setBeanClass(aClass);
                //注册
                beanDefinitionRegistry.registerBeanDefinition(aClass.getSimpleName(), beanDefinition);
            });
        } catch (IOException e) {
            log.error("Api register error");
        } catch (ClassNotFoundException e) {
            log.error("Api register error");
        }
    }
}

package red.asuka.alice.commons.exception;

import red.asuka.alice.commons.exception.annotation.ExpTranslation;
import red.asuka.alice.commons.exception.annotation.TranslationScan;
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
 * @Date: 2020/1/7 14:31
 * @Title: 注册Translation类
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * ----- 借助ImportBeanDefinitionRegistrar接口实现bean的动态注入
 */
public class TranslationRegister implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(TranslationRegister.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        /**
         * AnnotationAttributes: 注解的解析转换，通常对注解的解析之后，需要对注解的信息进行对象存储转换
         * docs: https://www.jianshu.com/p/9fb8dfb02461
         * fromMap： 根据给定的一个map返回一个注解的解析转换实例
         *
         * AnnotationMetadata.getAnnotationAttributes获取注解的属性方法，返回一个Map<String, Object>
         */
        AnnotationAttributes annotationAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(TranslationScan.class.getName()));
        String values = annotationAttrs.getString("value");

        try {
            Set<Class<?>> classes = ReflectionUtility.loadClassesByAnnotationClass(ExpTranslation.class, values.split(","));

            classes.forEach(aClass -> {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setSynthetic(true);
                beanDefinition.setBeanClass(aClass);

                registry.registerBeanDefinition(aClass.getSimpleName(), beanDefinition);
            });
        } catch (IOException e) {
            log.error("Exception translation register error", e);
        } catch (ClassNotFoundException e) {
            log.error("Exception translation register error", e);
        }
    }
}

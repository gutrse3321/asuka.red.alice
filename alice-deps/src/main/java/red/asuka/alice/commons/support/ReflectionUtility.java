package red.asuka.alice.commons.support;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Tomonori
 * @Date: 2019/11/5 18:20
 * @Desc: 反射工具类
 */
@UtilityClass
public class ReflectionUtility extends ReflectionUtils {

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private final Logger log = LoggerFactory.getLogger(ReflectionUtility.class);

    /**
     * 获取对象的所有属性，包括除了public以外的
     * @param objectClass
     * @param local
     * @return
     */
    public List<Field> getFields(Class<?> objectClass, boolean local) {
        List<Field> fields = new ArrayList<>();

        /**
         * 得到objectClass上的所有属性遍历，执行fields::add，add到声明的变量fields数组中
         * 可以看两个doWith的源码都是getDeclaredField 获取一个类所有的属性字段
         * 区别第三个参数进行过滤
         */
        if (local) {
            doWithLocalFields(objectClass, fields::add);
        } else {
            //过滤
            doWithFields(objectClass, fields::add, null);
        }

        return fields;
    }

    /**
     * 注解对象使用
     * @param objectClass
     * @param annotationClass
     * @param local
     * @param <A>
     * @return
     */
    public <A extends Annotation> Field[] getFields(Class<?> objectClass, Class<A> annotationClass, boolean local) {
        return getFields(objectClass, false).stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
    }

    public <A extends Annotation> Field getField(Class<?> objectClass, Class<A> annotationClass, boolean local, boolean requireUnique) {
        Field[] fields = getFields(objectClass, annotationClass, local);

        if (fields.length == 1) {
            return fields[0];
        } else if (fields.length > 1 && requireUnique) {
            //TODO 需要在此抛出异常
        }

        return null;
    }

    public List<Method> getMethods(Class<?> objectClass, boolean local) {
        List<Method> methods = new ArrayList<>();

        /**
         * 两个dowith都是用的getDeclaredMethods 获取某个类的所有方法，包括除了public的其他修饰符
         * 遍历所有的方法，执行methods::add 添加到methods数组中
         */
        if (local) {
            doWithLocalMethods(objectClass, methods::add);
        } else {
            doWithMethods(objectClass, methods::add, null);
        }
        return methods;
    }

    public <A extends Annotation> Method[] getMethods(Class<?> objectClass, Class<A> annotationClass, boolean local) {
        return getMethods(objectClass, false).stream()
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .toArray(Method[]::new);
    }

    /**
     * 传入一个注解类型类，获取使用这个注解的所有的类型类
     * Metadata 元数据相关doc: https://www.jianshu.com/p/83725adc2d45
     * @param annotationClass
     * @param packageNames
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Set<Class<?>> loadClassesByAnnotationClass(Class<? extends Annotation> annotationClass, String... packageNames) throws IOException, ClassNotFoundException {
        String annotationClassName = annotationClass.getName();
        //获取Spring资源解析器
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        /**
         * CachingMetadataReaderFactory：
         *     缓存MetadataReaderFactory接口的实现，
         *     MetadataReader每个Spring Resource
         *     句柄(即每个".class"文件)缓存一个实例
         */
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            //获取包路径的资源信息数组
            /**
             * ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX: "classpath*:"
             */
            String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                    "/" + "**/*.class";
            Resource[] resources = patternResolver.getResources(locationPattern);

            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }

                //获取元数据模型
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                //获取此类的注解元数据
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                if (!annotationMetadata.hasAnnotation(annotationClassName)) {
                    continue;
                }

                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> loadedClass = ClassUtils.forName(className, null);
                classes.add(loadedClass);
            }
        }
        return classes;
    }

    /**
     * 传入一个注解类型类数组，获取使用这个注解的所有的类型类
     * Metadata 元数据相关doc: https://www.jianshu.com/p/83725adc2d45
     * @param annotationClass
     * @param packageNames
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Set<Class<?>> loadClassesByAnnotationClass(Class<? extends Annotation>[] annotationClass, String... packageNames) throws IOException, ClassNotFoundException {
        //获取Spring资源解析器
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        /**
         * CachingMetadataReaderFactory：
         *     缓存MetadataReaderFactory接口的实现，
         *     MetadataReader每个Spring Resource
         *     句柄(即每个".class"文件)缓存一个实例
         */
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            //获取包路径的资源信息数组
            String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                    "/" + "**/*.class";
            Resource[] resources = patternResolver.getResources(locationPattern);

            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }

                //获取元数据模型
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                //获取此类的注解元数据
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                //判断一个注解数组传参，如果是有这个注解的就返回这个类
                boolean flag = false;
                for (Class<? extends Annotation> aClass : annotationClass) {
                    String name = aClass.getName();
                    if (annotationMetadata.hasAnnotation(name)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) continue;

                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> loadedClass = ClassUtils.forName(className, null);
                classes.add(loadedClass);
            }
        }
        return classes;
    }

}

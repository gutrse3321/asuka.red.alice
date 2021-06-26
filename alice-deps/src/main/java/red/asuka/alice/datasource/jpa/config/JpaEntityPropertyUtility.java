package red.asuka.alice.datasource.jpa.config;

import red.asuka.alice.commons.model.EntityPropertyInfo;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Tomonori
 * @Date: 2019/11/11 16:45
 * @Desc: Jpa对象属性工具类
 */
@UtilityClass
public class JpaEntityPropertyUtility extends BeanUtils {

    protected static final Logger log = LoggerFactory.getLogger(JpaEntityPropertyUtility.class);

    /**
     * PropertyDescriptor 属性描述器
     * 属性描述符描述了一个属性，即Java Bean 通过一对访问器方法来导出
     */
    private final Map<Class<?>, PropertyDescriptor[]> cacheBeanPropertyDes = new ConcurrentHashMap<>();
    private final Map<Class<?>, PropertyDescriptor[]> cacheEntityPropertyDes = new ConcurrentHashMap<>();
    private final List<String> excludes = new ArrayList<>();

    private final List<Class<?>> notNullProperty = new ArrayList<>();
    private final List<Class<?>> allProperty = new ArrayList<>();
    private final Map<Class<?>, EntityPropertyInfo> cacheNotNullPropertyInfo = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityPropertyInfo> cacheAllPropertyInfo = new ConcurrentHashMap<>();
    private static Map<Class<?>, PropertyDescriptor[]> cacheBeanProDes = new ConcurrentHashMap<>();

    static {
        excludes.add("class");

        notNullProperty.add(Integer.class);
        notNullProperty.add(Short.class);
        notNullProperty.add(Byte.class);
        notNullProperty.add(Float.class);
        notNullProperty.add(Double.class);
        notNullProperty.add(Long.class);
        notNullProperty.add(Boolean.class);
        notNullProperty.add(Character.class);
        notNullProperty.add(String.class);
        notNullProperty.add(Timestamp.class);
        notNullProperty.add(java.sql.Date.class);
        notNullProperty.add(Time.class);
        notNullProperty.add(BigDecimal.class);

        allProperty.add(int.class);
        allProperty.add(Integer.class);
        allProperty.add(short.class);
        allProperty.add(Short.class);
        allProperty.add(byte.class);
        allProperty.add(Byte.class);
        allProperty.add(float.class);
        allProperty.add(Float.class);
        allProperty.add(double.class);
        allProperty.add(Double.class);
        allProperty.add(long.class);
        allProperty.add(Long.class);
        allProperty.add(boolean.class);
        allProperty.add(Boolean.class);
        allProperty.add(char.class);
        allProperty.add(Character.class);
        allProperty.add(String.class);
        allProperty.add(Timestamp.class);
        allProperty.add(java.sql.Date.class);
        allProperty.add(Time.class);
        allProperty.add(BigDecimal.class);
    }

    /**
     * 获取对象属性信息
     * @param clazz
     * @param all 所有字段或非空字段
     * @return
     */
    public EntityPropertyInfo getProperty(Class<?> clazz, boolean all) {
        Assert.notNull(clazz, "Class required");

        //根据all判断去过去含所有字段的缓存map或非空字段的map
        EntityPropertyInfo entityPropertyInfo = all ? cacheAllPropertyInfo.get(clazz) : cacheNotNullPropertyInfo.get(clazz);
        if (null != entityPropertyInfo) return entityPropertyInfo;

        //如果缓存map没有，就去获取这个类的属性，并添加到cacheEntityPropertyDes 缓存实体描述器map中
        entityPropertyInfo = getEntityPropertyInfo(clazz, all ? allProperty : notNullProperty);
        //加入到缓存map中
        if (all) {
            cacheAllPropertyInfo.put(clazz, entityPropertyInfo);
        } else {
            cacheNotNullPropertyInfo.put(clazz, entityPropertyInfo);
        }

        return entityPropertyInfo;
    }

    /**
     * 获取对象指定属性字段信息
     * @param clazz
     * @param include
     * @return
     */
    public EntityPropertyInfo getEntityPropertyInfo(Class<?> clazz, List<Class<?>> include) {
        Assert.notNull(clazz, "class required");

        if (null == include) include = new ArrayList<>();

        PropertyDescriptor primaryKey = null;
        List<PropertyDescriptor> otherKeys = new ArrayList<>();
        //获取Entity对象所有属性
        PropertyDescriptor[] propertyDescriptors = getEntityPropertyDescriptor(clazz);

        for (PropertyDescriptor descriptor : propertyDescriptors) {
            if (checkAnnotation(clazz, descriptor, Id.class) || checkAnnotation(clazz, descriptor, EmbeddedId.class)) {
                primaryKey = descriptor;
                continue;
            }
            //getPropertyType 获取属性的java类型对象
            //allProperty或notNullProperty或new ArrayList<>()
            //include包含这个属性的java类型对象的话，就加入到otherKeys数组
            if (!include.contains(descriptor.getPropertyType())) continue;
            otherKeys.add(descriptor);
        }

        //TODO 留一个标记 primaryKey 上面属性为主键注解存在的话，赋值记录到EntityPropertyInfo类
        Assert.notNull(primaryKey, clazz.getName() + " cant find the primary key annotation");
        return new EntityPropertyInfo(primaryKey, otherKeys.toArray(new PropertyDescriptor[otherKeys.size()]));
    }

    /**
     * 获取Entity对象所有属性，过滤class属性和拥有Transient注解的属性
     * @param clazz
     * @return
     */
    public PropertyDescriptor[] getEntityPropertyDescriptor(Class<?> clazz) {
        PropertyDescriptor[] propertyDescriptors = cacheEntityPropertyDes.get(clazz);

        //缓存数组中不存在的话，新put进去
        if (null == propertyDescriptors) {
            PropertyDescriptor[] propertyDesList = BeanUtils.getPropertyDescriptors(clazz);
            List<PropertyDescriptor> list = new ArrayList<>();

            for (PropertyDescriptor propertyDes : propertyDesList) {
                //过滤class属性
                if ("class".equals(propertyDes.getName())) continue;
                list.add(propertyDes);
            }

            propertyDescriptors = new PropertyDescriptor[list.size()];
            cacheEntityPropertyDes.put(clazz, list.toArray(propertyDescriptors));
        }

        return propertyDescriptors;
    }

    /**
     * 判断对象某个属性是否有某个注解
     * @param clazz           对象
     * @param proDes          属性描述器
     * @param annotationClass 需要判断的注解
     * @return
     */
    public boolean checkAnnotation(Class<?> clazz, PropertyDescriptor proDes, Class<? extends Annotation> annotationClass) {
        Assert.notNull(clazz, " Class required");
        Assert.notNull(proDes, " PropertyDescriptor required");
        Assert.notNull(annotationClass, " Annotation class required");

        //获取类属性的getter方法
        Method readMethod = proDes.getReadMethod();
        Method method;

        /**
         * getMethod 获取当前类及所有继承的父类的public修饰的方法。仅包括public
         * getDeclaredMethod 获取当前类的所有方法，包括public/private/protected/default修饰的方法
         * 传参获取指定方法
         */
        try {
            method = clazz.getMethod(readMethod.getName(), readMethod.getParameterTypes());
            //判断是否含有这个注解 annotationClass
            if (null != method && null != method.getAnnotation(annotationClass)) return true;
        } catch (NoSuchMethodException e) {
        }

        try {
            method = clazz.getDeclaredMethod(readMethod.getName(), readMethod.getParameterTypes());
            //判断是否含有这个注解 annotationClass
            if (null != method && null != method.getAnnotation(annotationClass)) return true;
        } catch (NoSuchMethodException e) {
        }

        /**
         * getField 只能获取类的public属性字段.
         * getDeclaredField 获取一个类所有的属性字段
         */
        Field field;
        try {
            field = clazz.getField(proDes.getName());
            if (null != field && null != field.getAnnotation(annotationClass)) return true;
        } catch (NoSuchFieldException e) {
        }
        try {
            field = clazz.getDeclaredField(proDes.getName());
            if (null != field && null != field.getAnnotation(annotationClass)) return true;
        } catch (NoSuchFieldException e) {
        }

        /**
         * 获取clazz继承的类
         */
        Class<?> superClass = clazz.getSuperclass();
        if (null == superClass) return false;

        //继承类的继承类 ... 继续判断
        return checkAnnotation(superClass, proDes, annotationClass);
    }

    /**
     * 获取注解为@Id的属性值
     * @param entity
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object getValueByAnnotationId(Object entity) throws InvocationTargetException, IllegalAccessException {
        Assert.notNull(entity, " entity must be not null");

        //获取此对象所有的属性信息
        EntityPropertyInfo allProperty = getProperty(entity.getClass(), true);
        Method readMethod = allProperty.getPrimaryKey().getReadMethod();
        return readMethod.invoke(entity);
    }

    /**
     * 复制非空属性
     * @param origin
     * @param target
     */
    public void copyNotNull(Object origin, Object target) {
        Assert.notNull(origin, " Origin required");
        Assert.notNull(target, " Target required");

        /**
         * 当origin类和target不一样，且target对象不是origin的实例化
         */
        if (!origin.getClass().equals(target.getClass()) && !origin.getClass().isInstance(target)) {
            /**
             * copyProperties 拷贝不会空的属性，类型和名字要相同（基本类型和装箱类可以互相拷贝）
             */
            BeanUtils.copyProperties(origin, target, getNullPropertyNames(origin));
        } else {
            //获取对象的所有属性，根据excludes列表过滤(过滤 "class")
            PropertyDescriptor[] beanPropertyDescriptor = getBeanPropertyDescriptor(origin.getClass());

            for (PropertyDescriptor descriptor : beanPropertyDescriptor) {
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                Class<?> returnType = readMethod.getReturnType();

                if (returnType.isPrimitive()) continue;
                try {
                    //执行origin的getter方法
                    Object invoke = readMethod.invoke(origin);
                    //如果不为空的话，将getter返回的值，setter到target对象的属性
                    if (null != invoke) writeMethod.invoke(target, invoke);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    /**
     * 获取对象，值为null的属性的名称数组
     * @param source
     * @return
     */
    public String[] getNullPropertyNames(Object source) {
        /**
         * BeanWrapper：
         *     Spring 的中心接口，用于访问javabeans 的低层操作。
         *     默认实现为BeanWrapperImpl 提供分析和处理标准java beans 用于get 和set 属性，取得属性描述，查询属性的读/写能力。
         */
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            //获取属性值
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                //为null的话将属性名称加入到Set集合中
                emptyNames.add(pd.getName());
            }
        }

        //转成字符串数组，返回一个值为null的属性名称数组
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 获取对象的所有属性，根据excludes列表过滤(过滤 "class")
     * @param clazz
     * @return
     */
    public PropertyDescriptor[] getBeanPropertyDescriptor(Class<?> clazz) {
        PropertyDescriptor[] propertyDescriptors = cacheBeanPropertyDes.get(clazz);
        if (null == propertyDescriptors) {
            PropertyDescriptor[] propertyDesList = BeanUtils.getPropertyDescriptors(clazz);
            List<PropertyDescriptor> list = new ArrayList<>();

            for (PropertyDescriptor propertyDes : propertyDesList) {
                if (excludes.contains(propertyDes.getName())) continue;
                list.add(propertyDes);
            }

            propertyDescriptors = new PropertyDescriptor[list.size()];
            cacheBeanPropertyDes.put(clazz, list.toArray(propertyDescriptors));
        }
        return propertyDescriptors;
    }
}

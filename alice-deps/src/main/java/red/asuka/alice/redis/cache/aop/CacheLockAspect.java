package red.asuka.alice.redis.cache.aop;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.commons.support.EntityPropertyUtility;
import red.asuka.alice.commons.support.StringUtility;
import red.asuka.alice.redis.cache.RedisCacheKeyUtils;
import red.asuka.alice.redis.cache.lock.RedisLock;
import red.asuka.alice.redis.cache.lock.annotation.CacheLock;
import red.asuka.alice.redis.cache.lock.annotation.LockedObject;
import red.asuka.alice.redis.cache.lock.exception.RedisCacheException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 15:37
 * @Desc: 缓存锁切面
 */
@Slf4j
@Aspect
@Component
@Order(4)
public class CacheLockAspect {

    /**
     * 上级目录的同目录lock包下的redis锁类
     */
    @Autowired
    RedisLock redisLock;

    @Around("@annotation(cacheLock)")
    public Object around(ProceedingJoinPoint point, CacheLock cacheLock) throws Throwable {
        //获取使用的方法的锁前缀
        String prefix = cacheLock.lockedPrefix();
        //是否有指定的方法去进行 锁
        String methodKey = cacheLock.value();
        RedisCacheKeyUtils cacheKeyUtils;

        //判断是否有用指定的方法去 锁，没有的话就使用@LockObject的注解的字段去构造
        if (StringUtility.hasLength(methodKey)) {
            cacheKeyUtils = redisLock.madeCacheKeyUtils(prefix, methodKey);
        } else {
            //获取此方法的传参
            Object[] args = point.getArgs();
            //获得当前正在执行的方法
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Annotation[][] parameterAnnotations = methodSignature.getMethod().getParameterAnnotations();
            //获取使用@LockObject注解字段的值
            Object lockObject = getLockObject(parameterAnnotations, args);

            //proceed： 走到这个切面的目标方法继续执行
            if (null == lockObject) return point.proceed();

            cacheKeyUtils = redisLock.madeCacheKeyUtils(prefix, lockObject);
        }

        //进行具体redis操作开始锁
        Boolean lock = redisLock.lock(cacheKeyUtils, cacheLock.timeOut(), cacheLock.expireTime());

        if (!lock) {
            log.info("{}_{}分布式锁获取失败", prefix, methodKey);
            //取锁失败，正在进行相同的操作
            throw EXPF.exception(ErrorCode.InProgress, true);
        }

        try {
            log.info("{}_{}分布式锁获取成功", prefix, methodKey);
            return point.proceed();
        } finally {
            //解锁操作
            redisLock.unlock(cacheKeyUtils);
        }
    }

    /**
     * 获取使用@LockObject注解字段的值
     * @param annotations
     * @param args
     * @return
     */
    private Object getLockObject(Annotation[][] annotations, Object[] args) {
        //判断是否有使用注解
        if (null == annotations || annotations.length == 0) {
            throw new RedisCacheException("Can not find @LockedObject[annotations Empty]");
        }

        //判断是否有传参
        if (null == args || args.length == 0) {
            throw new RedisCacheException("Can not find @LockedObject[args Empty]");
        }

        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                //如果类是LockedObject注解的实例
                if (annotations[i][j] instanceof LockedObject) {
                    LockedObject lockedObject = (LockedObject) annotations[i][j];
                    Object arg = args[i];
                    return getLockObject(lockedObject, arg);
                }
            }
        }
        return null;
    }

    /**
     * 获取使用注解的值
     * @param lockedObject
     * @param arg
     * @return
     */
    private Object getLockObject(LockedObject lockedObject, Object arg) {
        if (null == arg) return arg;

        Class<?> aClass = arg.getClass();

        //判断使用LockedObject注解的字段（value()的值）的类型
        if (null != lockedObject && StringUtility.hasLength(lockedObject.value())) {
            try {
                PropertyDescriptor propertyDescriptor;
                if (aClass.getName().equals("java.util.HashMap")) {
                    Map dataMap = (Map) arg;
                    return dataMap.get(lockedObject.value());
                } else {
                    propertyDescriptor = EntityPropertyUtility.getPropertyDescriptor(aClass, lockedObject.value());
                }
                Method readMethod = propertyDescriptor.getReadMethod();
                return readMethod.invoke(arg);
            } catch (Exception e) {
                throw new RedisCacheException("Can not find @LockedObject[Object Empty]");
            }
        }  else if ((aClass == long.class) || (aClass == Long.class)) {
            return arg;
        } else if ((aClass == int.class) || (aClass == Integer.class)) {
            return arg;
        } else if ((aClass == char.class) || (aClass == Character.class)) {
            return arg;
        } else if ((aClass == short.class) || (aClass == Short.class)) {
            return arg;
        } else if ((aClass == double.class) || (aClass == Double.class)) {
            return arg;
        } else if ((aClass == float.class) || (aClass == Float.class)) {
            return arg;
        } else if ((aClass == boolean.class) || (aClass == Boolean.class)) {
            return arg;
        } else if (aClass == String.class) {
            return arg;
        } else if (aClass == java.sql.Date.class) {
            return arg;
        } else if (aClass == BigDecimal.class) {
            return arg;
        } else {
            return null;
        }
    }
}

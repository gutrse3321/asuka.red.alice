package red.asuka.alice.redis.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import red.asuka.alice.commons.support.ReflectionUtility;
import red.asuka.alice.redis.cache.annotation.CacheConfigList;
import red.asuka.alice.redis.cache.annotation.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 15:09
 * @Desc: redis缓存配置类
 */
@Configuration
@EnableConfigurationProperties({RedisProperties.class, RedisCacheProperties.class})
/**
 * https://segmentfault.com/a/1190000011069802
 *
 * -@EnableCaching注解是spring framework中的注解驱动的缓存管理功能。
 * 自spring版本3.1起加入了该注解。如果你使用了这个注解，那么你就不需要在XML文件中配置cache manager了。
 *
 * 当你在配置类(@Configuration)上使用@EnableCaching注解时，会触发一个post processor，这会扫描每一
 * 个spring bean，查看是否已经存在注解对应的缓存。
 * 如果找到了，就会自动创建一个代理拦截方法调用，使用缓存的bean执行处理。
 */
@EnableCaching
public class RedisCacheConfiguration extends CachingConfigurerSupport {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfiguration.class);

    private RedisConnectionFactory connectionFactory;
    //autoconfigure.data.redis包的redis属性信息
    private RedisProperties properties;
    //自定义的部分redis属性信息
    private RedisCacheProperties redisCacheProperties;
    private Map<String, Long> expires;
    private Set<String> nameSpaces;

    /**
     * 2.x 版本的包，JedisConnectionFactory bean的装配的是在org.springframework.boot.autoconfigure.data.redis包的
     * JedisConnectionConfiguration类中注入的
     * TODO 不知道有没有装配成功，idea没有找到对应的bean，看源码的确是注入了的
     * @param factory
     * @param properties
     * @param redisCacheProperties
     */
    public RedisCacheConfiguration(RedisConnectionFactory factory, RedisProperties properties, RedisCacheProperties redisCacheProperties) {
        this.connectionFactory = factory;
        this.properties = properties;
        this.redisCacheProperties = redisCacheProperties;
        init();
    }

    public void init() {
        try {
            String[] packagesToScan = redisCacheProperties.getPackagesToScan() != null ? redisCacheProperties.getPackagesToScan().split(",") : new String[]{""};

            //获取使用这两个注解的所有的类型类
            Set<Class<?>> classes = ReflectionUtility.loadClassesByAnnotationClass(
                    new Class[]{CacheConfiguration.class, CacheConfigList.class},
                    packagesToScan
            );

            if (!CollectionUtils.isEmpty(classes)) {
                expires = new HashMap<>();
                nameSpaces = new HashSet<>();
            }

            classes.forEach(aClass -> {
                //获取aClass类型类的指定注解的值
                CacheConfiguration configuration = AnnotationUtils.findAnnotation(aClass, CacheConfiguration.class);
                CacheConfigList configurationList = AnnotationUtils.findAnnotation(aClass, CacheConfigList.class);

                //过期时间和命名空间设置
                /**
                 * 刷新各自的命名空间 @EnableCaching
                 */
                if (null != configuration) {
                    long expTime = configuration.expTime() == 0 ? redisCacheProperties.getExpiredTime() : configuration.expTime();
                    expires.put(configuration.nameSpace(), expTime);
                    nameSpaces.add(configuration.nameSpace());
                }

                if (null != configurationList) {
                    CacheConfiguration[] values = configurationList.value();
                    for (CacheConfiguration config : values) {
                        long expTime = config.expTime() == 0 ? redisCacheProperties.getExpiredTime() : config.expTime();
                        expires.put(config.nameSpace(), expTime);
                        nameSpaces.add(config.nameSpace());
                    }
                }
            });
        } catch (IOException e) {
            log.error("RedisCache init error", e);
        } catch (ClassNotFoundException e) {
            log.error("RedisCache init error", e);
        }
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        GenericToStringSerializer toStringSerializer = new GenericToStringSerializer(Object.class);
        template.setKeySerializer(toStringSerializer);
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate2(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //默认配置
        org.springframework.data.redis.cache.RedisCacheConfiguration defaultCacheConfiguration = org.springframework.data.redis.cache.RedisCacheConfiguration
                .defaultCacheConfig()
                .computePrefixWith(name -> name + ":");

        //各个命名空间的配置
        Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> configMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(expires)) {
            expires.forEach((key, value) -> {
                org.springframework.data.redis.cache.RedisCacheConfiguration userCacheConfiguration = org.springframework.data.redis.cache.RedisCacheConfiguration
                        .defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(value))
                        .computePrefixWith(name -> name + ":");
                configMap.put(key, userCacheConfiguration);
            });
        }

        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfiguration, configMap);
        return cacheManager;
    }

    //1.5.x写法
//    @Override
//    @Bean
//    public CacheManager cacheManager() {
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
//        cacheManager.setUsePrefix(true);
//        if (!CollectionUtils.isEmpty(nameSpaces))
//            cacheManager.setCacheNames(nameSpaces);
//        if (!CollectionUtils.isEmpty(expires))
//            cacheManager.setExpires(expires);
//        return cacheManager;
//    }
}

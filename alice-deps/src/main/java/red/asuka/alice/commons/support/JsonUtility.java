package red.asuka.alice.commons.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import red.asuka.alice.commons.jacksonExtension.UnifyAnnotationIntrospector;
import red.asuka.alice.commons.jacksonExtension.UnifyFilterProvider;
import red.asuka.alice.commons.model.PropertyFilterInfo;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author: Tomonori
 * @Mail: gutrse3321@live.com
 * @Date: 2020-09-20 下午7:58
 *
 * Json处理工具类 自定义处理序列化和反序列化
 */
@UtilityClass
public class JsonUtility {

    private final Logger log = LoggerFactory.getLogger(JsonUtility.class);

    public final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance(); //声明默认工厂，可以使用他的方法进行前置类型转换
    public final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); //使用ObjectMapper进行读写 readValue API 是简单的入门起点：可以解析或反序列化json内容为java对象。 反之，writeValue API可以序列化任何java对象为json字符串
    public final UnifyFilterProvider DEFAULT_FILTER_PROVIDER = new UnifyFilterProvider(); //Jackson统一属性过滤器提供者
    public final String[] DEFAULT_EXCLUDE_PROPERTIES = {"class"};

    static{
        SimpleModule module = new SimpleModule("JsonUtility", new Version(1,0,0,"SNAPSHOT","red.asuka.alice","reimu-akari-commons"));
        //自定义类型序列化器。这些类就在本类下面
        module.addSerializer(float.class, new FloatSerializer());
        module.addSerializer(Float.class, new FloatSerializer());
        module.addSerializer(double.class, new DoubleSerializer());
        module.addSerializer(Double.class, new DoubleSerializer());

        //反序列化的时候如果多了其他属性,不抛出异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 过滤不需要的字段
        //如果序列化的对象中含有枚举类型，则此枚举类型序列化的时候用枚举值的ordinal()输出
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);       // 枚举值ordinal()输出
        //列如允许反斜杆等字符
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);    // 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        //忽略字段大小写
        OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        //属性为 字符串空("") 或者为 null值 都不序列化
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        /**
         * 继承Jackson注解查询，重写findFilterId方法。
         * 当查询FilterId时，先获取JsonFilter注解key，如果没有获取到
         * 直接使用对象Class当做FilterId使用
         */
        OBJECT_MAPPER.setAnnotationIntrospector(new UnifyAnnotationIntrospector());
        DEFAULT_FILTER_PROVIDER.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(DEFAULT_EXCLUDE_PROPERTIES));
    }

    /**
     *
     * toString
     *
     */
    public String toString(Object object) throws Exception {
        try {
            return OBJECT_MAPPER.writer(DEFAULT_FILTER_PROVIDER).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("Convert [{} {}] to string failed, e: {}",object != null ? object.getClass() : "null", object, e);
            throw e;
        }
    }

    public String toString(Object object, PropertyFilterInfo... filters) throws Exception {
        UnifyFilterProvider provider = createProvider(filters);
        try {
            return OBJECT_MAPPER.writer(provider).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("e: {} Convert [{} {}] to string failed", e, object != null ? object.getClass() : "null", object);
            throw e;
        }
    }

    public <T> T toObject(String json,Class<T> objectClass) throws Exception {
        JavaType javaType = TYPE_FACTORY.constructType(objectClass);
        try {
            return OBJECT_MAPPER.readValue(json,javaType);
        } catch (IOException e) {
            log.warn(e + " Convert [{}] to object [{}] failed", json, objectClass);
            throw e;
        }
    }

    /**
     *
     * toCollection
     *
     */
    public <E,C extends Collection<E>> C toCollection(String json, Class<C> collectionClass, Class<E> elementClass) throws Exception {
        CollectionLikeType collectionLikeType = TYPE_FACTORY.constructCollectionLikeType(collectionClass, elementClass);
        try {
            return OBJECT_MAPPER.readValue(json,collectionLikeType);
        } catch (IOException e) {
            log.warn(e + " Convert [{}] to collection [{}<{}>] failed", json, collectionClass, elementClass);
            throw e;
        }
    }

    /**
     *
     * toArrayList
     *
     */
    public <E> ArrayList<E> toArrayList(String json, Class<E> elementClass) throws Exception {
        return toCollection(json, ArrayList.class, elementClass);
    }

    /**
     *
     * toMap
     *
     */
    public <K, V> HashMap<K, V> toMap(String json) throws Exception {
        return toMap(json, HashMap.class, String.class, Object.class);
    }

    public <K,V,M extends Map<K,V>> M toMap(String json, Class<M> mapClass, Class<K> keyClass, Class<V> valueClass) throws Exception {
        MapType mapType = TYPE_FACTORY.constructMapType(mapClass, keyClass, valueClass);
        try {
            return OBJECT_MAPPER.readValue(json,mapType);
        } catch (IOException e) {
            log.warn(e + " Convert [{}] to map [{}<{}, {}>] failed", json, mapClass, keyClass, valueClass);
            throw e;
        }
    }

    public JsonNode readTree(String json) throws Exception {
        try {
            return JsonUtility.OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            log.warn(e+"Convert [{}] to jsonNode failed", json);
            //throw EXPF.exception(ErrorCode.JsonDeserializer,true);
            throw e;
        }
    }

    /**
     *
     * methods
     *
     */
    public <T> T[] addAll(final T[] array1, final T... array2) {
        if (array1 == null) {
            return null == array2 ? null : array2.clone();
        } else if (array2 == null) {
            return null == array1 ? null : array1.clone();
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (final ArrayStoreException ase) {
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
            }
            throw ase;
        }
        return joinedArray;
    }

    public UnifyFilterProvider createProvider(PropertyFilterInfo[] filters) {
        List<String> include = new ArrayList<>();
        List<String> exclude = new ArrayList<>();
        List<PropertyFilterInfo> classFilters = new ArrayList<>();
        UnifyFilterProvider provider = new UnifyFilterProvider();
        for (PropertyFilterInfo filterInfo : filters) {
            Class<?> clazz = filterInfo.get_clazz();
            boolean includeFlag = filterInfo.is_include();
            if (null != clazz) {
                classFilters.add(filterInfo);
            } else {
                if (includeFlag) {
                    include.addAll(CollectionUtils.arrayToList(filterInfo.get_properties()));
                } else {
                    exclude.addAll(CollectionUtils.arrayToList(filterInfo.get_properties()));
                }
            }
        }

        String[] includeNames = include.toArray(new String[include.size()]);
        String[] excludeNames = exclude.toArray(new String[exclude.size()]);

        for (PropertyFilterInfo filterInfo : classFilters) {
            boolean includeFlag = filterInfo.is_include();
            if (includeFlag) {
                provider.addFilter(filterInfo.get_clazz(), SimpleBeanPropertyFilter.filterOutAllExcept(addAll(filterInfo.get_properties(), includeNames)));
            } else {
                provider.addFilter(filterInfo.get_clazz(), SimpleBeanPropertyFilter.serializeAllExcept(addAll(addAll(filterInfo.get_properties(), excludeNames), DEFAULT_EXCLUDE_PROPERTIES)));
            }
        }

        PropertyFilter defaultFilter;
        // 如果包含全局包含字段，直接设置默认provider为FilterOut
        if (!CollectionUtils.isEmpty(include)) {
            defaultFilter = SimpleBeanPropertyFilter.filterOutAllExcept(include.toArray(new String[include.size()]));
        } else {
            String[] strings = addAll(exclude.toArray(new String[exclude.size()]), DEFAULT_EXCLUDE_PROPERTIES);
            defaultFilter = SimpleBeanPropertyFilter.serializeAllExcept(strings);
        }

        provider.setDefaultFilter(defaultFilter);
        return provider;
    }

    /**
     * 单精度值序列化器，下面有转换成Decimal类型，防止丢失精度
     */
    private static class FloatSerializer extends com.fasterxml.jackson.databind.ser.std.StdSerializer<Float> {

        // fields
        private static final DecimalFormat formatter = new DecimalFormat();

        /**
         *
         */
        public FloatSerializer() {
            super(Float.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(Float value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (Float.isNaN(value) || Float.isInfinite(value)) {
                generator.writeNumber(0);
            } else {
                generator.writeRawValue(formatter.format(value).replace(",", ""));
            }
        }
    }

    /**
     * 双精度序列化器
     */
    private static class DoubleSerializer extends com.fasterxml.jackson.databind.ser.std.StdSerializer<Double> {

        // fields
        private static final DecimalFormat formatter = new DecimalFormat();

        /**
         *
         */
        public DoubleSerializer() {
            super(Double.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(Double value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                generator.writeNumber(0);
            } else {
                generator.writeRawValue(formatter.format(value).replace(",", ""));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
            return createSchemaNode("number", true);
        }
    }
}

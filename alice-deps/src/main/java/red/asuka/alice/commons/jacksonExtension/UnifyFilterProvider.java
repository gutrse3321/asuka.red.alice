package red.asuka.alice.commons.jacksonExtension;

import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 17:58
 * @Desc: Jackson统一属性过滤器提供者
 */
public class UnifyFilterProvider extends FilterProvider implements Serializable {

    /**
     * @JsonFilter设置的ID
     */
    protected Map<String, PropertyFilter> _filtersById;
    /**
     * 没有设置@JsonFilter，直接使用Class作为Key
     */
    protected Map<Class<?>, PropertyFilter> _filtersByClass;

    protected PropertyFilter _defaultFilter;

    protected boolean _cfgFailOnUnknownId = true;

    public UnifyFilterProvider() {
        this(new HashMap<>(), new HashMap<>());
    }

    public UnifyFilterProvider(Map<String, ?> idMapping, Map<Class<?>, ?> classMapping) {
        if (null == idMapping) idMapping = new HashMap<>();
        if (null == classMapping) classMapping = new HashMap<>();

        for (Object ob : idMapping.values()) {
            if (!(ob instanceof PropertyFilter)) {
                _filtersById = _convertString(idMapping);
                break;
            }
        }

        if (null == _filtersById) {
            _filtersById = (Map<String, PropertyFilter>) idMapping;
        }

        for (Object ob : classMapping.values()) {
            if (!(ob instanceof PropertyFilter)) {
                _filtersByClass = _convertClass(classMapping);
                return;
            }
        }

        _filtersByClass = (Map<Class<?>, PropertyFilter>) classMapping;
    }

    private final static Map<String, PropertyFilter> _convertString(Map<String, ?> filters) {
        HashMap<String, PropertyFilter> result = new HashMap<>();
        for (Map.Entry<String, ?> entry : filters.entrySet()) {
            Object f = entry.getValue();
            if (f instanceof PropertyFilter) {
                result.put(entry.getKey(), (PropertyFilter) f);
            } else if (f instanceof BeanPropertyFilter) {
                result.put(entry.getKey(), _convert((BeanPropertyFilter) f));
            } else {
                throw new IllegalArgumentException("Unrecognized filter type (" + f.getClass().getName() + ")");
            }
        }
        return result;
    }

    private final static Map<Class<?>, PropertyFilter> _convertClass(Map<Class<?>, ?> filters) {
        HashMap<Class<?>, PropertyFilter> result = new HashMap<>();
        for (Map.Entry<Class<?>, ?> entry : filters.entrySet()) {
            Object f = entry.getValue();
            if (f instanceof PropertyFilter) {
                result.put(entry.getKey(), (PropertyFilter) f);
            } else if (f instanceof BeanPropertyFilter) {
                result.put(entry.getKey(), _convert((BeanPropertyFilter) f));
            } else {
                throw new IllegalArgumentException("Unrecognized filter type (" + f.getClass().getName() + ")");
            }
        }
        return result;
    }

    private final static PropertyFilter _convert(BeanPropertyFilter f) {
        return SimpleBeanPropertyFilter.from(f);
    }

    public UnifyFilterProvider setFailOnUnknownId(boolean state) {
        _cfgFailOnUnknownId = state;
        return this;
    }

    public boolean willFailOnUnknownId() {
        return _cfgFailOnUnknownId;
    }

    public UnifyFilterProvider addFilter(String id, PropertyFilter filter) {
        _filtersById.put(id, filter);
        return this;
    }

    public UnifyFilterProvider addFilter(Class<?> id, PropertyFilter filter) {
        _filtersByClass.put(id, filter);
        return this;
    }

    public UnifyFilterProvider addFilter(String id, SimpleBeanPropertyFilter filter) {
        _filtersById.put(id, filter);
        return this;
    }

    public UnifyFilterProvider addFilter(Class<?> id, SimpleBeanPropertyFilter filter) {
        _filtersByClass.put(id, filter);
        return this;
    }

    public UnifyFilterProvider setDefaultFilter(PropertyFilter f) {
        _defaultFilter = f;
        return this;
    }

    public PropertyFilter removeFilter(String id) {
        return _filtersById.remove(id);
    }

    public PropertyFilter removeFilter(Class<?> id) {
        return _filtersByClass.remove(id);
    }

    public PropertyFilter getDefaultFilter() {
        return _defaultFilter;
    }

    @Deprecated // since 2.3
    @Override
    public BeanPropertyFilter findFilter(Object filterId) {
        throw new UnsupportedOperationException("Access to deprecated filters not supported");
    }

    @Override
    public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
        PropertyFilter f = null;
        if (filterId.getClass() == String.class) {
            f = _filtersById.get(filterId);
        } else if (filterId instanceof Class) {
            f = _filtersByClass.get(filterId);
        }

        if (f == null){
            f = _filtersByClass.get(valueToFilter.getClass());
        }

        if (f == null){
            f = _filtersById.get(filterId.getClass().getSimpleName());
        }

        if (f == null) {
            f = _defaultFilter;
            if (f == null && _cfgFailOnUnknownId) {
                throw new IllegalArgumentException("No filter configured with id '" + filterId + "' (type "
                        + filterId.getClass().getName() + ")");
            }
        }
        return f;
    }
}

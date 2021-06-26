package red.asuka.alice.commons.model;

import lombok.Data;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 18:22
 * @Desc: 过滤信息,当类型类的属性值为null，将设置为全局过滤
 */
@Data
public class PropertyFilterInfo {

    private Class<?> _clazz;
    private boolean _include;
    private String[] _properties;

    public PropertyFilterInfo(String... properties) {
        this(true, properties);
    }

    public PropertyFilterInfo(boolean include, String... properties) {
        this(include, null, null == properties ? new String[0] : properties);
    }

    public PropertyFilterInfo(boolean include, Class<?> clazz, String... properties) {
        this._clazz = clazz;
        this._include = include;
        this._properties = null == properties ? new String[0] : properties;
    }
}

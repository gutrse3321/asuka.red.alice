package red.asuka.alice.commons.springExtension.view;

import red.asuka.alice.commons.constant.Constant;
import red.asuka.alice.commons.model.PropertyFilterInfo;
import red.asuka.alice.commons.support.StringUtility;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/11/26 17:53
 * @Desc: 返回值包装面板
 *        公司项目中大多数仅适用返回值，没有进行属性的过滤
 */
public class MVF {

    /**
     * 构造成功返回包装视图类
     * 只有一个200
     * @return
     */
    public static ModelAndView nullData() {
        return new ModelAndView(new UnifySuccessView());
    }

    /**
     * 返回所有类型值
     * 过滤设置可选
     * @param entity
     * @param filters
     * @return
     */
    public static ModelAndView filterData(Object entity, PropertyFilterInfo ...filters) {
        if (null == entity) {
            return nullData();
        }

        if (entity instanceof List) {
            return uniqueKeyData("list", entity, filters);
        }

        /**
         * 构造自定义Jackson统一属性过滤器提供者的UnifySuccessView视图类
         */
        return new ModelAndView(new UnifySuccessView(entity, filters));
    }

    /**
     * 返回一个只有一个key-value的值
     * @param key
     * @param entity
     * @param filters
     * @return
     */
    public static ModelAndView uniqueKeyData(String key, Object entity, PropertyFilterInfo ...filters) {
        if (null == entity) {
            return nullData();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(StringUtils.isEmpty(key) ? "defaultKey" : key, entity);
        return filterData(map, filters);
    }

    /**
     * 只返回对象的ID
     * @param entity
     * @return
     */
    public static ModelAndView idData(Object entity) {
        if (null == entity) {
            return nullData();
        }

        /**
         * 构造过滤除"id"以外的属性的信息类PropertyFilterInfo
         */
        UnifySuccessView view = new UnifySuccessView(entity, new PropertyFilterInfo(true, "id"));
        return new ModelAndView(view);
    }

    /**
     * 只返回字符串
     * @param str
     * @return
     */
    public static ModelAndView stringData(String str) {
        if (!StringUtility.hasLength(str)) return nullData();
        return new ModelAndView(new UnifySuccessView(str));
    }

    /**
     * 返回整形
     * @param integer
     * @return
     */
    public static ModelAndView intData(Integer integer) {
        if (null == integer) return nullData();
        return new ModelAndView(new UnifySuccessView(integer));
    }

    /**
     * 返回仅id的list
     * @param entities
     * @return
     */
    public static ModelAndView idListData(List<?> entities) {
        if (CollectionUtils.isEmpty(entities)) return nullData();

        Class<?> clazz = entities.get(0).getClass();
        /**
         * 构造list中所有的类型类的除id以外过滤
         */
        return new ModelAndView(new UnifySuccessView(entities, new PropertyFilterInfo(true, clazz, "id")));
    }

    /**
     * 根据条件过滤返回值
     * @param entities
     * @param filters
     * @return
     */
    public static ModelAndView filterListData(List<?> entities, PropertyFilterInfo ...filters) {
        if (CollectionUtils.isEmpty(entities)) return nullData();
        return new ModelAndView(new UnifySuccessView(entities, filters));
    }

    /**
     * 返回成功信息
     * @param msg
     * @return
     */
    public static ModelAndView msgData(String msg) {
        return new ModelAndView(new UnifySuccessView(msg));
    }

    /**
     * 根据枚举类型获取返回视图
     * @param msgData
     * @return
     */
    public static ModelAndView msgData(Constant.MsgData msgData) {
        return msgData(msgData.getVal());
    }
}

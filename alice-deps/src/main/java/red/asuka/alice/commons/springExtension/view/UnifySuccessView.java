package red.asuka.alice.commons.springExtension.view;

import red.asuka.alice.commons.model.PropertyFilterInfo;

/**
 * @Author: Tomonori
 * @Date: 2019/11/26 16:17
 * @Desc: 成功返回包装类
 */
public class UnifySuccessView extends UnifyView {

    public UnifySuccessView() {
        this("null data");
    }

    public UnifySuccessView(Object object) {
        //构造UnifyView，使用默认jackson过滤器
        super();

        if (null != object) {
            //DATA: 继承的UnifyView public static final String DATA = "data";
            /**
             * addStaticAttribute: UnifyView继承的AbstractView抽象类的方法
             * 将静态数据添加到此视图，在每个视图中公开
             */
            this.addStaticAttribute(DATA, object);
        }
        //CODE: 继承的UnifyView public static final String CODE = "code";
        this.addStaticAttribute(CODE, 200);
    }

    /**
     * PropertyFilterInfo: 过滤信息,当类型类的属性值为null，将设置为全局过滤
     * @param object
     * @param filters
     */
    public UnifySuccessView(Object object, PropertyFilterInfo...filters) {
        //构造UnifyView，创建自定义Jackson统一属性过滤器提供者
        super(filters);

        if (null != object) {
            /**
             * addStaticAttribute: UnifyView继承的AbstractView抽象类的方法
             * 将静态数据添加到此视图，在每个视图中公开
             */
            this.addStaticAttribute(DATA, object);
        }
        this.addStaticAttribute(CODE, 200);
    }

    /**
     * 成功信息返回
     * @param msg
     */
    public UnifySuccessView(String msg) {
        super();
        if (msg.equals("")) msg = "操作成功";
        this.addStaticAttribute(MESSAGE, msg);
        this.addStaticAttribute(CODE, 200);
        this.addStaticAttribute(DATA, null);
    }
}

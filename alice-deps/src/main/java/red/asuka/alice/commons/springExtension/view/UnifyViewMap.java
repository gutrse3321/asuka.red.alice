package red.asuka.alice.commons.springExtension.view;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/11/25 18:35
 * @Desc: jackson 过滤器使用，
 *        UnifyView类中，默认code, data等字段，使用此Map包含
 */
public class UnifyViewMap<K, V> extends HashMap<K, V> {

    public UnifyViewMap() {
        super();
    }

    public UnifyViewMap(int initialCapacity) {
        super(initialCapacity);
    }

    public UnifyViewMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public UnifyViewMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
}

package red.asuka.alice.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.PropertyDescriptor;

/**
 * @Author: Tomonori
 * @Date: 2019/11/4 11:24
 * @Desc: EntityPropertyUtility类使用
 * Entity对象属性信息类
 */
@Data
@AllArgsConstructor
public class EntityPropertyInfo {

    private PropertyDescriptor primaryKey;
    private PropertyDescriptor[] otherKey;
}

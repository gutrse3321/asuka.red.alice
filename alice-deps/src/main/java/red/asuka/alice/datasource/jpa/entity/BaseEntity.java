package red.asuka.alice.datasource.jpa.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import red.asuka.alice.commons.constant.Constant;
import lombok.Data;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @Author: Tomonori
 * @Date: 2019/11/8 14:15
 * @Desc: 标注为@MappedSuperclass的类将不是一个完整的实体类，
 * 他将不会映射到数据库表，但是他的属性都将映射到其子类的数据库字段中。
 * 不能再标注@Entity或@Table注解，也无需实现序列化接口
 */
@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseEntity implements Serializable {

    protected Integer data_state = Constant.DataState.Available.ordinal();
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long    created_time;
    protected String  created_by;
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long    updated_time;
    protected String  updated_by;
}

package red.asuka.alice.commons.version;

import red.asuka.alice.commons.constant.Constant;
import red.asuka.alice.commons.hibernateExtension.annotation.EnumValue;
import red.asuka.alice.commons.hibernateExtension.annotation.Version;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author Tomonori
 * @Mail gutrse3321@live.com
 * @Date 2020-09-20 2:16 AM
 *
 * 设置头部信息
 */
@Data
public class DriversHeader {

    @NotNull
    @EnumValue(clazz = Constant.DriversOs.class)
    private Integer os;

    @NotBlank
    @Version
    private String clientVersion; //app版本

    private String channel; //渠道
}

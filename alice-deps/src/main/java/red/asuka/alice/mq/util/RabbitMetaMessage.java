package red.asuka.alice.mq.util;

import lombok.Data;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 17:52
 * @Desc: 常量类
 */
@Data
public class RabbitMetaMessage {

    String messageId;
    String exchange;
    String routingKey;
    Object payload;
}

package red.asuka.alice.mq.util;

import lombok.Getter;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 11:33
 * @Desc: 用于关联发布者的基类确认发送的消息
 */
public class CompleteCorrelationData extends CorrelationData {

    @Getter
    private String coordinator;

    public CompleteCorrelationData(String id, String coordinator) {
        super(id);
        this.coordinator = coordinator;
    }

    @Override
    public String toString() {
        return "CompleteCorrelationData id=" + getId() + ", coordinator" + this.coordinator;
    }
}

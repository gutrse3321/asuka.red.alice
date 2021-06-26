package red.asuka.alice.persist.model.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-07 10:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventModel {

    private String eventName;
    private Object data;

}

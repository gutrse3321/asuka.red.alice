package red.asuka.alice.websocket;

import lombok.Data;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-25 18:14
 *
 * websocket统一报文模型
 */
@Data
public class WsParamModel {

    private String route;        //执行路由
    private String clientId;     //客户端唯一id
    private String request_time; //发起时间戳
    private String data;         //数据json序列化
}

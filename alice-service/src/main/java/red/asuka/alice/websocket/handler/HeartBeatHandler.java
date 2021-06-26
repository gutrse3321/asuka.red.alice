package red.asuka.alice.websocket.handler;

import red.asuka.alice.websocket.WsParamModel;
import red.asuka.alice.websocket.config.WsAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-26 14:45
 */
@Component
public class HeartBeatHandler extends WsAbstractHandler {

    private final static String routeKey = "heartBeat";

    public HeartBeatHandler() {
        super(routeKey);
    }

    @Override
    public WsParamModel handle(WsParamModel model) throws Exception {
        model.setRequest_time(String.valueOf(System.currentTimeMillis()));
        model.setData("Currently receive Heart Beat, Message From Server ... ");
        return model;
    }
}

package red.asuka.alice.websocket.config;

import red.asuka.alice.websocket.WsParamModel;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-25 18:12
 */
public abstract class WsAbstractHandler {

    private String routeKey;

    public WsAbstractHandler(String routeKey) {
        this.routeKey = routeKey;
    }

    public abstract WsParamModel handle(WsParamModel model) throws Exception;

    public String getRouteKey() {
        return this.routeKey;
    }
}

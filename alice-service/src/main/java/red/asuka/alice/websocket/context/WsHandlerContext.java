package red.asuka.alice.websocket.context;

import red.asuka.alice.websocket.config.WsAbstractHandler;

import java.util.HashMap;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-26 15:39
 */
public class WsHandlerContext {

    //函数池
    private static HashMap<String, WsAbstractHandler> handlerPool = new HashMap<>();

    public static void put(String routeKey, WsAbstractHandler handler) {
        handlerPool.put(routeKey, handler);
    }

    public static WsAbstractHandler get(String routeKey) {
        return handlerPool.get(routeKey);
    }
}

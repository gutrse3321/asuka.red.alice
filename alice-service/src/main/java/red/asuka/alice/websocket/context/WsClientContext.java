package red.asuka.alice.websocket.context;

import red.asuka.alice.websocket.WebSocketServer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-25 20:38
 */
public class WsClientContext {

    private static ConcurrentHashMap<String, WebSocketServer> clientMap = new ConcurrentHashMap<>();

    public static void set(String clientId, WebSocketServer client) {
        if (clientMap.containsKey(clientId)) {
            clear(clientId);
        }
        clientMap.put(clientId, client);
    }

    public static WebSocketServer get(String clientId) {
        return clientMap.get(clientId);
    }

    public static List<String> clientIds() {
        return clientMap.keySet().stream().collect(Collectors.toList());
    }

    public static void clear(String clientId) {
        clientMap.remove(clientId);
    }
}

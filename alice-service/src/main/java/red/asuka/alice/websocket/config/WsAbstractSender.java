package red.asuka.alice.websocket.config;

import red.asuka.alice.commons.constant.CacheConstant;
import red.asuka.alice.websocket.WebSocketServer;
import red.asuka.alice.websocket.WsParamModel;
import red.asuka.alice.websocket.context.WsClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-26 14:43
 */
public abstract class WsAbstractSender<T> {

    @Autowired
    RedisTemplate redisTemplate;
    private String route;
    private String clientId;

    public WsAbstractSender(String route) {
        this.route = route;
    }

    protected abstract String handle(T o);

    public void send(String clientId, T o) {
        this.clientId = clientId;
        String data = this.handle(o);
        WsParamModel handle = new WsParamModel();
        handle.setRoute(this.route);
        handle.setRequest_time(String.valueOf(System.currentTimeMillis()));
        handle.setClientId(clientId);
        handle.setData(data);
        WebSocketServer server = WsClientContext.get(clientId);
        if (server != null) server.send(handle);
    }

    public void sendGroup(Long village_id, T o) throws Exception {
        //TODO 缓存获取这个id组下的所有clientId
        SetOperations ops = redisTemplate.opsForSet();
        Cursor<String> cursor = ops.scan(CacheConstant.WS_VILLAGE_GROUP + CacheConstant.SPLIST + village_id, ScanOptions.NONE);
        while (cursor.hasNext()) {
            String clientId = cursor.next();
            send(clientId, o);
        }
    }

    public void sendAll(T o) throws Exception {
        for (String clientId : WsClientContext.clientIds()) {
            send(clientId, o);
        }
    }

    public String getClientId() {
        return this.clientId;
    }
}

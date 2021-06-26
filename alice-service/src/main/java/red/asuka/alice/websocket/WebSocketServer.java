package red.asuka.alice.websocket;

import red.asuka.alice.commons.constant.CacheConstant;
import red.asuka.alice.commons.support.DateUtility;
import red.asuka.alice.commons.support.JsonUtility;
import red.asuka.alice.websocket.config.WsAbstractHandler;
import red.asuka.alice.websocket.context.WsClientContext;
import red.asuka.alice.websocket.context.WsHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-26 14:31
 */
@ServerEndpoint("/public-house/{clientId}")
@Component
public class WebSocketServer {

    private final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    public static RedisTemplate redisTemplate;

    //websocket client 连接回话
    private Session session;
    private String clientId;
    private String villageId;

    /**
     * 连接成功
     * @param session
     * @param clientId
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("clientId") String clientId) throws IOException {
        this.session = session;
        this.clientId = clientId;
        String[] arr = clientId.split("_");
        if (arr.length < 2) {
            send("exception", clientId, "clientId无关联小区Id");
            session.close();
        }
        this.villageId = arr[0];

        //分小区缓存到redis
        SetOperations ops = redisTemplate.opsForSet();
        ops.add(CacheConstant.WS_VILLAGE_GROUP + CacheConstant.SPLIST + arr[0],
                clientId);
        WsClientContext.set(clientId, this);
        log.info("【WebSocket onOpen】 客户端：{}，连接成功：{}", clientId, DateUtility.getCurrentDate_YYYYMMDD());

        try {
            send("greeting", clientId, "hello");
        } catch (Exception e) {
            log.warn("【WebSocket onOpen】 客户端：{}，回调信息异常：\n{}", clientId, e);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param msg
     * @param session
     */
    @OnMessage
    public void onMessage(String msg, Session session) {
        WsParamModel param;
        try {
            param = JsonUtility.toObject(msg, WsParamModel.class);
        } catch (Exception e) {
            send("exception", clientId, "报文反序列化失败");
            return;
        }

        if (StringUtils.isEmpty(param.getRoute()) || StringUtils.isEmpty(param.getClientId())) {
            param.setRoute("exception");
            param.setRequest_time(String.valueOf(System.currentTimeMillis()));
            param.setData("路由或ClientId为空");
            param.setClientId(clientId);
            send(param);
            return;
        }

        log.info("【WebSocket onMessage】 客户端：{}，收到客户端的报文为：\n{}", clientId, param);
        WsAbstractHandler handler = WsHandlerContext.get(param.getRoute());
        if (handler != null) {
            try {
                //回文
                WsParamModel data = handler.handle(param);
                if (data != null) {
                    send(data);
                }
            } catch (Exception e) {
            }
        }
    }

    @OnClose
    public void onClose() {
        SetOperations ops = redisTemplate.opsForSet();
        ops.remove(CacheConstant.WS_VILLAGE_GROUP + CacheConstant.SPLIST + villageId,
                clientId);
        WsClientContext.clear(clientId);
        log.info("【WebSocket onClose】 客户端：{}，已断开：{}", DateUtility.getCurrentDate_YYYYMMDD());
    }

    @OnError
    public void onError(Session session, Throwable e) throws IOException {
        session.close();
        SetOperations ops = redisTemplate.opsForSet();
        ops.remove(CacheConstant.WS_VILLAGE_GROUP + CacheConstant.SPLIST + villageId,
                clientId);
        log.info("【WebSocket onClose】 客户端：{}，错误：\n", clientId, e);
    }

    public void send(WsParamModel param) {
        try {
            this.session
                    .getBasicRemote()
                    .sendText(JsonUtility.toString(param));
        } catch (Exception e) {
            log.info("【WebSocket send】 客户端：{}，发送报文异常：\n{}", clientId, e);
        }
    }

    /**
     * 推送至客户端
     * @param clientId
     * @param data
     * @throws IOException
     */
    public void send(String route, String clientId, String data) {
        WsParamModel param = new WsParamModel();
        param.setRoute(route);
        param.setData(data);
        param.setClientId(clientId);
        param.setRequest_time(String.valueOf(System.currentTimeMillis()));
        send(param);
    }
}

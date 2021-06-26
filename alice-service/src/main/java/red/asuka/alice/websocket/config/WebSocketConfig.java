package red.asuka.alice.websocket.config;

import red.asuka.alice.websocket.WebSocketServer;
import red.asuka.alice.websocket.context.WsHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.List;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-03-25 11:19
 */
@Configuration
public class WebSocketConfig {

    //已实现的函数
    @Autowired
    private List<WsAbstractHandler> handlerList;
    @Autowired
    RedisTemplate redisTemplate;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        for (WsAbstractHandler handler : handlerList) {
            WsHandlerContext.put(handler.getRouteKey(), handler);
        }
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        WebSocketServer.redisTemplate = redisTemplate;
    }
}

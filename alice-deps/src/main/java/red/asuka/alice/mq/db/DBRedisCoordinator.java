package red.asuka.alice.mq.db;

import red.asuka.alice.commons.support.JsonUtility;
import red.asuka.alice.mq.util.MQConstants;
import red.asuka.alice.mq.util.RabbitMetaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 17:56
 * @Desc:
 */
@Component
public class DBRedisCoordinator implements DBCoordinator {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 设置消息为prepare状态
     */
    @Override
    public void setMsgPrepare(String msgId) {
        redisTemplate.opsForSet().add(MQConstants.MQ_MSG_PREPARE, msgId);
    }

    /**
     * 设置消息为ready状态，删除prepare状态
     */
    @Override
    public void setMsgReady(String msgId, RabbitMetaMessage rabbitMetaMessage) {
        try {
            redisTemplate.opsForHash().put(MQConstants.MQ_MSG_READY, msgId, JsonUtility.toString(rabbitMetaMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisTemplate.opsForSet().remove(MQConstants.MQ_MSG_PREPARE, msgId);
    }

    /**
     * 消息发送成功，删除ready状态信息
     * @param msgId
     */
    @Override
    public void setMsgSuccess(String msgId) {
        redisTemplate.opsForHash().delete(MQConstants.MQ_MSG_READY, msgId);
    }

    /**
     * 从db中获取消息实体
     * @param msgId
     * @return
     */
    @Override
    public RabbitMetaMessage getMetaMsg(String msgId) {
        String value = (String) redisTemplate.opsForHash().get(MQConstants.MQ_MSG_READY, msgId);
        try {
            return JsonUtility.toObject(value, RabbitMetaMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取ready状态消息
     * @return
     * @throws Exception
     */
    @Override
    public List getMsgReady() throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();
        List<RabbitMetaMessage> messages = hashOperations.values(MQConstants.MQ_MSG_READY);
        List<RabbitMetaMessage> messageAlert = new ArrayList<>();
        List<String> messageIds = new ArrayList<>();

        for (RabbitMetaMessage message : messages) {
            /**
             * 如果消息超时，加入超时队列
             */
            if (messageTimeOut(message.getMessageId())) {
                messageAlert.add(message);
                messageIds.add(message.getMessageId());
            }
        }

        /**
         * 在redis中删除已超时的消息
         */
        hashOperations.delete(MQConstants.MQ_MSG_READY, messageIds);
        return messageAlert;
    }

    boolean messageTimeOut(String messageId) throws Exception {
        String messageTime = (messageId.split(MQConstants.DB_SPLIT))[1];
        long timeGap = System.currentTimeMillis() - new SimpleDateFormat(MQConstants.TIME_PATTERN).parse(messageTime).getTime();

        if (timeGap > MQConstants.TIME_GAP) {
            return true;
        }
        return false;
    }

    /**
     * 获取prepare状态消息
     * @return
     * @throws Exception
     */
    @Override
    public List getMsgPrepare() throws Exception {
        SetOperations setOperations = redisTemplate.opsForSet();
        Set<String> messageIds = setOperations.members(MQConstants.MQ_MSG_PREPARE);
        List<String> messageAlert = new ArrayList();
        for(String messageId: messageIds){
            /**
             * 如果消息超时，加入超时队列
             */
            if(messageTimeOut(messageId)){
                messageAlert.add(messageId);
            }
        }
        /**
         * 在redis中删除已超时的消息
         */
        setOperations.remove(MQConstants.MQ_MSG_READY,messageAlert);
        return messageAlert;
    }

    /**
     * 消息重发次数+1
     * @param key
     * @param hashKey
     * @return
     */
    @Override
    public Long incrResendKey(String key, String hashKey) {
        return redisTemplate.opsForHash().increment(key, hashKey, 1);
    }

    /**
     * 获取重发值
     * @param key
     * @param hashKey
     * @return
     */
    @Override
    public Long getResendValue(String key, String hashKey) {
        return (Long) redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Long delete(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }
}

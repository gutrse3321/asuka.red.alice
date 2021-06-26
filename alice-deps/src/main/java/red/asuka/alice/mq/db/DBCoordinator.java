package red.asuka.alice.mq.db;

import red.asuka.alice.mq.util.RabbitMetaMessage;

import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 17:50
 * @Desc:
 */
public interface DBCoordinator {

    /**
     * 设置消息为prepare状态
     */
    void setMsgPrepare(String msgId);

    /**
     * 设置消息为ready状态，删除prepare状态
     */
    void setMsgReady(String msgId, RabbitMetaMessage rabbitMetaMessage);

    /**
     * 消息发送成功，删除ready状态信息
     * @param msgId
     */
    void setMsgSuccess(String msgId);

    /**
     * 从db中获取消息实体
     * @param msgId
     * @return
     */
    RabbitMetaMessage getMetaMsg(String msgId);

    /**
     * 获取ready状态消息
     * @return
     * @throws Exception
     */
    List getMsgReady() throws Exception;

    /**
     * 获取prepare状态消息
     * @return
     * @throws Exception
     */
    List getMsgPrepare() throws Exception;

    /**
     * 消息重发次数+1
     * @param key
     * @param hashKey
     * @return
     */
    Long incrResendKey(String key, String hashKey);

    Long getResendValue(String key, String hashKey);

    /**
     * 删除消息
     * @param key
     * @param hashKey
     * @return
     */
    Long delete(String key, String hashKey);
}

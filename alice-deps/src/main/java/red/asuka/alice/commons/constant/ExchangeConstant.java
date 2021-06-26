package red.asuka.alice.commons.constant;

/**
 * @Author Tomonori
 * @Mail   gutrse3321@live.com
 * @Date   2020-09-20 ${TIME}
 */
public class ExchangeConstant {

    public static final String EVENT_EXCHANGE = "alice.event.exchange"; //事件交换机

    /************************ OAuth相关队列 ************************/
    //oauth用户交换机
    public static final String OAUTH_USER_EXCHANGE = "aw.oauth.user.exchange";
    //oauth广播交换机
    public static final String OAUTH_USER_FANOUT_EXCHANGE = "aw.oauth.user.fanout.exchange";

}

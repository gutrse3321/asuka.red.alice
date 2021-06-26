package red.asuka.alice.commons.http.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 16:03
 * @Title: http管理者模型
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Data
/**
 * ConfigurationProperties这个注解之前不生效，使用@Component注入也可以，
 * 但实际上需要在其他类上使用EnableConfigurationProperties注解并传现在这个class
 * 才能生效读取配置文件ConfigurationProperties这个注解
 * 具体可以ctrl+left click本类，看那个使用了本类
 */
@ConfigurationProperties(prefix = "appserver.http")
public class HttpManagerProperties {

    private int timeOutSocket;            //响应超时
    private int timeOutConnection;        //建立连接超时
    private int timeOutRequestConnection; //获取连接超时
    private int retryNumber;              //重试次数
    private int maxTotal;                 //最大连接数

    private Map<String, HttpClientProperties> clientProperties;
}

package red.asuka.alice.commons.http.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author: Tomonori
 * @Date: 2019/12/25 11:17
 * @Title: HttpClient配置
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@AllArgsConstructor
@Getter
/**
 * @Accessors用于配置getter和setter方法的生成结果,fluent都以基础属性名声明方法
 * eg: 没设置则是public CacheConfig setNameSpace(String nameSpace), 设置了fluent为true，则是
 * public CacheConfig nameSpace(String nameSpace)
 * getter同理
 */
@Accessors(fluent = true)
public class HttpManagerConfig {

    private int timeOutSocket; //响应超时
    private int timeOutConnection; //建立连接超时
    private int timeOutRequestConnection; //获取连接超时
    private int retryNumber;
    private int maxTotal;

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(final HttpManagerConfig config) {
        if (null == config) {
            return null;
        }

        return new Builder()
                .timeOutSocket(config.timeOutSocket())
                .timeOutConnection(config.timeOutConnection())
                .timeOutRequestConnection(config.timeOutRequestConnection())
                .retryNumber(config.retryNumber())
                .maxTotal(config.maxTotal());
    }

    @Setter
    @Accessors(fluent = true)
    public static class Builder {

        private int timeOutSocket;
        private int timeOutConnection;
        private int timeOutRequestConnection;
        private int retryNumber;
        private int maxTotal;

        Builder() {
            timeOutSocket = 5000;
            timeOutConnection = 2000;
            timeOutRequestConnection = 5000;
            retryNumber = 2;
            maxTotal = 500;
        }

        public HttpManagerConfig build() {
            return new HttpManagerConfig(
                    timeOutSocket,
                    timeOutConnection,
                    timeOutRequestConnection,
                    retryNumber,
                    maxTotal
            );
        }
    }
}

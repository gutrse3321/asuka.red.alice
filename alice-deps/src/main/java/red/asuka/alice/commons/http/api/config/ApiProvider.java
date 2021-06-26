package red.asuka.alice.commons.http.api.config;

import red.asuka.alice.commons.http.HttpClientManager;
import red.asuka.alice.commons.http.config.HttpClientProperties;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 16:15
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public interface ApiProvider {

    /**
     * 设置http客户端信息
     * @param properties
     */
    void setHttpClientProperties(HttpClientProperties properties);

    void setHttpClientManager(HttpClientManager clientManager);

    HttpClientProperties getHttpClientProperties();

    void init();
}

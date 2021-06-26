package red.asuka.alice.commons.http;

import red.asuka.alice.commons.http.api.annotation.Api;
import red.asuka.alice.commons.http.api.annotation.ApiScan;
import red.asuka.alice.commons.http.api.config.ApiProvider;
import red.asuka.alice.commons.http.config.HttpClientProperties;
import red.asuka.alice.commons.http.config.HttpManagerProperties;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 14:26
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Configuration
//自动化配置
@ConditionalOnProperty(prefix = "appserver.http", name = "enabled")
//注册Http管理者模型Bean
@EnableConfigurationProperties(HttpManagerProperties.class)
//扫描包以及注册ApiRegister
@ApiScan("red.asuka.alice.server.http")
public class HttpClientConfiguration {

    private HttpManagerProperties managerProperties;
    @Autowired(required = false)
    private List<ApiProvider> apiProviders;

    public HttpClientConfiguration(HttpManagerProperties managerProperties) {
        this.managerProperties = managerProperties;
    }

    @Bean
    public HttpClientManager httpClientManager() {
        HttpClientManager httpClientManager = new HttpClientManager(managerProperties);
        Map<String, HttpClientProperties> clientProperties = managerProperties.getClientProperties();

        if (!CollectionUtils.isEmpty(clientProperties)) {
            clientProperties.forEach((s, httpClientProperties) -> {
                HttpHost httpHost = HttpHost.create(httpClientProperties.getHost());
                httpClientManager.setMaxPerRoute(httpHost, httpClientProperties.getMaxPreRoute());
                ApiProvider apiProvider = findApiProvider(s);

                if (null != apiProvider) {
                    apiProvider.setHttpClientManager(httpClientManager);
                    apiProvider.setHttpClientProperties(httpClientProperties);
                    apiProvider.init();
                }
            });
        }

        return httpClientManager;
    }

    private ApiProvider findApiProvider(String name) {
        if (CollectionUtils.isEmpty(apiProviders)) {
            return null;
        }

        for (ApiProvider apiProvider : apiProviders) {
            Api annotation = AnnotationUtils.findAnnotation(apiProvider.getClass(), Api.class);
            String value = annotation.value();

            if (name.equals(value)) {
                return apiProvider;
            }
        }

        return null;
    }
}

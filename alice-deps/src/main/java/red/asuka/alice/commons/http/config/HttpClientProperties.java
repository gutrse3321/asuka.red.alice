package red.asuka.alice.commons.http.config;

import lombok.Data;

import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/12/17 16:11
 * @Title: http客户端模型
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Data
public class HttpClientProperties {

    private String  host;
    private String  v3Host;
    private Integer maxPreRoute;
    private Boolean https;

    private Map<String, String> urlMap;
}

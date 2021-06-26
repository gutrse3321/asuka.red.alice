package red.asuka.alice.redis.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 15:08
 * @Desc:
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisCacheProperties {

    private String  packagesToScan;
    private Long    expiredTime = 1800L;
    private Integer pageSize = 20;
}

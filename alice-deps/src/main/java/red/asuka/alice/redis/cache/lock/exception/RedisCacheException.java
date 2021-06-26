package red.asuka.alice.redis.cache.lock.exception;

import org.springframework.dao.DataAccessException;

/**
 * @Author: Tomonori
 * @Date: 2019/11/19 17:47
 * @Desc:
 */
public class RedisCacheException extends DataAccessException {

    public RedisCacheException(String msg) {
        super(msg);
    }

    public RedisCacheException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

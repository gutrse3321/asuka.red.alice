package red.asuka.alice.commons.support;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @Author: Tomonori
 * @Date: 2019/11/20 16:31
 * @Desc: null值校验
 */
@UtilityClass
public class NullResolveUtility {

    public <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}

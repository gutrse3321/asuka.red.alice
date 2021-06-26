package red.asuka.alice.reactor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2020-12-04 15:31
 */
@Component
public class ReactorConfig {
    
    @Bean
    Environment env() {
        return new Environment();
    }

    @Bean
    @Primary
    Reactor pool(Environment env) {
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.THREAD_POOL)
                .get();
    }

    @Bean
    Reactor loop(Environment env) {
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.EVENT_LOOP)
                .get();
    }
}

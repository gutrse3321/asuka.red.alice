package red.asuka.alice.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-02 14:16
 */
@SpringBootApplication(scanBasePackages = {"red.asuka.alice"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"red.asuka.alice"})
@ComponentScan("red.asuka.alice")
@MapperScan({"red.asuka.alice.persist.mapper"})
@EnableAspectJAutoProxy
public class AliceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliceApplication.class);
        System.out.println("\n" +
                "          _      _____ _____ ______ \n" +
                "    /\\   | |    |_   _/ ____|  ____|\n" +
                "   /  \\  | |      | || |    | |__   \n" +
                "  / /\\ \\ | |      | || |    |  __|  \n" +
                " / ____ \\| |____ _| || |____| |____ \n" +
                "/_/    \\_\\______|_____\\_____|______| —— The Pool of Tears");
    }
}

package red.asuka.alice.commons;

import red.asuka.alice.commons.support.HostAddressUtility;
import red.asuka.alice.commons.support.StringUtility;
import red.asuka.alice.commons.support.encrypt.EncryptorUtility;
import red.asuka.alice.commons.support.generator.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Set;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-19 17:10
 */
@Configuration
public class AutowiredConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public EncryptorUtility encryptorUtility() {
        String encryptor = environment.getProperty("appserver.autowired.encryptor");

        if (StringUtility.hasText(encryptor)) {
            return new EncryptorUtility(encryptor);
        }

        return new EncryptorUtility("");
    }

    @Bean
    public IdGenerator idGenerator() throws SocketException {
        String ignoreHost = environment.getProperty("appserver.autowired.id-generator");
        if (StringUtility.hasText(ignoreHost)) {
            return new IdGenerator(hostToLong());
        }
        return new IdGenerator(HostAddressUtility.localHostAfterTwo());
    }

    private long hostToLong() throws SocketException {
        String ignoreHost = environment.getProperty("appserver.autowired.id-generator");
        if (StringUtility.hasText(ignoreHost)) {
            String[] ignoreHosts = ignoreHost.split(",");
            Set<InetAddress> hosts = HostAddressUtility.multiGetLocalAddress(ignoreHosts);
            if (!CollectionUtils.isEmpty(hosts)) {
                long lastTwoHost = HostAddressUtility.localHostAfterTwo((InetAddress) hosts.toArray()[0]);
                return lastTwoHost;
            }
        }
        return HostAddressUtility.localHostAfterTwo();
    }

}

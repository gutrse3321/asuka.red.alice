package red.asuka.alice.commons.http.config;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-01-18 17:24
 */
public class AliceHttpDelete extends HttpEntityEnclosingRequestBase {

    public static final String METHOD_NAME = "DELETE";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public AliceHttpDelete(String uri) {
        setURI(URI.create(uri));
    }

    public AliceHttpDelete(URI uri) {
        setURI(uri);
    }

    public AliceHttpDelete() {
    }
}

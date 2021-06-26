package red.asuka.alice.commons.http;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * @Author: Tomonori
 * @Date: 2019/12/18 11:28
 * @Title: 连接失败重试处理
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * ----- HttpRequestRetryHandler: Http请求出错后的重试的处理接口类
 */
public class SimpleHttpRequestRetryHandler implements HttpRequestRetryHandler {

    private int retryNumber;

    public SimpleHttpRequestRetryHandler(int retryNumber) {
        super();
        this.retryNumber = retryNumber;
    }

    /**
     * 主要实现方法
     * @param e
     * @param executionCount
     * @param httpContext
     * @return
     */
    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {

        //超过构造传参的重试次数，不再重试
        if (executionCount >= retryNumber) {
            return false;
        }

        if (e instanceof InterruptedIOException) {
            return true;
        }

        if (e instanceof UnknownHostException) {
            return false;
        }

        if (e instanceof ConnectTimeoutException) {
            return true;
        }

        if (e instanceof SSLException) {
            return false;
        }

        /**
         * HttpClientContext适配器类简化上下文状态的活动
         */
        HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        HttpRequest request = clientContext.getRequest();
        //如果是post/put这种有body的请求，需要先判断100-continue扩展协议是否支持
        //即发送包含body请求前，先判断服务端是否支持同样的协议如果不支持，则不发送了。除非特殊约定，默认双端是都不设置的。
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

        if (idempotent) {
            return true;
        }
        return false;
    }
}

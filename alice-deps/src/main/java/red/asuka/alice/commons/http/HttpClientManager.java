package red.asuka.alice.commons.http;

import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.commons.http.config.HttpManagerConfig;
import red.asuka.alice.commons.http.config.HttpManagerProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Tomonori
 * @Date: 2019/12/18 10:20
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public class HttpClientManager {

    /**
     * ReentrantLock是可以重入的锁，相比synchronized关键字而言功能更加丰富
     * 当一个线程获取锁时，还可以接着重复获取多次，且是独占锁，只允许线程互斥的访问临界区
     * 需要手动加锁和解锁
     */
    private Lock lock = new ReentrantLock();
    private HttpManagerProperties managerProperties;

    //客户端连接池
    @Getter
    private PoolingHttpClientConnectionManager poolingClientConnectionManager;
    //默认client
    @Getter
    private CloseableHttpClient defaultClient;
    private Map<HttpHost, CloseableHttpClient> hostClients;

    //过期连接回收线程
    private IdelConnectionMonitorThread monitorThread;

    @Getter
    @Setter
    private RequestConfig defaultRequestConfig;
    @Getter
    @Setter
    private HttpRequestRetryHandler defaultRequestRetryHandler;

    public HttpClientManager(HttpManagerProperties managerProperties) {
        Assert.notNull(managerProperties, "config can not be null");
        this.managerProperties = managerProperties;
        init();
    }

    public HttpClientManager(HttpManagerConfig managerConfig) {
        this.managerProperties = new HttpManagerProperties();
        this.managerProperties.setTimeOutSocket(managerConfig.timeOutSocket());
        this.managerProperties.setTimeOutConnection(managerConfig.timeOutConnection());
        this.managerProperties.setTimeOutRequestConnection(managerConfig.timeOutRequestConnection());
        this.managerProperties.setRetryNumber(managerConfig.retryNumber());
        this.managerProperties.setMaxTotal(managerConfig.maxTotal());

        init();
    }

    public void init() {
        this.poolingClientConnectionManager = new PoolingHttpClientConnectionManager();

        ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom()
                                                   .setMalformedInputAction(CodingErrorAction.IGNORE)
                                                   .setUnmappableInputAction(CodingErrorAction.IGNORE)
                                                   .setCharset(Consts.UTF_8)
                                                   .build();
        SocketConfig defaultSocketConfig = SocketConfig.custom()
                                           .setSoTimeout(managerProperties.getTimeOutSocket())
                                           .setTcpNoDelay(true)
                                           .build();

        this.poolingClientConnectionManager.setDefaultConnectionConfig(defaultConnectionConfig);
        this.poolingClientConnectionManager.setDefaultSocketConfig(defaultSocketConfig);
        this.poolingClientConnectionManager.setMaxTotal(managerProperties.getMaxTotal());

        this.defaultRequestConfig = RequestConfig.custom()
                                    .setCookieSpec(CookieSpecs.DEFAULT)
                                    .setSocketTimeout(managerProperties.getTimeOutSocket())
                                    .setConnectionRequestTimeout(managerProperties.getTimeOutRequestConnection())
                                    .setConnectTimeout(managerProperties.getTimeOutConnection())
                                    .build();
        /**
         * 连接失败重试处理
         */
        this.defaultRequestRetryHandler = new SimpleHttpRequestRetryHandler(managerProperties.getRetryNumber());

        this.defaultClient = HttpClients.custom()
                                        .setDefaultRequestConfig(defaultRequestConfig)
                                        .setRetryHandler(defaultRequestRetryHandler)
                                        .setConnectionManager(poolingClientConnectionManager)
                                        .build();
        /**
         * 过期连接回收线程
         */
        this.monitorThread = new IdelConnectionMonitorThread(poolingClientConnectionManager);
        this.monitorThread.start();
    }

    /**
     * http客户端实例
     * @return
     */
    public HttpClientBuilder createHttpClient() {
        return HttpClients.custom()
                          .setDefaultRequestConfig(defaultRequestConfig)
                          .setRetryHandler(defaultRequestRetryHandler)
                          .setConnectionManager(poolingClientConnectionManager);
    }

    public void registerClient(HttpHost host) {
        CloseableHttpClient createHttpClient = createHttpClient().build();
        registerClient(host, createHttpClient);
    }

    public void registerClient(HttpHost host, CloseableHttpClient client) {
        lock.lock();

        try {
            if (null == hostClients) {
                hostClients = new HashMap<>();
            }
            hostClients.put(host, client);
        } finally {
            lock.unlock();
        }
    }

    public void setMaxPerRoute(HttpHost host, int maxPerRoute) {
        this.poolingClientConnectionManager.setMaxPerRoute(new HttpRoute(host), maxPerRoute);
    }

    public void removeClient(HttpHost host) throws IOException {
        lock.lock();

        try {
            if (!CollectionUtils.isEmpty(hostClients)) {
                CloseableHttpClient closeableHttpClient = hostClients.get(host);

                if (null != closeableHttpClient) {
                    closeableHttpClient.close();
                    hostClients.remove(host);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        lock.lock();

        try {
            if (null != defaultClient) {
                monitorThread.shutDown();
                poolingClientConnectionManager.close();
                defaultClient.close();

                if (!CollectionUtils.isEmpty(hostClients)) {
                    hostClients.forEach((httpHost, closeableHttpClient) -> {
                        try {
                            closeableHttpClient.close();
                        } catch (IOException e) {

                        }
                    });
                }
            }
        } catch (IOException e) {
            lock.unlock();
        }
    }

    public AliceHttpResponse execute(AliceHttpRequest request, HttpClientContext context) throws Exception {
        CloseableHttpClient client = this.defaultClient;

        if (null != hostClients) {
            CloseableHttpClient closeableHttpClient = hostClients.get(request.getHttpPost());
            if (null != closeableHttpClient) {
                client = closeableHttpClient;
            }
        }

        return execute(client, request, context);
    }

    public AliceHttpResponse execute(AliceHttpRequest request) throws Exception {
        CloseableHttpClient client = this.defaultClient;

        if (null != hostClients) {
            CloseableHttpClient closeableHttpClient = hostClients.get(request.getHttpHost());

            if (null != closeableHttpClient) {
                client = closeableHttpClient;
            }
        }

        return execute(client, request);
    }

    /**
     * TODO 封装异常抛出rollback
     * @param client
     * @param request
     * @param context
     * @return
     */
    public AliceHttpResponse execute(CloseableHttpClient client, AliceHttpRequest request, HttpClientContext context) throws Exception {
        CloseableHttpResponse closeableHttpResponse = null;
        HttpEntity entity = null;
        AliceHttpResponse response;

        try {
            closeableHttpResponse = client.execute(request.getHttpRequestBase(), context);
            int code = closeableHttpResponse.getStatusLine().getStatusCode();
            String codeText = closeableHttpResponse.getStatusLine().getReasonPhrase();

            response = new AliceHttpResponse();
            response.setStatusCode(code);
            response.setStatusCodeText(codeText);
            parsingHeader(response, closeableHttpResponse);
        } catch (IOException e) {
            throw EXPF.exception(ErrorCode.HttpRequestError.getCode(), ErrorCode.HttpRequestError.getTemplate(),
                    null, this.getClass().getSimpleName(),true);
        } finally {
            if (null != closeableHttpResponse) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {

                }
            }

            if (null != entity) {
                try {
                    entity.getContent().close();
                } catch (IllegalStateException e) {

                } catch (IOException e) {

                }
            }

            if (null != request) {
                request.close();
            }
        }

        return response;
    }

    /**
     * TODO 封装异常抛出roll back
     * @param client
     * @param request
     * @return
     * @throws Exception
     */
    public AliceHttpResponse execute(CloseableHttpClient client, AliceHttpRequest request) throws Exception {
        CloseableHttpResponse closeableHttpResponse = null;
        HttpEntity entity = null;
        AliceHttpResponse response;

        try {
            closeableHttpResponse = client.execute(request.getHttpRequestBase());
            int code = closeableHttpResponse.getStatusLine().getStatusCode();
            String codeText = closeableHttpResponse.getStatusLine().getReasonPhrase();

            response = new AliceHttpResponse();
            response.setStatusCode(code);
            response.setStatusCodeText(codeText);
            parsingHeader(response, closeableHttpResponse);
        } catch (IOException e) {
            throw EXPF.exception(ErrorCode.HttpRequestError.getCode(), ErrorCode.HttpRequestError.getTemplate(),null,
                    this.getClass().getSimpleName(),true);
        } finally {
            if (null != closeableHttpResponse) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                }
            }

            if (null != entity) {
                try {
                    entity.getContent().close();
                } catch (IllegalStateException e) {
                } catch (IOException e) {
                }
            }

            if (null != request) {
                request.close();
            }
        }

        return response;
    }

    private void parsingHeader(AliceHttpResponse response, CloseableHttpResponse closeableHttpResponse) throws IOException {
        if (closeableHttpResponse.containsHeader(HttpHeaders.CONTENT_ENCODING)) {
            Header encodingHeader = closeableHttpResponse.getFirstHeader(HttpHeaders.CONTENT_ENCODING);
            response.setEncoding(encodingHeader.getValue());
        }

        if (closeableHttpResponse.containsHeader(HttpHeaders.CONTENT_TYPE)) {
            Header contentTypeHeader = closeableHttpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);

            if (contentTypeHeader.getValue().contains(";")) {
                String[] cType = contentTypeHeader.getValue().split(";");
                response.setContentType(cType[0]);

                String[] charSet = cType[1].split("=");
                response.setEncoding(charSet[1]);
            } else {
                response.setContentType(contentTypeHeader.getValue());
            }
        }
        response.setHeaders(closeableHttpResponse.getAllHeaders());

        HttpEntity entity = closeableHttpResponse.getEntity();
        Header contentType = entity.getContentType();
        Header contentEncoding = entity.getContentEncoding();

        if (null != contentType) {
            if (contentType.getValue().contains(";")) {
                String[] cType = contentType.getValue().split(";");
                response.setContentType(cType[0]);

                String[] charSet = cType[1].split("=");
                response.setEncoding(charSet[1]);
            } else {
                response.setContentType(contentType.getValue());
            }
        }

        byte[] byteArray = EntityUtils.toByteArray(entity);
        response.setContentBytes(byteArray);
    }

}

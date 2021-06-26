package red.asuka.alice.commons.http;

import red.asuka.alice.commons.http.config.AliceHttpDelete;
import red.asuka.alice.commons.support.StringUtility;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/12/19 12:27
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public class AliceHttpRequest {

    private HttpRequestBase httpRequestBase;
    @Getter
    private Map<String, Object> parameter;
    private List<Header> headers;
    @Setter
    @Getter
    private MultipartEntityBuilder multipartEntityBuilder;
    @Getter
    @Setter
    private StringEntity stringEntity;
    @Getter
    @Setter
    private UrlEncodedFormEntity formEntity;

    public enum Type {
        GET, POST, PUT, DELETE
    }
    private Type type;
    private StringBuilder url;

    public AliceHttpRequest(String url) {
        this(Type.GET, url);
    }

    public AliceHttpRequest(Type type, String url) {
        this(type, url, new ArrayList<>());
    }

    public AliceHttpRequest(Type type, String url, List<Header> headers) {
        this.type = type;
        this.url = new StringBuilder(url);
        this.headers = headers;
        this.parameter = new IdentityHashMap<>();
    }

    private void initMultipartEntity() {
        if (null != stringEntity || null != formEntity) {
            return;
        }

        if (!CollectionUtils.isEmpty(parameter)) {
            switch(type) {
                case GET:
                    try {
                        StringUtility.composeUrl(this.url, parameter);
                    } catch (UnsupportedEncodingException e) {

                    }
                    break;
                case DELETE:
                case POST:
                case PUT:
                    multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    multipartEntityBuilder.setCharset(Consts.UTF_8);

                    parameter.forEach((s, o) -> {
                        if (o instanceof File) {
                            multipartEntityBuilder.addBinaryBody(s, (File) o);
                        } else if (o instanceof byte[]) {
                            multipartEntityBuilder.addBinaryBody(s, (byte[]) o);
                        } else {
                            multipartEntityBuilder.addTextBody(s, String.valueOf(o), ContentType.create("text/plain", Consts.UTF_8));
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void initHttpBase() {
        switch(type) {
            case GET:
                httpRequestBase = new HttpGet(URI.create(this.url.toString()));
                break;
            case POST:
                HttpPost post = new HttpPost(URI.create(this.url.toString()));
                setEntity(post);
                httpRequestBase = post;
                break;
            case PUT:
                HttpPut put = new HttpPut(URI.create(this.url.toString()));
                setEntity(put);
                httpRequestBase = put;
                break;
            case DELETE:
                AliceHttpDelete delete = new AliceHttpDelete(URI.create(this.url.toString()));
                setEntity(delete);
                httpRequestBase = delete;
                break;
            default:
                break;
        }
    }

    private void setEntity(HttpEntityEnclosingRequestBase httpRequestBase) {
        if (null != multipartEntityBuilder) {
            httpRequestBase.setEntity(multipartEntityBuilder.build());
        } else if (null != stringEntity) {
            httpRequestBase.setEntity(stringEntity);
        } else if (null != formEntity) {
            httpRequestBase.setEntity(formEntity);
        }
    }

    private void initHeader() {
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(header -> httpRequestBase.addHeader(header));
        }
    }

    public HttpHost getHttpHost() {
        return URIUtils.extractHost(URI.create(this.url.toString()));
    }

    public HttpRequestBase getHttpRequestBase() {
        initMultipartEntity();
        initHttpBase();
        initHeader();
        return httpRequestBase;
    }

    public void close() {
        if (null != httpRequestBase) {
            httpRequestBase.releaseConnection();
        }
    }

    public HttpHost getHttpPost() {
        return URIUtils.extractHost(URI.create(this.url.toString()));
    }

    public void addAllParameter(Map<String, String> parameter) {
        this.parameter.putAll(parameter);
    }

    public void addParameter(String name, String value) {
        this.parameter.put(name, value);
    }

    public void addParameter(String name, File value) {
        this.parameter.put(name, value);
    }

    public void addParameter(Map<String, Object> parameter) {
        this.parameter.putAll(parameter);
    }

    public void addHeader(String name, String value) {
        this.addHeader(new BasicHeader(name, value));
    }

    public void addHeader(Header header) {
        this.headers.add(header);
    }

    public void addHeader(List<Header> headers) {
        this.headers.addAll(headers);
    }

    public void addHeader(Map<String, String> header) {
        header.forEach((key, value) -> this.headers.add(new BasicHeader(key, value)));
    }

}

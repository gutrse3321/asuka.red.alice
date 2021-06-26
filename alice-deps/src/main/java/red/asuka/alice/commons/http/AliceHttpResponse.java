package red.asuka.alice.commons.http;

import red.asuka.alice.commons.exception.EXPF;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/12/20 10:24
 * @Title:
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public class AliceHttpResponse {

    protected  static final Logger log = LoggerFactory.getLogger(AliceHttpResponse.class);

    @Getter
    @Setter
    private int statusCode;
    @Getter
    @Setter
    private String statusCodeText;
    @Getter
    @Setter
    private String encoding;
    @Getter
    @Setter
    private String contentType;
    @Setter
    private String contentString;
    @Setter
    private byte[] contentBytes;
    @Getter
    @Setter
    private Header[] headers;

    public AliceHttpResponse() {
        this(0, Consts.UTF_8.displayName(), null, null, null);
    }

    public AliceHttpResponse(int statusCode, String encoding, String contentType, byte[] contentBytes, Header[] headers) {
        this.statusCode = statusCode;
        this.encoding = encoding;
        this.contentType = contentType;
        this.contentBytes = contentBytes;
        this.headers = headers;
    }

    public AliceHttpResponse(int statusCode, String contentType, byte[] contentBytes) {
        this(statusCode, Consts.UTF_8.displayName(), contentType, contentBytes, null);
    }

    public AliceHttpResponse(int statusCode, String encoding, String contentType) {
        this(statusCode, encoding, contentType, null, null);
    }

    public AliceHttpResponse(int statusCode, String encoding, String contentType, byte[] contentBytes) {
        this(statusCode, encoding, contentType, contentBytes, null);
    }

    public AliceHttpResponse(int statusCode, String contentType, byte[] contentBytes, Header[] headers) {
        this(statusCode, Consts.UTF_8.displayName(), contentType, contentBytes, headers);
    }

    public InputStream getContentStream() {
        InputStream stream = null;

        if (null != contentBytes) {
            stream = new ByteArrayInputStream(contentBytes);
        }
        return stream;
    }

    public String getContentString() {
        if (null != contentBytes) {
            String encoding = this.encoding == null ? Consts.UTF_8.displayName() : this.encoding;

            try {
                contentString = new String(contentBytes, encoding);
            } catch (UnsupportedEncodingException e) {
                log.error(EXPF.getExceptionMsg(e));
            }
        }

        return contentString;
    }

    public boolean containsHeader(final String name) {
        for (int i = 0; i < this.headers.length; i++) {
            final Header header = this.headers[i];

            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Header[] getHeaders(final String name) {
        final List<Header> headersFound = new ArrayList<>();

        for (int i = 0; i < this.headers.length; i++) {
            final Header header = this.headers[i];

            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }

        return headersFound.toArray(new Header[headersFound.size()]);
    }

    public Header getFirstHeader(final String name) {
        for (int i = 0; i < this.headers.length; i++) {
            final Header header = this.headers[i];

            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }

        return null;
    }

    public Header getLastHeader(final String name) {
        for (int i = headers.length - 1; i >= 0; i--) {
            final Header header = headers[i];

            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }

        return null;
    }

}

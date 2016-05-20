package poc.samsung.fido.rp.util;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.HttpHeaders;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpRequest;

public class ApiRequest implements HttpRequest {

    private final String contentType;

    private final String accept;

    private final String userAgent;

    private final String body;

    public ApiRequest(HttpServletRequest request, String body) {
        contentType = request.getContentType();
        accept = request.getHeader(HttpHeaders.ACCEPT);
        userAgent = request.getHeader(HttpHeaders.USER_AGENT);

        this.body = body;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getAccept() {
        return accept;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public String getBody() {
        return body;
    }
}

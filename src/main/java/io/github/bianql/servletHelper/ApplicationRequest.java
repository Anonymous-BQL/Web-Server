package io.github.bianql.servletHelper;

import io.github.bianql.config.MimeConfig;
import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.servletApi.ApplicationDispatcher;
import io.github.bianql.util.http.HttpParser;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationRequest {
    private Map<String, String> header = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, Object> attributes = new HashMap<>();
    private String method;
    private String queryString;
    private String sessionId;
    private String url;
    private String protocol;
    private String characterEncoding = "UTF-8";
    private long contentLength = -1;
    private Cookie[] cookies;
    private ApplicationContext applicationContext;
    private String scheme;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String localName;
    private String localAddr;
    private int localPort;
    private DispatcherType dispatcherType;
    private byte[] body;
    private boolean isFinish;
    private ApplicationResponse response;
    private boolean isSessionIdFormCookie;

    public void setSessionIdFormCookie(boolean sessionIdFormCookie) {
        isSessionIdFormCookie = sessionIdFormCookie;
        response.setCookiesAllowed(sessionIdFormCookie);
    }

    public ApplicationRequest(ApplicationResponse response) {
        this.response = response;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        response.setSessionId(sessionId);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public long getDateHeader(String s) {
        return -1L;
    }

    public String getHeader(String name) {
        return header.get(name);
    }

    public Collection<String> getHeaders(String name) {
        return header.values();
    }

    public Collection<String> getHeaderNames() {
        return header.keySet();
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        throw new RuntimeException("不支持的操作");
    }

    public String getPathTranslated() {
        throw new RuntimeException("不支持的操作");
    }

    public String getContextPath() {
        return applicationContext.getContextPath();
    }

    public String getQueryString() {
        return queryString;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRequestURI() {
        return url;
    }

    public String getHostString() {
        String host = getHeader("Host");
        if (StringUtils.isEmpty(host)) {
            host = getLocalName();
        } else {
            host = host.substring(0, host.indexOf(":") + 1);
        }
        //http:
        return getScheme() + "://" + host + ":" + getLocalPort();
    }

    public StringBuffer getRequestURL() {
        return new StringBuffer(getHostString() + getRequestURI());
    }

    public String getServletPath() {
        return url.replace(applicationContext.getContextPath(), "");
    }

    public HttpSession getSession(boolean allowGenerate) {
        HttpSession session = applicationContext.getSessionManager().getSession(sessionId, allowGenerate);
        if (session != null) {
            sessionId = session.getId();
            response.setSessionId(sessionId);
        }
        return session;
    }

    public boolean isSessionIdFromCookie() {
        return sessionId != null && isSessionIdFormCookie;
    }

    public boolean isSessionIdFromURL() {
        return sessionId != null && !isSessionIdFormCookie;
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        try {
            return aClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Collection<String> getAttributeNames() {
        return attributes.keySet();
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
        if (isFinish)
            return;
        this.characterEncoding = characterEncoding;
    }

    private void parseBody() {
        if (!isFinish) {
            String body = null;
            try {
                body = new String(getBody(), getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (getMethod().equals(HttpMethod.POST.name())) {
                HttpParser.parseParameters(body).forEach(this::addParameter);
            }
            isFinish = true;
        }
    }

    public int getContentLength() {
        String contentLength = getHeader("Content-Length");
        if (StringUtils.isEmpty(contentLength))
            return Long.valueOf(this.contentLength).intValue();
        return Integer.parseInt(contentLength);
    }

    public long getContentLengthLong() {
        String contentLength = getHeader("Content-Length");
        if (StringUtils.isEmpty(contentLength))
            return this.contentLength;
        return Long.valueOf(contentLength);
    }

    public String getContentType() {
        String contentType = getRequestURI().substring(getRequestURI().indexOf(".") + 1);
        if (StringUtils.isEmpty(contentType))
            return MimeConfig.getMimeType("text");
        return MimeConfig.getMimeType(contentType);
    }

    public String[] getParameterValues(String name) {
        parseBody();
        if (parameters.get(name) == null)
            return null;
        List<String> values = parameters.get(name);
        return values.toArray(new String[values.size()]);
    }

    public void addParameter(String name, List<String> value) {
        parameters.put(name, value);
    }

    public void addHeader(String name, String value) {
        header.put(name, value);
    }

    public String getParameter(String name) {
        parseBody();
        List<String> parameter = parameters.get(name);
        return parameter == null ? null : parameter.get(0);
    }

    public Collection<String> getParameterNames() {
        parseBody();
        return parameters.keySet();
    }

    public Map<String, String[]> getParameterMap() {
        parseBody();
        Map<String, String[]> parameterMap = new HashMap<>();
        parameters.forEach((name, value) -> {
            parameterMap.put(name, value.toArray(new String[value.size()]));
        });
        return parameterMap;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getScheme() {
        return scheme;
    }

    public String getServerName() {
        return "WebServer power by bianql.";
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public Locale getLocale() {
        return Locale.SIMPLIFIED_CHINESE;
    }

    public Collection<Locale> getLocales() {
        return Stream.of(Locale.SIMPLIFIED_CHINESE).collect(Collectors.toList());
    }

    public RequestDispatcher getRequestDispatcher(String url) {
        if (response.isCommitted()) {
            throw new RuntimeException("该请求已经被处理！");
        }
        if (StringUtils.isEmpty(url)) {
            url = "/";
        }
        String queryString = null;
        if (url.indexOf("?") > 0) {
            queryString = url.substring(url.indexOf("?") + 1);
        }
        return new ApplicationDispatcher(applicationContext.getServletMapper(), url, queryString, this);
    }

    public String getRealPath(String url) {
        if (url == null)
            throw new IllegalArgumentException("url不能为null");
        return new File(url).getAbsolutePath();
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getLocalName() {
        return localName;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public int getLocalPort() {
        return localPort;
    }

    public ServletContext getServletContext() {
        return applicationContext.getServletContext();
    }

    public DispatcherType getDispatcherType() {
        return dispatcherType;
    }

    public void setDispatcherType(DispatcherType dispatcherType) {
        this.dispatcherType = dispatcherType;
    }


}

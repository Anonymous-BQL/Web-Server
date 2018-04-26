package io.github.bianql.servletHelper;

import io.github.bianql.config.MimeConfig;
import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.host.context.SessionManager;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class ApplicationResponse {
    private OutputStream socketOutput;
    private Map<String, List<String>> headers = new HashMap<>();
    private boolean isAppCommited = false;
    private String sessionId;
    private ApplicationContext applicationContext;
    private String protocol;
    private String hostString;
    private boolean isCookiesAllowed = false;
    private int status;
    private String charEncoding = "utf-8";
    private int bufferSize;
    private Locale locale;
    private ApplicationOutputStream out = new ApplicationOutputStream();
    private String message;

    public ApplicationResponse(OutputStream socketOutput) {
        this.socketOutput = socketOutput;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setHostString(String hostString) {
        if (StringUtils.isEmpty(hostString)) {
            hostString = "localhost";
        } else {
            if (hostString.indexOf(":") > 0) {
                hostString = hostString.substring(0, hostString.indexOf(":"));
            }
        }
        this.hostString = hostString;
    }

    public void setCookiesAllowed(boolean cookiesAllowed) {
        isCookiesAllowed = cookiesAllowed;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addCookie(Cookie cookie) {
        addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; domain=" + hostString + "; path=" + applicationContext.getContextPath() + "; max-age=" + cookie.getMaxAge());
    }

    public byte[] getHeaderBytes() {
        if (!StringUtils.isEmpty(sessionId)) {
            addCookie(SessionManager.createSessionCookie(applicationContext, sessionId, false));
        }
        //解析头部并输出
        out.writeHeader((protocol + " " + status + " " + message + "\r\n").getBytes());
        headers.forEach((name, value) -> {
            value.forEach(v -> {
                try {
                    out.writeHeader((name + ": " + v + "\r\n").getBytes("iso-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        });
        out.writeHeader("\r\n".getBytes());
        return out.getHeaderBytes();
    }

    public boolean containsHeader(String name) {
        return headers.get(name) != null;
    }

    public String encodeURL(String url) {
        if (url != null && !url.startsWith("/"))
            url = "/" + url;
        if (isCookiesAllowed || StringUtils.isEmpty(sessionId))
            return url;
        return url + "?" + SessionManager.getSessionCookieName() + "=" + sessionId;
    }

    public String encodeRedirectURL(String url) {
        return hostString + applicationContext.getContextPath() + encodeURL(url);
    }

    public void sendError(int status, String message) throws IOException {
        if (isAppCommited) {
            return;
        }
        isAppCommited = true;
        this.status = status;
        this.message = message;
        setHeader("Content-Length", "0");
        sendToClient(getHeaderBytes());
    }

    public void sendRedirect(String url) throws IOException {
        if (isAppCommited) {
            return;
        }
        isAppCommited = true;
        this.status = HttpServletResponse.SC_FOUND;
        addHeader("Location", url);
        sendToClient(getHeaderBytes());
    }

    public void setDateHeader(String s, long l) {
        setHeader(s, new Date(l).toGMTString());
    }

    public void addDateHeader(String s, long l) {
        addHeader(s, new Date(l).toGMTString());
    }

    public void setHeader(String s, String s1) {
        headers.put(s, Arrays.asList(s1));
    }

    public void addHeader(String s, String s1) {
        if (headers.get(s) == null)
            setHeader(s, s1);
        else
            headers.get(s).add(s1);
    }

    public void setIntHeader(String s, int i) {
        headers.put(s, Arrays.asList(String.valueOf(i)));
    }

    public void removeHeader(String header) {
        headers.remove(header);
    }

    public void setStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getHeader(String name) {
        List<String> values = headers.get(name);
        return values == null ? null : headers.get(name).get(0);
    }

    public byte[] getBodyBytes() {
        return out.getBodyBytes();
    }

    public Collection<String> getHeaders(String s) {
        return headers.get(s);
    }

    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getCharacterEncoding() {
        return charEncoding;
    }

    public String getContentType() {
        return headers.get("Content-Type").get(0);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(out, true);
    }

    public void setCharacterEncoding(String s) {
        charEncoding = s;
    }

    public void setContentLength(int i) {
        setContentLengthLong(Long.valueOf(i));
    }

    public void sendToClient(byte[] httpData) {
        try {
            socketOutput.write(httpData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContentLengthLong(long l) {
        setHeader("Content-Length", String.valueOf(l));
    }

    public void setContentType(String s) {
        if (!StringUtils.isEmpty(s))
            setHeader("Content-Type", s);
        else
            setHeader("Content-Type", MimeConfig.getMimeType("text"));
    }

    public void setBufferSize(int i) {
        bufferSize = i;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void flushBuffer() throws IOException {

    }

    public void resetBuffer() {

    }

    public boolean isCommitted() {
        return isAppCommited;
    }

    public void reset() {

    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    private class ApplicationOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private ByteArrayOutputStream headerOutput = new ByteArrayOutputStream();
        private boolean sending = false;

        @Override
        public boolean isReady() {
            throw new RuntimeException("未实现操作");
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new RuntimeException("未实现操作");
        }

        public void writeHeader(byte[] header) {
            try {
                headerOutput.write(header);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public byte[] getHeaderBytes() {
            return headerOutput.toByteArray();
        }

        public byte[] getBodyBytes() {
            return outputStream.toByteArray();
        }

        @Override
        public void write(int b) throws IOException {
            if (isAppCommited) {
                return;
            }
            outputStream.write(b);
            if (!(outputStream.size() < 50 * 1024 * 1024)) {
                if (!sending) {
                    sending = true;
                    isAppCommited = true;
                    sendToClient(ApplicationResponse.this.getHeaderBytes());
                }
                sendToClient(outputStream.toByteArray());
                outputStream.reset();
            }
        }
    }
}

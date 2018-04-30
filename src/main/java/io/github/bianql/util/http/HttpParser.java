package io.github.bianql.util.http;

import io.github.bianql.exception.FailedParseParameterException;
import io.github.bianql.host.context.SessionManager;
import io.github.bianql.servletHelper.ApplicationRequest;
import io.github.bianql.servletHelper.ApplicationResponse;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.DispatcherType;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class HttpParser {
    public static Map<String, List<String>> parseParameters(String parameterString) {
        if (StringUtils.isEmpty(parameterString))
            return null;
        Map<String, List<String>> result = new HashMap<>();
        String[] parameter = parameterString.split("&");
        for (String s : parameter) {
            String[] param = s.split("=");
            if (param.length < 2)
                throw new FailedParseParameterException("参数无法解析");
            String name = param[0], value = param[1];
            if (CollectionUtils.isEmpty(result.get(name))) {
                result.put(name, Arrays.asList(value));
            } else {
                result.get(name).add(value);
            }
        }
        return result;
    }

    public static boolean parseHttp(ApplicationRequest request, ApplicationResponse response, Socket socket) {
        try {
            request.setDispatcherType(DispatcherType.REQUEST);
            request.setLocalPort(socket.getLocalPort());
            request.setRemotePort(socket.getPort());
            request.setLocalName(socket.getLocalAddress().getHostName());
            request.setLocalAddr(socket.getLocalAddress().getHostAddress());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            List<String> headerInfo = new ArrayList<>();
            while (!StringUtils.isEmpty(line = reader.readLine())) {
                headerInfo.add(line);
            }
            parseQueryLine(request, response, headerInfo.get(0));
            response.setProtocol(request.getProtocol());
            headerInfo.remove(0);
            parseHeader(request, headerInfo);
            if (request.getContentLength() > 0) {
                char[] body = new char[request.getContentLength()];
                reader.read(body);
                request.setBody(new String(body).getBytes());
            } else if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
                request.setBody(reader.readLine().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }

    private static void parseQueryLine(ApplicationRequest request, ApplicationResponse response, String queryLine) throws IOException {
        String[] info = queryLine.split("\\s+");
        if (info.length < 3 || HttpMethod.resolve(info[0]) == null)
            throw new FailedParseParameterException("解析请求行失败！");
        request.setMethod(info[0]);
        String url = info[1], queryString = null;
        if (info[1].contains("?")) {
            url = info[1].split("\\?")[0];
            queryString = info[1].substring(info[1].indexOf("?") + 1);
        }
        request.setUrl(url);
        request.setScheme(info[2].split("/")[0]);
        request.setProtocol(info[2]);
        if ("http".equalsIgnoreCase(info[2])) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Protocol error.");
            throw new RuntimeException("不支持的协议");
        }
        request.setQueryString(queryString);
        if (!StringUtils.isEmpty(queryString))
            parseParameters(queryString).forEach((name, values) -> {
                if (SessionManager.getSessionCookieName().equalsIgnoreCase(name)) {
                    request.setSessionId(values.get(0));
                    request.setSessionIdFormCookie(false);
                }
                request.addParameter(name, values);
            });
    }

    private static void parseHeader(ApplicationRequest request, List<String> headerInfo) {
        headerInfo.forEach(header -> {
            String[] line = header.split(":\\s+");
            if (line.length < 2)
                throw new FailedParseParameterException("头部解析失败！");
            request.addHeader(line[0], line[1]);
            if ("Cookie".equals(line[0])) {
                parseCookies(request, line[1]);
            }
            if ("Content-Length".equals(line[0])) {
                request.setContentLength(Long.valueOf(line[1]));
            }
        });
    }

    private static void parseCookies(ApplicationRequest request, String cookies) {
        List<Cookie> cookieList = new ArrayList<>();
        Arrays.asList(cookies.split(";")).forEach(cookie -> {
            String[] c = cookie.split("=");
            if (SessionManager.getSessionCookieName().equalsIgnoreCase(c[0])) {
                request.setSessionIdFormCookie(true);
                request.setSessionId(c[1]);
            }
            if (isToken(c[0]))
                cookieList.add(new Cookie(c[0], c[1]));
        });
        request.setCookies(cookieList.toArray(new Cookie[cookieList.size()]));
    }

    private static boolean isToken(String value) {
        int len = value.length();
        String illegalString = "/()<>@,;:\\\"[]?={} \t";
        for (int i = 0; i < len; ++i) {
            char c = value.charAt(i);
            if (c < ' ' || c >= 127 || illegalString.indexOf(c) != -1) {
                return false;
            }
        }

        return true;
    }

}

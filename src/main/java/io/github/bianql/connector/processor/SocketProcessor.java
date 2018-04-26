package io.github.bianql.connector.processor;

import io.github.bianql.connector.Mapper.ContextMapper;
import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.servletApi.Request;
import io.github.bianql.servletApi.Response;
import io.github.bianql.servletHelper.ApplicationRequest;
import io.github.bianql.servletHelper.ApplicationResponse;
import io.github.bianql.util.http.HttpParser;
import org.springframework.util.StringUtils;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Arrays;

public class SocketProcessor implements Runnable {
    private Socket socket;

    public SocketProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //解析请求为ApplicationRequest
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            closeSocket();
            return;
        }
        ApplicationResponse applicationResponse = new ApplicationResponse(outputStream);
        ApplicationRequest applicationRequest = new ApplicationRequest(applicationResponse);
        if (!HttpParser.parseHttp(applicationRequest, applicationResponse, socket)) {
            closeSocket();
            return;
        }
        //初始化ApplicationResponse
        applicationResponse.setStatus(HttpServletResponse.SC_OK, "OK");
        applicationResponse.setContentType(applicationRequest.getContentType());
        applicationResponse.setHeader("Server", "Web Server.");
        applicationResponse.setDateHeader("Date", Instant.now().toEpochMilli());
        String connectionString = applicationRequest.getHeader("Connection");
        if (!StringUtils.isEmpty(connectionString)) {
            applicationResponse.addHeader("Connection", connectionString);
        }
        applicationResponse.setHostString(applicationRequest.getHeader("Host"));
        //根据URL判断ApplicationContext
        ApplicationContext applicationContext = ContextMapper.getContextByUrl(applicationRequest.getRequestURI());
        try {
            if (applicationContext == null) {
                applicationResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND");
                closeSocket();
                return;
            }
            applicationRequest.setApplicationContext(applicationContext);
            applicationResponse.setApplicationContext(applicationContext);
            applicationResponse.setHeader("Server", applicationContext.getServletContext().getServerInfo());
        } catch (Exception e) {
            e.printStackTrace();
            //抛弃服务器自身异常
        }
        try {
            //映射请求并调用Servlet处理
            String servletUrl = applicationRequest.getRequestURI().replace(applicationContext.getContextPath(), "");
            applicationContext.getServletMapper().getFilterChainByUrl(servletUrl, DispatcherType.REQUEST).doFilter(new Request(applicationRequest), new Response(applicationResponse));
        } catch (Exception e) {
            e.printStackTrace();
            //发送服务器内部错误
            applicationResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            try {
                applicationResponse.getWriter().println(e.toString());
                Arrays.asList(e.getStackTrace()).forEach(stackTraceElement -> {
                    try {
                        applicationResponse.getWriter().println("\tat" + stackTraceElement.toString());
                    } catch (IOException e1) {
                    }
                });
            } catch (IOException e1) {
                e1.printStackTrace();
                //抛弃服务器自身异常
            }
        }
        try {
            //根据协议，相应客户端
            byte[] body = applicationResponse.getBodyBytes();
            byte[] header = null;
            if (!applicationResponse.isCommitted()) {
                applicationResponse.setHeader("Content-Length", String.valueOf(body.length));
                header = applicationResponse.getHeaderBytes();
                applicationResponse.sendToClient(header);
            }
            applicationResponse.sendToClient(new String(body).getBytes());
            closeSocket();
        } catch (Exception e) {
            //抛弃服务器自身处理异常
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            if (socket.isConnected())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

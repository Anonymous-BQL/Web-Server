package io.github.bianql.servletHelper;

import io.github.bianql.host.context.ApplicationContext;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DefaultServlet implements Servlet {
    private ApplicationContext applicationContext;

    public DefaultServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String fileName = request.getServletContext().getRealPath(request.getServletPath());
        if (StringUtils.isEmpty(fileName)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "NOT FOUND");
            return;
        }
        if (request.getServletPath().contains("WEB-INF")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        InputStream inputStream = null;
        File objectFile = new File(fileName);
        if (objectFile.exists() && objectFile.isFile()) {
            response.setContentLengthLong(objectFile.length());
            inputStream = request.getServletContext().getResourceAsStream(request.getServletPath());
            byte[] b = new byte[1024];
            OutputStream out = response.getOutputStream();
            int count = 0;
            while ((count = inputStream.read(b)) > 0) {
                out.write(b, 0, count);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "NOT FOUND");
        }
        if (inputStream != null)
            inputStream.close();
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}

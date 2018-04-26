package io.github.bianql.servletApi;

import io.github.bianql.host.ServletMapper;
import io.github.bianql.servletHelper.ApplicationRequest;
import io.github.bianql.util.http.HttpParser;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;

public class ApplicationDispatcher implements RequestDispatcher {
    private ServletMapper servletMapper;
    private String url;
    private String queryString;
    private String servletName;
    private ApplicationRequest applicationRequest;

    public ApplicationDispatcher(ServletMapper servletMapper, String url, String queryString, ApplicationRequest applicationRequest) {
        this.servletMapper = servletMapper;
        this.queryString = queryString;
        this.url = url.startsWith("/") ? url : "/" + url;
        this.applicationRequest = applicationRequest;
    }

    public ApplicationDispatcher(ServletMapper servletMapper, String servletName) {
        this.servletMapper = servletMapper;
        this.servletName = servletName;
    }

    private boolean isEmptyQuery(String query) {
        return query == null || "".equals(query.trim());
    }

    @Override
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if(applicationRequest == null){
            applicationRequest = ((Request)servletRequest).getRequest();
        }
        applicationRequest.setDispatcherType(DispatcherType.FORWARD);
        if (servletName != null) {
            servletMapper.getFilterChainByServletName(servletName, DispatcherType.FORWARD).doFilter(servletRequest, servletResponse);
        } else if (!StringUtils.isEmpty(url)) {
            //根据query填充parameter
            fillParameter();
            servletMapper.getFilterChainByUrl(url, DispatcherType.FORWARD).doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if(applicationRequest == null){
            applicationRequest = ((Request)servletRequest).getRequest();
        }
        applicationRequest.setDispatcherType(DispatcherType.INCLUDE);
        if (servletName != null) {
            servletMapper.getFilterChainByServletName(servletName, DispatcherType.INCLUDE).doFilter(servletRequest, servletResponse);
        } else if (!StringUtils.isEmpty(url)) {
            //根据query填充parameter
            fillParameter();
            servletMapper.getFilterChainByUrl(url, DispatcherType.INCLUDE).doFilter(servletRequest, servletResponse);
        }
    }

    private void fillParameter() {
        if (!isEmptyQuery(queryString))
            HttpParser.parseParameters(queryString).forEach((name, value) -> {
                applicationRequest.addParameter(name, value);
            });
    }
}

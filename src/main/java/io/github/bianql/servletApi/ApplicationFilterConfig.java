package io.github.bianql.servletApi;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

public class ApplicationFilterConfig implements FilterConfig {
    private String filterName;
    private ServletContext servletContext;
    private Map<String,String> initParameter;

    public ApplicationFilterConfig(String filterName, ServletContext servletContext, Map<String, String> initParameter) {
        this.filterName = filterName;
        this.servletContext = servletContext;
        this.initParameter = initParameter;
    }

    @Override
    public String getFilterName() {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getInitParameter(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;
    }
}

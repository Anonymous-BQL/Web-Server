package io.github.bianql.servletApi;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

public class ApplicationServletConfig implements ServletConfig {
    private String servletName;
    private ServletContext servletContext;
    private Map<String, String> initParameters;

    public ApplicationServletConfig(String servletName, ServletContext servletContext, Map<String, String> initParameters) {
        this.servletName = servletName;
        this.servletContext = servletContext;
        this.initParameters = initParameters;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }
}

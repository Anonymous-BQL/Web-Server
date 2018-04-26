package io.github.bianql.servletHelper;

import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.servletApi.ApplicationServletConfig;

import javax.servlet.*;
import java.util.*;

public class ServletRegistrationWrapper {
    private int loadOnStartup;
    private ServletSecurityElement servletSecurity;
    private MultipartConfigElement multipartConfigElement;
    private String runAsRole;
    private boolean asyncSupported;
    private Set<String> servletMapping = new HashSet<>();
    private Map<String, String> initParameters = new HashMap<>();
    private String servletName;
    private String className;
    private Servlet servlet;
    private ApplicationContext applicationContext;
    private Class servletClass;
    private volatile Boolean firstInvoke = Boolean.TRUE;

    public boolean isFirstInvoke() {
        return firstInvoke;
    }

    public boolean changeFirstInvoke() {
        if (firstInvoke == Boolean.TRUE) {
            synchronized (firstInvoke) {
                if (firstInvoke == Boolean.TRUE) {
                    firstInvoke = Boolean.FALSE;
                    return true;
                }
            }
        }
        return false;
    }

    public ServletRegistrationWrapper(ApplicationContext applicationContext, String servletName, String className, Class servletClass, Servlet servlet) {
        this.applicationContext = applicationContext;
        this.servletName = servletName;
        this.className = className;
        this.servletClass = servletClass;
        this.servlet = servlet;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public Set<String> setServletSecurity(ServletSecurityElement servletSecurity) {
        this.servletSecurity = servletSecurity;
        return null;
    }

    public void setMultipartConfig(MultipartConfigElement multipartConfigElement) {
        this.multipartConfigElement = multipartConfigElement;
    }

    public String getRunAsRole() {
        return runAsRole;
    }

    public void setRunAsRole(String runAsRole) {
        this.runAsRole = runAsRole;
    }

    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public Set<String> getServletMapping() {
        return servletMapping;
    }

    public Set<String> addServletMapping(String... urlPattern) {
        if (urlPattern == null)
            return Collections.emptySet();
        Set<String> conflicts = new HashSet<>();
        if (applicationContext.getServletMapper() != null)
            for (String url : urlPattern) {
                if (applicationContext.getServletMapper().findServletByUrl(url) != null) {
                    conflicts.add(url);
                }
            }
        if (!conflicts.isEmpty()) {
            return conflicts;
        }
        servletMapping.addAll(Arrays.asList(urlPattern));
        return Collections.emptySet();
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    public Set<String> setInitParameters(Map<String, String> initParameters) {

        Set<String> conflicts = new HashSet<>();

        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException("初始化参数不能包含null对象！");
            }
            if (getInitParameter(entry.getKey()) != null) {
                conflicts.add(entry.getKey());
            }
        }
        if (conflicts.isEmpty()) {
            for (Map.Entry<String, String> entry : initParameters.entrySet()) {
                setInitParameter(entry.getKey(), entry.getValue());
            }
        }

        return conflicts;
    }

    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("初始化参数不能包含null对象");
        }
        if (getInitParameter(name) != null) {
            return false;
        }
        initParameters.put(name, value);
        return true;
    }

    public String getInitParameter(String key) {
        return initParameters.get(key);
    }

    public String getServletName() {
        return servletName;
    }

    public String getClassName() {
        return className;
    }

    public Servlet getServlet() {
        if (servlet != null)
            return servlet;
        if (servletClass != null) {
            try {
                return applicationContext.getServletContext().createServlet(servletClass);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        if (className != null) {
            try {
                return applicationContext.getServletContext().createServlet((Class<Servlet>) applicationContext.getClassLoader().loadClass(className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ServletContext getServletContext() {
        return applicationContext.getServletContext();
    }

    public Class getServletClass() {
        return servletClass;
    }

    public ServletConfig getServletConfig() {
        return new ApplicationServletConfig(servletName, getServletContext(), initParameters);
    }
}

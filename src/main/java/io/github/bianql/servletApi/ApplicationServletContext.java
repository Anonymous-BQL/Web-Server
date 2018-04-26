package io.github.bianql.servletApi;

import io.github.bianql.config.MimeConfig;
import io.github.bianql.connector.Mapper.ContextMapper;
import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.server.Constants;
import io.github.bianql.servletHelper.FilterRegistrationWrapper;
import io.github.bianql.servletHelper.ServletRegistrationWrapper;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationServletContext implements ServletContext {
    private ApplicationContext applicationContext;
    private Map<String, String> initParameters = Collections.synchronizedMap(new HashMap<>());
    private Map<String, Object> attributes = Collections.synchronizedMap(new HashMap<>());
    private Set<SessionTrackingMode> sessionTrackingModes = null;
    private Set<SessionTrackingMode> supportedSessionTrackingModes = null;
    private SessionCookieConfig sessionCookieConfig;

    public ApplicationServletContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initSessionTrackingMode();
        sessionCookieConfig = new ApplicationSessionCookieConfig(null, getContextPath());
    }

    private void initSessionTrackingMode() {
        supportedSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        supportedSessionTrackingModes.add(SessionTrackingMode.COOKIE);
    }

    @Override
    public String getContextPath() {
        return applicationContext.getContextPath();
    }

    @Override
    public javax.servlet.ServletContext getContext(String url) {
        if (url == null || !url.startsWith("/"))
            return null;
        ApplicationContext context = ContextMapper.getContextByUrl(url);
        if (context != null)
            return context.getServletContext();
        return null;
    }

    @Override
    public int getMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    @Override
    public String getMimeType(String s) {
        if (s.indexOf(".") > 0) {
            s = s.substring(s.indexOf(".") + 1);
        }
        return MimeConfig.getMimeType(s);
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        if (path == null)
            return null;
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("路径参数非法，必须以'/'开头");
        }
        File directory = new File(getRealPath(path));
        if (!directory.exists())
            return null;
        if (directory.isDirectory()) {
            return Stream.of(directory.listFiles()).map(file -> path + file.getAbsolutePath().replace(directory.getAbsolutePath(), "")).collect(Collectors.toSet());
        }
        return Stream.of(path).collect(Collectors.toSet());
    }

    @Override
    public URL getResource(String url) throws MalformedURLException {
        url = getRealPath(url);
        if (url == null)
            throw new MalformedURLException("url无法处理！");
        return new File(url).toURI().toURL();
    }

    @Override
    public InputStream getResourceAsStream(String url) {
        url = getRealPath(url);
        if (StringUtils.isEmpty(url))
            return null;
        File resource = new File(url);
        if (resource.exists()) {
            try {
                return new FileInputStream(resource);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String url) {
        return new ApplicationDispatcher(applicationContext.getServletMapper(), url, url.substring(url.indexOf("?") + 1), null);
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String servletName) {
        return new ApplicationDispatcher(applicationContext.getServletMapper(), servletName);
    }

    @Override
    public Servlet getServlet(String servletName) throws ServletException {
        return applicationContext.getServletMapper().findServletByName(servletName);
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return Collections.enumeration(applicationContext.getServletMapper().getServlets());
    }

    @Override
    public Enumeration<String> getServletNames() {
        return Collections.enumeration(applicationContext.getServletMapper().getServletNames());
    }

    @Override
    public void log(String s) {
        applicationContext.getAppLogger().info(s);
    }

    @Override
    public void log(Exception e, String s) {
        applicationContext.getAppLogger().error(s, e);
    }

    @Override
    public void log(String s, Throwable throwable) {
        applicationContext.getAppLogger().error(s, throwable);
    }

    @Override
    public String getRealPath(String url) {
        if (url == null || !url.startsWith("/"))
            return null;
        return Constants.WEBAPP_DIR + getContextPath() + url;
    }

    @Override
    public String getServerInfo() {
        return "Web Server power by bianql.";
    }

    @Override
    public String getInitParameter(String key) {
        return initParameters.get(key);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }

    @Override
    public boolean setInitParameter(String key, String value) {
        if (key != null) {
            initParameters.put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public String getServletContextName() {
        return getContextPath().substring(1);
    }

    private ServletRegistration.Dynamic addServlet(String servletName, String className, Class servletClass, Servlet servlet) {
        if (StringUtils.isEmpty(servletName)) {
            throw new IllegalArgumentException("Servlet名称不能为空!");
        }
        ServletRegistrationWrapper servletRegistrationWrapper = new ServletRegistrationWrapper(applicationContext, servletName, className, servletClass, servlet);
        applicationContext.addServletRegistration(servletName, servletRegistrationWrapper);
        return new ApplicationServletRegistration(servletRegistrationWrapper);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {

        return addServlet(servletName, className, null, null);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return addServlet(servletName, null, null, servlet);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, null, servletClass, null);
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> servletClass) throws ServletException {
        try {
            return servletClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return new ApplicationServletRegistration(applicationContext.getServletRegistration(servletName));
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        Map<String, ServletRegistration> servletRegistrations = new HashMap<>();
        applicationContext.getServletRegistrations().forEach((key, value) -> {
            servletRegistrations.put(key, new ApplicationServletRegistration(value));
        });
        return servletRegistrations;
    }

    private FilterRegistration.Dynamic addFilter(String filterName, String className, Class filterClass, Filter filter) {
        if (StringUtils.isEmpty(filterName)) {
            throw new IllegalArgumentException("Filter名称不能为空!");
        }
        FilterRegistrationWrapper filterRegistrationWrapper = new FilterRegistrationWrapper(applicationContext, filterName, className, filterClass, filter);
        applicationContext.addFilterRegistration(filterName, filterRegistrationWrapper);
        return new ApplicationFilterRegistration(filterRegistrationWrapper);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return addFilter(filterName, className, null, null);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return addFilter(filterName, null, null, filter);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, null, filterClass, null);
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> filterClass) throws ServletException {
        try {
            return filterClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return new ApplicationFilterRegistration(applicationContext.getFilterRegistration(filterName));
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        Map<String, FilterRegistration> filterRegistrations = new HashMap<>();
        applicationContext.getFilterRegistrations().forEach((key, value) -> {
            filterRegistrations.put(key, new ApplicationFilterRegistration(value));
        });
        return filterRegistrations;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return sessionCookieConfig;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        for (SessionTrackingMode sessionTrackingMode : sessionTrackingModes) {
            if (!supportedSessionTrackingModes.contains(sessionTrackingMode)) {
                throw new IllegalArgumentException("不支持的session追踪策略" + sessionTrackingModes.toString());
            }
        }
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return supportedSessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return sessionTrackingModes == null ? supportedSessionTrackingModes : sessionTrackingModes;
    }

    @Override
    public void addListener(String className) {
        if (StringUtils.isEmpty(className))
            throw new IllegalArgumentException("class名称非法！");
        try {
            Object listener = createListener((Class<EventListener>) getClassLoader().loadClass(className));
            if (!(listener instanceof EventListener)) {
                throw new IllegalArgumentException("仅支持EventListener类型");
            }
            addListener((EventListener) listener);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> void addListener(T listener) {
        applicationContext.getListenerManager().addListener(listener);
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        try {
            addListener(createListener(listenerClass));
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> listener) throws ServletException {
        try {
            return listener.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException("该服务器不支持jsp！");
    }

    @Override
    public ClassLoader getClassLoader() {
        ClassLoader result = applicationContext.getClassLoader();
        if (result == null) {
            result = Thread.currentThread().getContextClassLoader();
        }
        return result;
    }

    @Override
    public void declareRoles(String... strings) {
        throw new UnsupportedOperationException("不支持在context中定义角色！");
    }

    @Override
    public String getVirtualServerName() {
        return "localhost" + getContextPath();
    }
}

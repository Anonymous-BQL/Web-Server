package io.github.bianql.host.context;

import io.github.bianql.host.ServletMapper;
import io.github.bianql.log.AppLogger;
import io.github.bianql.server.Constants;
import io.github.bianql.servletApi.ApplicationServletContext;
import io.github.bianql.servletHelper.DefaultServlet;
import io.github.bianql.servletHelper.FilterRegistrationWrapper;
import io.github.bianql.servletHelper.ServletContainerEvent;
import io.github.bianql.servletHelper.ServletRegistrationWrapper;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private String contextPath;
    private ServletContext servletContext;
    private ServletMapper servletMapper;
    private ListenerManager listenerManager = new ListenerManager();
    private AppLogger appLogger;
    private ClassLoader classLoader;
    private Map<String, ServletRegistrationWrapper> servletRegistrations = Collections.synchronizedMap(new HashMap<>());
    private Map<String, FilterRegistrationWrapper> filterRegistrations = Collections.synchronizedMap(new HashMap<>());
    private SessionManager sessionManager;

    public ApplicationContext(String contextPath) {
        if (!contextPath.startsWith("/"))
            contextPath = "/" + contextPath;
        this.contextPath = contextPath;
        appLogger = new AppLogger(Constants.SERVER_ROOT_DIR + "/log" + contextPath + ".log");
        sessionManager = new SessionManager(this);
        servletContext = new ApplicationServletContext(this);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void addServletRegistration(String servletName, ServletRegistrationWrapper servletRegistration) {
        servletRegistrations.put(servletName, servletRegistration);
    }

    public void addFilterRegistration(String filterName, FilterRegistrationWrapper filterRegistration) {
        filterRegistrations.put(filterName, filterRegistration);
    }

    public Map<String, ServletRegistrationWrapper> getServletRegistrations() {
        return servletRegistrations;
    }

    public ServletRegistrationWrapper getServletRegistration(String servletName) {
        return servletRegistrations.get(servletName);
    }

    public Map<String, FilterRegistrationWrapper> getFilterRegistrations() {
        return filterRegistrations;
    }

    public FilterRegistrationWrapper getFilterRegistration(String filterName) {
        return filterRegistrations.get(filterName);
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void initServletMapper() {
        servletMapper = new ServletMapper(filterRegistrations.values(), servletRegistrations.values(), new DefaultServlet(this));
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public String getContextPath() {
        return contextPath;
    }

    public AppLogger getAppLogger() {
        return appLogger;
    }

    public ServletMapper getServletMapper() {
        return servletMapper;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void publishEvent(ServletContainerEvent event) {
        listenerManager.publishEvent(event);
    }
}

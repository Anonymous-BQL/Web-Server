package io.github.bianql.servletApi;

import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.servletHelper.ServletContainerEvent;
import io.github.bianql.servletHelper.ServletEventType;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Session implements HttpSession {
    private String id;
    private long creationTime;
    private long lastAccessedTime;
    private ApplicationContext context;
    private int maxInactiveInterval = 60 * 60;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean newSession = true;

    public Session(String id, long creationTime, long lastAccessedTime, ApplicationContext context) {
        this.id = id;
        this.creationTime = creationTime;
        this.lastAccessedTime = lastAccessedTime;
        this.context = context;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public void changeNewStatus() {
        this.newSession = false;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        maxInactiveInterval = i;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        context.publishEvent(new ServletContainerEvent(ServletEventType.SESSION_ADD_ATTR, new HttpSessionBindingEvent(this, s, o)));
        attributes.put(s, o);
    }

    @Override
    public void putValue(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {
        context.publishEvent(new ServletContainerEvent(ServletEventType.SESSION_RM_ATTR, new HttpSessionBindingEvent(this, s)));
        attributes.remove(s);
    }

    @Override
    public void removeValue(String s) {

    }

    @Override
    public void invalidate() {
        context.getSessionManager().invalidateSession(getId());
    }

    @Override
    public boolean isNew() {
        return newSession;
    }
}

package io.github.bianql.host.context;

import io.github.bianql.exception.UnsupportedListenerException;
import io.github.bianql.servletHelper.ServletContainerEvent;
import io.github.bianql.servletHelper.ServletEventType;

import javax.servlet.*;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;

public class ListenerManager {
    private Map<Class, List<EventListener>> listeners = new HashMap();

    public void addListener(EventListener listener) {
        if (ServletContextListener.class.isInstance(listener)) {
            addListener(ServletContextListener.class, listener);
        } else if (ServletContextAttributeListener.class.isInstance(listener)) {
            addListener(ServletContextAttributeListener.class, listener);
        } else if (HttpSessionListener.class.isInstance(listener)) {
            addListener(HttpSessionListener.class, listener);
        } else if (HttpSessionAttributeListener.class.isInstance(listener)) {
            addListener(HttpSessionAttributeListener.class, listener);
        } else if (ServletRequestListener.class.isInstance(listener)) {
            addListener(ServletRequestListener.class, listener);
        } else if (ServletRequestAttributeListener.class.isInstance(listener)) {
            addListener(ServletRequestAttributeListener.class, listener);
        } else {
            throw new UnsupportedListenerException("不支持的该监听器类型：" + listener.getClass().getName());
        }
    }

    private void addListener(Class listenerClass, EventListener listener) {
        if (listeners.get(listenerClass) == null) {
            List<EventListener> listenerList = new ArrayList<>();
            listenerList.add(listener);
            listeners.put(listenerClass, listenerList);
        } else {
            listeners.get(listenerClass).add(listener);
        }
    }

    private List getListeners(Class listenerClass) {
        return listeners.get(listenerClass);
    }

    public void publishEvent(ServletContainerEvent event) {
        ServletEventType eventType = event.getEventType();
        if (eventType == ServletEventType.REQUEST_ADD_ATTR) {
            listeners.get(ServletRequestAttributeListener.class).forEach(listener -> {
                ServletRequestAttributeListener.class.cast(listener).attributeAdded((ServletRequestAttributeEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.REQUEST_CREATE) {
            listeners.get(ServletRequestListener.class).forEach(listener -> {
                ServletRequestListener.class.cast(listener).requestInitialized((ServletRequestEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.REQUEST_DESTORY) {
            listeners.get(ServletRequestListener.class).forEach(listener -> {
                ServletRequestListener.class.cast(listener).requestDestroyed((ServletRequestEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.REQUEST_MD_ATTR) {
            listeners.get(ServletRequestAttributeListener.class).forEach(listener -> {
                ServletRequestAttributeListener.class.cast(listener).attributeReplaced((ServletRequestAttributeEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.REQUEST_RM_ATTR) {
            listeners.get(ServletRequestAttributeListener.class).forEach(listener -> {
                ServletRequestAttributeListener.class.cast(listener).attributeRemoved((ServletRequestAttributeEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SERVLET_ADD_ATTR) {
            listeners.get(ServletContextAttributeListener.class).forEach(listener -> {
                ServletContextAttributeListener.class.cast(listener).attributeAdded((ServletContextAttributeEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SERVLET_CREATE) {
            listeners.get(ServletContextListener.class).forEach(listener -> {
                ServletContextListener.class.cast(listener).contextInitialized((ServletContextEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SERVLET_DESTORY) {
            listeners.get(ServletContextListener.class).forEach(listener -> {
                ServletContextListener.class.cast(listener).contextDestroyed((ServletContextEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SERVLET_MD_ATTR) {
            listeners.get(ServletContextAttributeListener.class).forEach(listener -> {
                ServletContextAttributeListener.class.cast(listener).attributeReplaced((ServletContextAttributeEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SERVLET_RM_ATTR) {
            listeners.get(ServletContextAttributeListener.class).forEach(listener -> {
                ServletContextAttributeListener.class.cast(listener).attributeRemoved((ServletContextAttributeEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SESSION_ADD_ATTR) {
            listeners.get(HttpSessionAttributeListener.class).forEach(listener -> {
                HttpSessionAttributeListener.class.cast(listener).attributeAdded((HttpSessionBindingEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SESSION_CREATE) {
            listeners.get(HttpSessionListener.class).forEach(listener -> {
                HttpSessionListener.class.cast(listener).sessionCreated((HttpSessionEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SESSION_DESTORY) {
            listeners.get(HttpSessionListener.class).forEach(listener -> {
                HttpSessionListener.class.cast(listener).sessionDestroyed((HttpSessionEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SESSION_MD_ATTR) {
            listeners.get(HttpSessionAttributeListener.class).forEach(listener -> {
                HttpSessionAttributeListener.class.cast(listener).attributeReplaced((HttpSessionBindingEvent) event.getSource());
            });
        } else if (eventType == ServletEventType.SESSION_RM_ATTR) {
            listeners.get(HttpSessionAttributeListener.class).forEach(listener -> {
                HttpSessionAttributeListener.class.cast(listener).attributeRemoved((HttpSessionBindingEvent) event.getSource());
            });
        }
    }
}

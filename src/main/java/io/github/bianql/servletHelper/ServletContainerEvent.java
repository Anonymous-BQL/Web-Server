package io.github.bianql.servletHelper;

import org.springframework.context.ApplicationEvent;

import java.util.EventObject;

public class ServletContainerEvent extends ApplicationEvent {
    private ServletEventType eventType;
    public ServletContainerEvent(ServletEventType eventType, EventObject source) {
        super(source);
    }

    public ServletEventType getEventType() {
        return eventType;
    }
}

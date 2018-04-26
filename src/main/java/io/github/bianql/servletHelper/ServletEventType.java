package io.github.bianql.servletHelper;

public enum ServletEventType {
    SERVLET_CREATE("servletEvent"),
    SERVLET_DESTORY("servletEvent"),
    SERVLET_ADD_ATTR("servletEvent"),
    SERVLET_RM_ATTR("servletEvent"),
    SERVLET_MD_ATTR("servletEvent"),
    SESSION_CREATE("sessionEvent"),
    SESSION_DESTORY("sessionEvent"),
    SESSION_ADD_ATTR("sessionEvent"),
    SESSION_RM_ATTR("sessionEvent"),
    SESSION_MD_ATTR("sessionEvent"),
    REQUEST_CREATE("requestEvent"),
    REQUEST_DESTORY("requestEvent"),
    REQUEST_ADD_ATTR("requestEvent"),
    REQUEST_RM_ATTR("requestEvent"),
    REQUEST_MD_ATTR("requestEvent"),
    ;
    private String action;

    ServletEventType(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

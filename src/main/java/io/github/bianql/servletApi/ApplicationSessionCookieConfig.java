package io.github.bianql.servletApi;

import javax.servlet.SessionCookieConfig;

public class ApplicationSessionCookieConfig implements SessionCookieConfig {

    private boolean httpOnly = false;
    private boolean secure = false;
    private int maxAge = -1;
    private String comment;
    private String domain;
    private String name;
    private String path;

    public ApplicationSessionCookieConfig(String domain, String path) {
        this.domain = domain;
        this.path = path;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}

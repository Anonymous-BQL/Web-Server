package io.github.bianql.servletApi;

import io.github.bianql.servletHelper.ApplicationRequest;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public class Request implements HttpServletRequest {
    private ApplicationRequest request;

    public Request(ApplicationRequest request) {
        this.request = request;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return request.getCookies();
    }

    @Override
    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(request.getHeaders(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(request.getHeaderNames());
    }

    @Override
    public int getIntHeader(String s) {
        return Integer.parseInt(request.getHeader(s));
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPathInfo() {
        return request.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return request.getPathTranslated();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return request.getSessionId();
    }

    @Override
    public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    @Override
    public String getServletPath() {
        return request.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean allowGenerate) {
        return request.getSession(allowGenerate);
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public String changeSessionId() {
        throw new RuntimeException("不支持该操作！");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return getRequestedSessionId() != null && getSession(false) == null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return request.isSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return request.isSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        throw new RuntimeException("不支持authenticate操作");
    }

    @Override
    public void login(String s, String s1) throws ServletException {
        throw new RuntimeException("不支持login操作");
    }

    @Override
    public void logout() throws ServletException {
        throw new RuntimeException("不支持logout操作");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return request.upgrade(aClass);
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(request.getAttributeNames());
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        request.setCharacterEncoding(s);
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return request.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(request.getParameterNames());
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public ApplicationRequest getRequest() {
        return request;
    }

    @Override
    public String getProtocol() {
        return request.getProtocol();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getLocalPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(request.getLocales());
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String url) {
        return request.getRequestDispatcher(url);
    }

    @Override
    public String getRealPath(String url) {
        return request.getRealPath(url);
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return request.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new RuntimeException("不支持该操作");
    }

    @Override
    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }
}

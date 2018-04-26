package io.github.bianql.servletApi;

import io.github.bianql.servletHelper.ApplicationResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class Response implements HttpServletResponse {
    ApplicationResponse applicationResponse;

    public Response(ApplicationResponse applicationResponse) {
        this.applicationResponse = applicationResponse;
    }

    @Override
    public void addCookie(Cookie cookie) {
        applicationResponse.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String s) {
        return applicationResponse.containsHeader(s);
    }

    @Override
    public String encodeURL(String s) {
        return applicationResponse.encodeURL(s);
    }

    @Override
    public String encodeRedirectURL(String s) {
        return applicationResponse.encodeRedirectURL(s);
    }

    @Override
    public String encodeUrl(String s) {
        return encodeURL(s);
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return encodeRedirectURL(s);
    }

    @Override
    public void sendError(int i, String s) throws IOException {
        applicationResponse.sendError(i, s);
    }

    @Override
    public void sendError(int i) throws IOException {
        sendError(i, null);
    }

    @Override
    public void sendRedirect(String s) throws IOException {
        applicationResponse.sendRedirect(s);
    }

    @Override
    public void setDateHeader(String s, long l) {
        applicationResponse.setDateHeader(s, l);
    }

    @Override
    public void addDateHeader(String s, long l) {
        applicationResponse.addDateHeader(s, l);
    }

    @Override
    public void setHeader(String s, String s1) {
        applicationResponse.setHeader(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
        applicationResponse.addHeader(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {
        applicationResponse.setIntHeader(s, i);
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int status) {
        setStatus(status, null);
    }

    @Override
    public void setStatus(int status, String message) {
        applicationResponse.setStatus(status, message);
    }

    @Override
    public int getStatus() {
        return applicationResponse.getStatus();
    }

    @Override
    public String getHeader(String s) {
        return applicationResponse.getHeader(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return applicationResponse.getHeaders(s);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return applicationResponse.getHeaderNames();
    }

    @Override
    public String getCharacterEncoding() {
        return applicationResponse.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return applicationResponse.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return applicationResponse.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return applicationResponse.getWriter();
    }

    @Override
    public void setCharacterEncoding(String s) {
        applicationResponse.setCharacterEncoding(s);
    }

    @Override
    public void setContentLength(int i) {
        applicationResponse.setContentLength(i);
    }

    @Override
    public void setContentLengthLong(long l) {
        applicationResponse.setContentLengthLong(l);
    }

    @Override
    public void setContentType(String s) {
        applicationResponse.setContentType(s);
    }

    @Override
    public void setBufferSize(int i) {
        applicationResponse.setBufferSize(i);
    }

    @Override
    public int getBufferSize() {
        return applicationResponse.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        applicationResponse.flushBuffer();

    }

    @Override
    public void resetBuffer() {
        applicationResponse.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return applicationResponse.isCommitted();
    }

    @Override
    public void reset() {
        applicationResponse.reset();
    }

    @Override
    public void setLocale(Locale locale) {
        applicationResponse.setLocale(locale);
    }

    @Override
    public Locale getLocale() {
        return applicationResponse.getLocale();
    }
}

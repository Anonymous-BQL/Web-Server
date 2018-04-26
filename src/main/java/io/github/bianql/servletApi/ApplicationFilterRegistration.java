package io.github.bianql.servletApi;

import io.github.bianql.servletHelper.FilterRegistrationWrapper;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.*;

public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {
    private FilterRegistrationWrapper filterRegistrationWrapper;

    public ApplicationFilterRegistration(FilterRegistrationWrapper filterRegistrationWrapper) {
        this.filterRegistrationWrapper = filterRegistrationWrapper;
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        filterRegistrationWrapper.addMapping(dispatcherTypes, Arrays.asList(servletNames), null, isMatchAfter);
    }

    @Override
    public Collection<String> getServletNameMappings() {
        return filterRegistrationWrapper.getServletNameMappings();
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        filterRegistrationWrapper.addMapping(dispatcherTypes, null, Arrays.asList(urlPatterns), isMatchAfter);
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return filterRegistrationWrapper.getUrlPatternMappings();
    }

    @Override
    public void setAsyncSupported(boolean asyncSupported) {
        filterRegistrationWrapper.setAsyncSupported(asyncSupported);
    }

    @Override
    public String getName() {
        return filterRegistrationWrapper.getFilterName();
    }

    @Override
    public String getClassName() {
        return filterRegistrationWrapper.getClassName();
    }

    @Override
    public boolean setInitParameter(String s, String s1) {
        return filterRegistrationWrapper.setInitParameter(s,s1);
    }

    @Override
    public String getInitParameter(String s) {
        return filterRegistrationWrapper.getInitParameter(s);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> map) {
        return filterRegistrationWrapper.setInitParameters(map);
    }

    @Override
    public Map<String, String> getInitParameters() {
        return filterRegistrationWrapper.getInitParameters();
    }
}

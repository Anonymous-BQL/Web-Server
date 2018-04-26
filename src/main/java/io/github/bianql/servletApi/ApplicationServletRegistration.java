package io.github.bianql.servletApi;

import io.github.bianql.servletHelper.ServletRegistrationWrapper;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ApplicationServletRegistration implements ServletRegistration.Dynamic {
    private ServletRegistrationWrapper servletRegistrationWrapper;

    public ApplicationServletRegistration(ServletRegistrationWrapper servletRegistrationWrapper) {
        this.servletRegistrationWrapper = servletRegistrationWrapper;
    }

    @Override
    public void setLoadOnStartup(int i) {
        servletRegistrationWrapper.setLoadOnStartup(i);
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement servletSecurityElement) {
        return servletRegistrationWrapper.setServletSecurity(servletSecurityElement);
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfigElement) {
        servletRegistrationWrapper.setMultipartConfig(multipartConfigElement);
    }

    @Override
    public void setRunAsRole(String s) {
        servletRegistrationWrapper.setRunAsRole(s);
    }

    @Override
    public void setAsyncSupported(boolean b) {
        servletRegistrationWrapper.setAsyncSupported(b);
    }

    @Override
    public Set<String> addMapping(String... strings) {
        return servletRegistrationWrapper.addServletMapping(strings);
    }

    @Override
    public Collection<String> getMappings() {
        return servletRegistrationWrapper.getServletMapping();
    }

    @Override
    public String getRunAsRole() {
        return servletRegistrationWrapper.getRunAsRole();
    }

    @Override
    public String getName() {
        return servletRegistrationWrapper.getServletName();
    }

    @Override
    public String getClassName() {
        return servletRegistrationWrapper.getClassName();
    }

    @Override
    public boolean setInitParameter(String s, String s1) {
        return servletRegistrationWrapper.setInitParameter(s, s1);
    }

    @Override
    public String getInitParameter(String s) {
        return servletRegistrationWrapper.getInitParameter(s);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> map) {
        return servletRegistrationWrapper.setInitParameters(map);
    }

    @Override
    public Map<String, String> getInitParameters() {
        return servletRegistrationWrapper.getInitParameters();
    }
}

package io.github.bianql.servletHelper;

import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.servletApi.ApplicationFilterConfig;
import org.springframework.util.CollectionUtils;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.*;

public class FilterRegistrationWrapper {
    private String filterName;
    private String className;
    private Set<FilterMap> mapping = new HashSet<>();
    private Map<String, String> initParameters = new HashMap<>();
    private boolean asyncSupported;
    private ApplicationContext applicationContext;
    private Class filterClass;
    private Filter filter;
    public static class FilterMap{
        private EnumSet<DispatcherType> dispatcherTypes;
        private List<String> servletNames;
        private List<String> urlPatterns;
        private boolean isMatchAfter;

        public FilterMap(EnumSet<DispatcherType> dispatcherTypes, List<String> servletNames, List<String> urlPatterns, boolean isMatchAfter) {
            this.dispatcherTypes = dispatcherTypes;
            this.servletNames = servletNames;
            this.urlPatterns = urlPatterns;
            this.isMatchAfter = isMatchAfter;
        }

        public EnumSet<DispatcherType> getDispatcherTypes() {
            return dispatcherTypes;
        }

        public void setDispatcherTypes(EnumSet<DispatcherType> dispatcherTypes) {
            this.dispatcherTypes = dispatcherTypes;
        }

        public List<String> getServletNames() {
            if(CollectionUtils.isEmpty(servletNames)){
                return Collections.emptyList();
            }
            return servletNames;
        }

        public void setServletNames(List<String> servletNames) {
            this.servletNames = servletNames;
        }

        public List<String> getUrlPatterns() {
            if(CollectionUtils.isEmpty(urlPatterns))
                return Collections.emptyList();
            return urlPatterns;
        }

        public void setUrlPatterns(List<String> urlPatterns) {
            this.urlPatterns = urlPatterns;
        }

        public boolean isMatchAfter() {
            return isMatchAfter;
        }

        public void setMatchAfter(boolean matchAfter) {
            isMatchAfter = matchAfter;
        }
    }

    public Set<FilterMap> getMapping() {
        return mapping;
    }

    public FilterRegistrationWrapper(ApplicationContext applicationContext, String filterName, String className, Class filterClass, Filter filter) {
        this.filterName = filterName;
        this.className = className;
        this.applicationContext = applicationContext;
        this.filterClass = filterClass;
        this.filter = filter;
    }

    public FilterConfig getFilterConfig() {
        return new ApplicationFilterConfig(filterName, applicationContext.getServletContext(), initParameters);
    }
    public void addMapping(EnumSet<DispatcherType> dispatcherTypes,List<String> servletNames,List<String> urlPatterns,boolean isMatchAfter){
        mapping.add(new FilterMap(dispatcherTypes,servletNames,urlPatterns,isMatchAfter));
    }
    public Collection<String> getServletNameMappings() {
        Collection<String> servletNames = new HashSet<>();
        mapping.forEach(filterMap -> {
            if(!CollectionUtils.isEmpty(filterMap.getServletNames())){
                servletNames.addAll(filterMap.getServletNames());
            }
        });
        return servletNames;
    }
    public Collection<String> getUrlPatternMappings() {
        Collection<String> urlPatterns = new HashSet<>();
        mapping.forEach(filterMap -> {
            if(!CollectionUtils.isEmpty(filterMap.getUrlPatterns())){
                urlPatterns.addAll(filterMap.getUrlPatterns());
            }
        });
        return urlPatterns;
    }

    public String getFilterName() {
        return filterName;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }
    public String getInitParameter(String key) {
        return initParameters.get(key);
    }

    public Set<String> setInitParameters(Map<String, String> map) {
        Set<String> conflicts = new HashSet<>();

        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException("初始化参数不能包含null对象！");
            }
            if (getInitParameter(entry.getKey()) != null) {
                conflicts.add(entry.getKey());
            }
        }
        if (conflicts.isEmpty()) {
            for (Map.Entry<String, String> entry : initParameters.entrySet()) {
                setInitParameter(entry.getKey(), entry.getValue());
            }
        }

        return conflicts;
    }

    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("初始化参数不能包含null对象");
        }
        if (getInitParameter(name) != null) {
            return false;
        }
        initParameters.put(name, value);
        return true;
    }
    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Class getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class filterClass) {
        this.filterClass = filterClass;
    }

    public Filter getFilter() {
        if(filter!=null)
        return filter;
        if(filterClass!=null){
            try {
                return applicationContext.getServletContext().createFilter(filterClass);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        if(className!=null){
            try {
                return applicationContext.getServletContext().createFilter((Class<Filter>) applicationContext.getClassLoader().loadClass(className));
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}

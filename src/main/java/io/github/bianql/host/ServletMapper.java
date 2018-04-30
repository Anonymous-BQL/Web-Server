package io.github.bianql.host;

import io.github.bianql.servletApi.ApplicationFilterChain;
import io.github.bianql.servletHelper.FilterRegistrationWrapper;
import io.github.bianql.servletHelper.ServletRegistrationWrapper;
import org.springframework.util.StringUtils;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.util.*;
import java.util.stream.Collectors;

public class ServletMapper {
    private Collection<FilterRegistrationWrapper> filters;
    private Collection<ServletRegistrationWrapper> servlets;
    private Servlet defaultServlet;

    public ServletMapper(Collection<FilterRegistrationWrapper> filters, Collection<ServletRegistrationWrapper> servlets, Servlet defaultServlet) {
        this.filters = filters;
        this.servlets = servlets;
        this.defaultServlet = defaultServlet;
    }

    public void initOnStartup() {
        servlets.forEach(ServletRegistrationWrapper::getServlet);
        servlets.stream()
                .filter(servletRegistrationWrapper -> servletRegistrationWrapper.getLoadOnStartup() > 0)
                .sorted(Comparator.comparing(ServletRegistrationWrapper::getLoadOnStartup))
                .forEach(servlet -> {
                    try {
                        servlet.changeFirstInvoke();
                        servlet.getServlet().init(servlet.getServletConfig());
                    } catch (ServletException e) {
                        e.printStackTrace();
                    }
                });
    }

    public FilterChain getFilterChainByUrl(String url, DispatcherType dispatcherType) {
        ApplicationFilterChain filterChain = new ApplicationFilterChain(findServletByUrl(url));
        filters.forEach(filter -> {
            if (filter.getMapping().stream().anyMatch((FilterRegistrationWrapper.FilterMap filterMap) ->
                    filterMap.getDispatcherTypes().contains(dispatcherType) && filterMap.getUrlPatterns().stream().anyMatch(urlPattern -> {
                        if (urlPattern.indexOf("*") > 0) {
                            urlPattern.replaceAll("\\*", ".*");
                        }
                        if (urlPattern.endsWith("/"))
                            urlPattern += ".*";
                        return url.matches(urlPattern);
                    })))
                filterChain.addFilter(filter.getFilter());
        });
        return filterChain;
    }

    public FilterChain getFilterChainByServletName(String servletName, DispatcherType dispatcherType) {
        ApplicationFilterChain filterChain = new ApplicationFilterChain(findServletByName(servletName));
        filters.forEach(filter -> {
            if (filter.getMapping().stream().anyMatch((FilterRegistrationWrapper.FilterMap filterMap) ->
                    filterMap.getDispatcherTypes().contains(dispatcherType) && filterMap.getServletNames().stream().anyMatch(name -> name.equals(servletName))))
                filterChain.addFilter(filter.getFilter());
        });
        return filterChain;
    }

    public Servlet findServletByUrl(String url) {
        Map<String, ServletRegistrationWrapper> alternativeServlets = new HashMap<>();
        if (StringUtils.isEmpty(url))
            url = "/";
        String finalUrl = url;
        servlets.forEach(servlet -> {
            for (String urlPattern : servlet.getServletMapping()) {
                if (urlPattern.indexOf("*") > 0) {
                    urlPattern.replaceAll("\\*", ".*");
                }
                if (urlPattern.endsWith("/"))
                    urlPattern += ".*";
                if (finalUrl.matches(urlPattern)) {
                    alternativeServlets.put(urlPattern, servlet);
                }
            }
        });
        final String[] bestUrlPattern = {null};
        alternativeServlets.keySet().forEach(urlPattern -> {
            if (bestUrlPattern[0] == null) {
                bestUrlPattern[0] = urlPattern;
            } else {
                if (urlCompare(bestUrlPattern[0], urlPattern)) {
                    bestUrlPattern[0] = urlPattern;
                }
            }

        });
        ServletRegistrationWrapper bestServletResult = alternativeServlets.get(bestUrlPattern[0]);
        Servlet bestServlet = null;
        if (bestServletResult != null) {
            bestServlet = bestServletResult.getServlet();
            if (bestServletResult.isFirstInvoke() && bestServletResult.changeFirstInvoke()) {
                try {
                    bestServlet.init(bestServletResult.getServletConfig());
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            }
        }
        return bestServlet == null ? defaultServlet : bestServlet;
    }

    //return true if second is better than first.
    private boolean urlCompare(String first, String second) {
        int firstLength = first.split("/").length, secondLength = second.split("/").length;
        if (firstLength > secondLength) {
            return false;
        } else if (secondLength > firstLength) {
            return true;
        }
        if (first.contains("*") && !second.contains("*")) {
            return true;
        }
        return false;
    }

    public Servlet findServletByName(String servletName) {
        return servlets.stream().filter(servlet -> {
                    if (servlet.getServletName().equals(servletName)) {
                        if (servlet.isFirstInvoke() && servlet.changeFirstInvoke()) {
                            try {
                                servlet.getServlet().init(servlet.getServletConfig());
                            } catch (ServletException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                    return false;
                }
        ).findFirst().orElseThrow(() -> new IllegalArgumentException("找不到名称为" + servletName + "的Servlet")).getServlet();
    }

    public List<Servlet> getServlets() {
        return servlets.stream().map(servletRegistrationWrapper -> servletRegistrationWrapper.getServlet()).collect(Collectors.toList());
    }

    public List<String> getServletNames() {
        return servlets.stream().map(servletRegistrationWrapper -> servletRegistrationWrapper.getServletName()).collect(Collectors.toList());
    }

    public Servlet getDefaultServlet() {
        return defaultServlet;
    }
}

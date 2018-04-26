package io.github.bianql.load;

import io.github.bianql.host.context.ApplicationContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.io.File;
import java.util.*;

public class WebXmlLoader {
    public static void loadWebXml(ApplicationContext context, String filePath) {
        ServletContext servletContext = context.getServletContext();
        try {
            File webXml = new File(filePath);
            if (!webXml.exists()) {
                context.getAppLogger().info("web.xml不存在，文件路径：" + webXml.getAbsolutePath());
                return;
            }
            SAXReader reader = new SAXReader();
            context.getAppLogger().info("开始加载web.xml...");
            Document doc = reader.read(webXml);
            Element root = doc.getRootElement();
            if (!root.getName().equals("web-app")) {
                context.getAppLogger().error("错误的web.xml文件", new RuntimeException("非法文件"));
            }
            loadContextParam(servletContext, root);
            loadListener(servletContext, root);
            loadServlet(servletContext, root);
            loadFilter(servletContext, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.getAppLogger().info("加载web.xml完成.");
    }

    private static void loadContextParam(ServletContext servletContext, Element element) {
        Element contextParam;
        for (Iterator i = element.elementIterator("context-param"); i.hasNext(); ) {
            contextParam = (Element) i.next();
            servletContext.setInitParameter(contextParam.elementText("param-name"), contextParam.elementText("param-value"));
        }
    }

    private static void loadListener(ServletContext servletContext, Element element) {
        Element listener;
        for (Iterator i = element.elementIterator("listener"); i.hasNext(); ) {
            listener = (Element) i.next();
            servletContext.addListener(listener.elementText("listener-class"));
        }
    }

    private static void loadServlet(ServletContext servletContext, Element element) {
        Map<String, ServletRegistration.Dynamic> servlets = new HashMap<>();
        Element servlet;
        Element servletMapping;
        Element initParam;
        Element urlPattern;
        ArrayList<String> urlPatterns;
        Set<String> conflict;
        for (Iterator i = element.elementIterator("servlet"); i.hasNext(); ) {
            servlet = (Element) i.next();
            String servletName = servlet.elementText("servlet-name");
            ServletRegistration.Dynamic servletRegistration = servletContext.addServlet(servletName, servlet.elementText("servlet-class"));
            servlets.put(servletName, servletRegistration);
            for (Iterator j = servlet.elementIterator("init-param"); j.hasNext(); ) {
                initParam = (Element) j.next();
                servletRegistration.setInitParameter(initParam.elementText("param-name"), initParam.elementText("param-value"));
            }
            String loadOnStartup = servlet.elementText("load-on-startup");
            if (!StringUtils.isEmpty(loadOnStartup)) {
                servletRegistration.setLoadOnStartup(Integer.valueOf(loadOnStartup));
            }
        }
        for (Iterator i = element.elementIterator("servlet-mapping"); i.hasNext(); ) {
            servletMapping = (Element) i.next();
            urlPatterns = new ArrayList<>();
            ServletRegistration.Dynamic servletRegistration = servlets.get(servletMapping.elementText("servlet-name"));
            for (Iterator j = servletMapping.elementIterator("url-pattern"); j.hasNext(); ) {
                urlPattern = (Element) j.next();
                urlPatterns.add(urlPattern.getText());
            }
            conflict = servletRegistration.addMapping(urlPatterns.toArray(new String[urlPatterns.size()]));
            //忽略冲突
        }
    }

    private static void loadFilter(ServletContext servletContext, Element element) {
        Map<String, FilterRegistration.Dynamic> filters = new HashMap<>();
        Element filter;
        Element filterMapping;
        Element initParam;
        Element urlPattern;
        Element servletName;
        ArrayList<String> urlPatterns;
        ArrayList<String> servletNames;
        EnumSet<DispatcherType> dispatcherTypes;
        String dispatcherType;
        for (Iterator i = element.elementIterator("filter"); i.hasNext(); ) {
            filter = (Element) i.next();
            String filterName = filter.elementText("filter-name");
            FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(filterName, filter.elementText("filter-class"));
            filters.put(filterName, filterRegistration);
            for (Iterator j = filter.elementIterator("init-param"); j.hasNext(); ) {
                initParam = (Element) j.next();
                filterRegistration.setInitParameter(initParam.elementText("param-name"), initParam.elementText("param-value"));
            }
        }
        for (Iterator i = element.elementIterator("filter-mapping"); i.hasNext(); ) {
            filterMapping = (Element) i.next();
            FilterRegistration.Dynamic filterRegistration = filters.get(filterMapping.elementText("filter-name"));
            urlPatterns = new ArrayList<>();
            for (Iterator j = filterMapping.elementIterator("url-pattern"); j.hasNext(); ) {
                urlPattern = (Element) j.next();
                urlPatterns.add(urlPattern.getText());
            }
            servletNames = new ArrayList<>();
            for (Iterator j = filterMapping.elementIterator("servlet-name"); j.hasNext(); ) {
                servletName = (Element) j.next();
                servletNames.add(servletName.getText());
            }
            dispatcherTypes = EnumSet.noneOf(DispatcherType.class);
            for (Iterator j = filterMapping.elementIterator("dispatcher"); j.hasNext(); ) {
                dispatcherType = ((Element) j.next()).getText();
                dispatcherTypes.add(getDispatcherType(dispatcherType));
            }
            filterRegistration.addMappingForServletNames(dispatcherTypes, false, servletNames.toArray(new String[servletNames.size()]));
            filterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urlPatterns.toArray(new String[urlPatterns.size()]));
        }
    }

    private static DispatcherType getDispatcherType(String dispatcher) {
        if (dispatcher.equalsIgnoreCase("FORWARD")) {
            return DispatcherType.FORWARD;
        } else if (dispatcher.equalsIgnoreCase("REQUEST")) {
            return DispatcherType.REQUEST;
        } else if (dispatcher.equalsIgnoreCase("INCLUDE")) {
            return DispatcherType.INCLUDE;
        } else if (dispatcher.equalsIgnoreCase("ERROR")) {
            return DispatcherType.ERROR;
        }
        throw new IllegalArgumentException("无法识别的dispatch type");
    }
}

package io.github.bianql.connector.Mapper;

import io.github.bianql.host.context.ApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContextMapper {
    private static Map<String, ApplicationContext> contextMap = Collections.synchronizedMap(new HashMap<String, ApplicationContext>());

    public static ApplicationContext getContextByUrl(String url) {
        String contextUrl = url;
        if (!contextUrl.startsWith("/")) {
            if (contextUrl.indexOf("/") > 0)
                contextUrl = "/" + url.split("/")[0];
        } else {
            if (contextUrl.lastIndexOf("/") > 0)
                contextUrl = "/" + contextUrl.split("/")[1];
        }
        return contextMap.get(contextUrl);
    }

    public static void addContextMapping(String url, ApplicationContext context) {
        contextMap.put(url, context);
    }

    public static Collection<ApplicationContext> getContexts() {
        return contextMap.values();
    }

    public static void clearMapping() {
        contextMap.clear();
    }
}

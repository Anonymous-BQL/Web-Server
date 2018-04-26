package io.github.bianql.host.processor;

import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.load.WebAppClassLoader;
import io.github.bianql.load.WebXmlLoader;

public class ContextProcessor implements  Runnable {
    private String appDir;
    private ApplicationContext applicationContext;

    public ContextProcessor(String appDir, ApplicationContext applicationContext) {
        this.appDir = appDir;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        //设置classloader
        ClassLoader classLoader = new WebAppClassLoader(appDir);
        Thread.currentThread().setContextClassLoader(classLoader);
        applicationContext.setClassLoader(classLoader);
        //解析web.xml
        WebXmlLoader.loadWebXml(applicationContext,appDir+"/WEB-INF/web.xml");
        //解析ServletContainerInitializer
        //解析web3.0注解
        //初始化ServletMapper
        applicationContext.initServletMapper();
        //初始化onStartup servlet.
        applicationContext.getServletMapper().initOnStartup();
    }
}

package io.github.bianql.server;

import io.github.bianql.config.MimeConfig;
import io.github.bianql.config.WebServerConfig;
import io.github.bianql.connector.ConnectionAcceptor;
import io.github.bianql.connector.Mapper.ContextMapper;
import io.github.bianql.executor.ExecutorFactory;
import io.github.bianql.host.context.ApplicationContext;
import io.github.bianql.host.processor.ContextProcessor;
import io.github.bianql.log.ServerLogger;
import io.github.bianql.servletHelper.ServletContainerEvent;
import io.github.bianql.servletHelper.ServletEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class WebServerManager {
    private ServerLogger logger;
    private ConnectionAcceptor connector;
    private WebServerConfig webServerConfig;

    @Autowired
    public WebServerManager(ServerLogger logger, WebServerConfig webServerConfig, ConnectionAcceptor connector) {
        this.logger = logger;
        this.webServerConfig = webServerConfig;
        this.connector = connector;
    }

    public void init() {
        logger.info("web服务器初始化开始");
        //设置线程池大小
        ExecutorFactory.setWebThreadSize(webServerConfig.getExecutorPoolSize());
        //初始化mime映射
        MimeConfig.initMimeMapping();
        //加载webApp上下文
        loadContexts();
        //初始化接收端口
        connector.setPort(webServerConfig.getWebServerPort());
        logger.info("web服务器初始化完成");
    }

    public void start() {
        logger.info("web服务器开始启动");
        //发布ServletContext created事件
        //运行SessionManager
        ContextMapper.getContexts().forEach(context -> {
            context.publishEvent(new ServletContainerEvent(ServletEventType.SERVLET_CREATE, new ServletContextEvent(context.getServletContext())));
            context.getSessionManager().run();
        });
        //启动connector
        ExecutorFactory.getServerExecutorPool().submit(() -> {
            try {
                connector.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logger.info("web服务器启动完成，等待请求...");

    }

    public void shutdown() {
        logger.info("web服务器关闭中");
        //关闭connector
        connector.stop();
        //调用Servlet destroy方法
        //发布ServletContext destroy事件
        ContextMapper.getContexts().forEach(context -> {
            context.getServletMapper().getServlets().forEach(Servlet::destroy);
            context.publishEvent(new ServletContainerEvent(ServletEventType.SERVLET_DESTORY, new ServletContextEvent(context.getServletContext())));
        });
        //清理ContextMapper
        ContextMapper.clearMapping();
        //关闭线程池
        ExecutorFactory.shutdown();
        logger.info("web服务器关闭完成");
    }

    private void loadContexts() {
        File webApps = new File(Constants.WEBAPP_DIR);
        ArrayList<Future> contextFuture = new ArrayList<>();
        if (!webApps.exists() || !webApps.isDirectory()) {
            return;
        }
        Arrays.asList(webApps.listFiles()).forEach(file -> {
            if (file.isDirectory()) {
                String appDir = file.getAbsolutePath();
                String url = "/" + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(File.separator) + 1);
                ApplicationContext context = new ApplicationContext(url);
                ContextMapper.addContextMapping(url, context);
                contextFuture.add(ExecutorFactory.getServerExecutorPool().submit(new ContextProcessor(appDir, context)));
            }
        });
        contextFuture.forEach(future -> {
            try {
                if (!future.isDone())
                    future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}

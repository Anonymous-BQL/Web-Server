package io.github.bianql.server;

import io.github.bianql.Server;
import io.github.bianql.config.ServerConfig;
import io.github.bianql.log.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.github.bianql.executor.ExecutorFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ServerControllerImpl implements ServerController {
    private ServerLogger logger;
    private ServerConfig serverConfig;
    private WebServerManager webServer;
    private AtomicBoolean isRuning = new AtomicBoolean(false);

    @Autowired
    public ServerControllerImpl(ServerLogger logger, ServerConfig serverConfig, WebServerManager webServer) {
        this.logger = logger;
        this.serverConfig = serverConfig;
        this.webServer = webServer;
    }

    @Override
    public void init(Server server) {
        server.setServerConfig(serverConfig);
        logger.setLoggerFile(serverConfig.getLoggerFile());
        ExecutorFactory.setServerThreadSize(serverConfig.getExecutorPoolSize());
    }

    @Override
    public void processCommand(String command) {
        switch (command) {
            case "shutdown":
                shutdown();
                break;
            case "start":
                start();
                break;
            case "restart":
                restart();
                break;
            default:
                logger.error("无法处理的指令" + command);
        }
    }

    @Override
    public void shutdown() {
        if (!isRuning.get()) {
            logger.error("web服务器未启动，拒绝关闭操作！");
            return;
        }
        logger.info("准备关闭web服务器");
        webServer.shutdown();
        isRuning.compareAndSet(true, false);
    }

    @Override
    public void start() {
        if (isRuning.get()) {
            logger.error("web服务器已经启动，拒绝本次操作！");
            return;
        }
        logger.info("准备启动web服务器");
        isRuning.compareAndSet(false, true);
        webServer.init();
        webServer.start();
    }

    public void restart() {
        logger.info("准备重启web服务器");
    }
}

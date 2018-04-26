package io.github.bianql.server;

import io.github.bianql.Server;
import io.github.bianql.config.ServerConfig;
import io.github.bianql.log.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.github.bianql.executor.ExecutorFactory;

@Component
public class ServerControllerImpl implements ServerController {
    private ServerLogger logger;
    private ServerConfig serverConfig;
    private WebServerManager webServer;
    @Autowired
    public ServerControllerImpl(ServerLogger logger, ServerConfig serverConfig, WebServerManager webServer){
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
        logger.info("准备关闭web服务器");
        webServer.shutdown();
    }

    @Override
    public void start() {
        logger.info("准备启动web服务器");
        webServer.init();
        webServer.start();
    }

    public void restart() {
        logger.info("准备重启web服务器");
    }
}

package io.github.bianql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerConfig {
    @Value("${server.port}")
    private int serverPort = 9999;
    @Value("${logger.file}")
    private String loggerFile = "logger.log";
    @Value("${server.executor.size}")
    private int executorPoolSize = 50;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getLoggerFile() {
        return loggerFile;
    }

    public void setLoggerFile(String loggerFile) {
        this.loggerFile = loggerFile;
    }

    public int getExecutorPoolSize() {
        return executorPoolSize;
    }

    public void setExecutorPoolSize(int executorPoolSize) {
        this.executorPoolSize = executorPoolSize;
    }
}

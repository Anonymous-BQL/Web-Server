package io.github.bianql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebServerConfig {
    @Value("${web.port}")
    private int webServerPort = 8080;
    @Value("${web.executor.size}")
    private int executorPoolSize = 50;

    public int getWebServerPort() {
        return webServerPort;
    }

    public void setWebServerPort(int webServerPort) {
        this.webServerPort = webServerPort;
    }

    public int getExecutorPoolSize() {
        return executorPoolSize;
    }

    public void setExecutorPoolSize(int executorPoolSize) {
        this.executorPoolSize = executorPoolSize;
    }
}

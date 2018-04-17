package io.github.bianql.server;

public interface ServerController {
    public void processCommand(String command);

    public void shutdown();

    public void start();
}

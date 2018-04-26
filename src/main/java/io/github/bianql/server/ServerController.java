package io.github.bianql.server;

import io.github.bianql.Server;

public interface ServerController {
    public void init(Server server);

    public void processCommand(String command);

    public void shutdown();

    public void start();
}

package io.github.bianql.server;

import org.springframework.stereotype.Component;

@Component
public class ServerControllerImpl implements ServerController {
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
                System.out.println("无法处理的指令" + command);
        }
    }

    @Override
    public void shutdown() {
        System.out.println("正在");
    }

    @Override
    public void start() {

    }

    public void restart() {

    }
}

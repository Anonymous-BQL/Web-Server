package io.github.bianql;

import io.github.bianql.config.ServerConfig;
import io.github.bianql.server.ServerController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@ComponentScan("io.github.bianql")
@PropertySource("classpath:serverConfig.properties")
public class Server {
    private ServerConfig serverConfig;
    private ServerController serverController;

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    //等待外部指令
    private void prepare() {

        ServerSocket server = null;
        try {
            server = new ServerSocket(serverConfig.getServerPort());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("服务器端口号配置错误！");
        }
        while (true) {
            Socket socket = null;
            try {
                socket = server.accept();
                BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String command = read.readLine();
                while (command != null) {
                    serverController.processCommand(command);
                    command = read.readLine();
                }
            } catch (IOException e) {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e1) {
                    //丢弃关闭异常
                }
            }

        }
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Server.class);
        Server server = new Server();
        server.serverController = context.getBean(ServerController.class);
        server.serverController.init(server);
        server.prepare();
    }


}

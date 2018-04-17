package io.github.bianql;

import io.github.bianql.server.ServerController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@ComponentScan("io.github.bianql")
public class WebServer {
    public static void main(String[] args) {
        try {
            ApplicationContext context = new AnnotationConfigApplicationContext(WebServer.class);
            ServerSocket server = new ServerSocket(9999);
            ServerController controller = context.getBean(ServerController.class);
            while (true) {
                Socket socket = server.accept();
                BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String command = read.readLine();
                while (command != null) {
                    controller.processCommand(command);
                    command = read.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

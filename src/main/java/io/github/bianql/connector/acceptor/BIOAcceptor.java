package io.github.bianql.connector.acceptor;

import io.github.bianql.connector.ConnectionAcceptor;
import io.github.bianql.connector.processor.SocketProcessor;
import io.github.bianql.executor.ExecutorFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
@Component
public class BIOAcceptor implements ConnectionAcceptor {
    private volatile boolean stop = false;
    private int port;

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void accept() throws IOException {
        stop = false;
        ServerSocket server = new ServerSocket(port);
        while (!stop) {
            Socket socket = server.accept();
            ExecutorFactory.getWebExecutorPool().submit(new SocketProcessor(socket));
        }
    }

    @Override
    public void stop() {
        stop = true;
    }
}

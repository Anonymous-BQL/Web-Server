package io.github.bianql.connector;

import java.io.IOException;

public interface ConnectionAcceptor {
    public void accept() throws IOException;
    public void setPort(int port);
    public void stop();
}

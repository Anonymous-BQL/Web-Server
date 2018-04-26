package io.github.bianql.test;

import io.github.bianql.connector.Mapper.ContextMapper;
import io.github.bianql.servletHelper.DefaultServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class CServerTest {
    private static Socket client;
    private static PrintStream out;

    public static void main(String[] args) {
        try {
            client = new Socket("127.0.0.1", 9999);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void start() {
        try {
            if (out == null)
                out = new PrintStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("start");
    }

    private static void stop() {
        if (out == null) {
            try {
                out = new PrintStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        out.println("shutdown");
    }

    private static void testDefaultServlet() {
        ContextMapper.getContexts().forEach(context -> {
            Servlet servlet = context.getServletMapper().findServletByUrl("/");
            if (!DefaultServlet.class.isInstance(servlet)) {
                throw new RuntimeException("程序异常");
            }
            try {
                System.out.println(context.getContextPath());
                servlet.service(null, null);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

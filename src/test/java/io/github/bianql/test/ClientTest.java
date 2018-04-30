package io.github.bianql.test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) throws Exception {
        ServerSocket socket = new ServerSocket(10086);
        while (true) {
            Socket client = socket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = reader.readLine();
            if (line.equalsIgnoreCase("camera"))
                new Thread(() -> {
                    while (true) {
                        File file = new File("C:\\Users\\95232\\Desktop\\videoFrame.jpg");
                        try {
                            client.getOutputStream().write((file.length() + "len").getBytes());

                            FileInputStream inputStream = new FileInputStream(file);
                            byte[] buffer = new byte[128];
                            int count;
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            while ((count = inputStream.read(buffer)) > 0) {
                                out.write(buffer, 0, count);
                            }
                            client.getOutputStream().write(out.toByteArray());
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            else if (line.equalsIgnoreCase("begin")) {
                client.getOutputStream().write("begin,59,69,88,".getBytes());
            }
        }
    }
}

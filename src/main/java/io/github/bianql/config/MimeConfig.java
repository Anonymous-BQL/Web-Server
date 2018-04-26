package io.github.bianql.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MimeConfig {
    private static Properties mimeMapping = new Properties();
    public static String getMimeType(String mime){
        return (String) mimeMapping.get(mime);
    }
    public static void initMimeMapping(){
        InputStream in = MimeConfig.class.getClassLoader().getResourceAsStream("mime.properties");
        try {
            mimeMapping.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

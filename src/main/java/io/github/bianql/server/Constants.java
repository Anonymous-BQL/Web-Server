package io.github.bianql.server;

import java.io.File;
import java.io.IOException;

public class Constants {
    public static final String SERVER_ROOT_DIR = getServerPath();
    public static final String SERVER_CLASS_LIB = getServerPath()+"/lib";
    public static final String WEBAPP_DIR = getServerPath()+"/webapps";
    public static final int MAJOR_VERSION = 3;
    public static final int MINOR_VERSION = 1;

    private static String getServerPath() {
        String path = null;
        try {
            path = new File("").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}

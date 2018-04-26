package io.github.bianql.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Stream;

public class AppLogger {
    private String loggerFile;
    Logger logger;

    public AppLogger(String loggerFile) {
        logger = Logger.getLogger("AppLogger");
        this.loggerFile = loggerFile;
        initLogger();
    }

    private void initLogger() {
        FileHandler fileHandler = null;
        File file = new File(loggerFile);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            fileHandler = new FileHandler(loggerFile);
            fileHandler.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleFormatter sf = new SimpleFormatter();
        fileHandler.setFormatter(sf);
        logger.addHandler(fileHandler);
    }

    public void error(String message , Throwable e) {
        logger.log(Level.SEVERE, message);
        logger.log(Level.SEVERE, e.toString());
        Stream.of(e.getStackTrace()).forEach(stackTraceElement -> logger.log(Level.SEVERE,"\tat"+stackTraceElement.toString()));
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void warn(String message) {
        logger.log(Level.WARNING, message);
    }

}

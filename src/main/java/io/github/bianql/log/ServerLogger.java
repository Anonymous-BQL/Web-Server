package io.github.bianql.log;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Component
public class ServerLogger {
    private String loggerFile;
    Logger logger;

    public ServerLogger() {
        logger = Logger.getLogger("ServerLogger");
    }

    public void setLoggerFile(String loggerFile) {
        this.loggerFile = loggerFile;
        FileHandler fileHandler = null;
        File file = new File(loggerFile);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            fileHandler = new FileHandler(loggerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleFormatter sf = new SimpleFormatter();
        fileHandler.setFormatter(sf);
        logger.addHandler(fileHandler);
    }

    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void warn(String message) {
        logger.log(Level.WARNING, message);
    }

}

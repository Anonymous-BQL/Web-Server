package io.github.bianql.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorFactory {
    private static int serverThreadSize;
    private static int webThreadSize;
    private static volatile ExecutorService serverExecutors = null;
    private static Object serverLock = new Object();
    private static Object webLock = new Object();
    private static Object sessionLock = new Object();
    private static volatile ExecutorService webExecutors = null;
    private static volatile Thread sessionManagerThread = null;

    public static ExecutorService getServerExecutorPool() {
        if (serverExecutors == null) {
            synchronized (serverLock) {
                if (serverExecutors == null) {
                    serverExecutors = Executors.newFixedThreadPool(serverThreadSize, new ExecutorFactory.ServerThreadFactory());
                }
            }
        }
        return serverExecutors;
    }

    public static Thread getSessionManagerThread(Runnable runnable) {
        if (sessionManagerThread == null) {
            synchronized (sessionLock) {
                if (sessionManagerThread == null) {
                    sessionManagerThread = new Thread(runnable, "session-manager-thread");
                }
            }
        }
        return sessionManagerThread;
    }

    public static void setServerThreadSize(int serverThreadSize) {
        ExecutorFactory.serverThreadSize = serverThreadSize;
    }

    private static class ServerThreadFactory implements ThreadFactory {

        int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "server-thread-" + count++);
        }
    }

    public static ExecutorService getWebExecutorPool() {
        if (webExecutors == null) {
            synchronized (webLock) {
                if (webExecutors == null) {
                    webExecutors = Executors.newFixedThreadPool(webThreadSize, new ExecutorFactory.WebThreadFactory());
                }
            }
        }
        return webExecutors;
    }


    public static void setWebThreadSize(int webThreadSize) {
        ExecutorFactory.webThreadSize = webThreadSize;
    }

    private static class WebThreadFactory implements ThreadFactory {

        int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "web-thread-" + count++);
        }
    }

    public static void shutdown() {
        serverExecutors.shutdown();
        serverExecutors = null;
        webExecutors.shutdown();
        webExecutors = null;
        if (sessionManagerThread != null && sessionManagerThread.isAlive()) {
            sessionManagerThread.stop();
        }
    }
}

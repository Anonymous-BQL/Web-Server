package io.github.bianql.host.context;

import io.github.bianql.executor.ExecutorFactory;
import io.github.bianql.servletApi.Session;
import io.github.bianql.servletHelper.ServletContainerEvent;
import io.github.bianql.servletHelper.ServletEventType;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionManager {
    private ApplicationContext context;
    private Map<String, Session> sessionMap = Collections.synchronizedMap(new HashMap<>());
    private List<ExpiredSession> expiredSessions = new CopyOnWriteArrayList<>();
    private Thread managerThread;

    public SessionManager(ApplicationContext context) {
        this.context = context;
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public void run() {
        managerThread = ExecutorFactory.getSessionManagerThread(new SessionProcessor());
        managerThread.start();
    }

    public static String getSessionCookieName() {
        return "JSESSIONID";
    }

    public HttpSession saveSession() {
        String sessionId = generateSessionId();
        long now = new Date().getTime();
        Session session = new Session(sessionId, now, now, context);
        context.publishEvent(new ServletContainerEvent(ServletEventType.SESSION_CREATE, new HttpSessionEvent(session)));
        expiredSessions.add(new ExpiredSession(session));
        updateSession();
        sessionMap.put(sessionId, session);
        return session;
    }

    private void updateSession() {
        if (!managerThread.isAlive()) {
            managerThread.interrupt();
        }
    }

    public HttpSession getSession(String sessionId, boolean allowGenerate) {
        Session session = sessionMap.get(sessionId);
        if (session != null) {
            session.changeNewStatus();
            session.setLastAccessedTime(new Date().getTime());
            updateSession();
            return session;
        }
        if (allowGenerate) {
            return saveSession();
        }
        return null;
    }

    public void invalidateSession(String sessionId) {
        context.publishEvent(new ServletContainerEvent(ServletEventType.SESSION_DESTORY, new HttpSessionEvent(sessionMap.remove(sessionId))));
    }

    public static Cookie createSessionCookie(ApplicationContext applicationContext,
                                             String sessionId, boolean secure) {
        ServletContext context = applicationContext.getServletContext();
        SessionCookieConfig scc =
                context.getSessionCookieConfig();

        Cookie cookie = new Cookie(
                SessionManager.getSessionCookieName(), sessionId);
        HttpSession session = applicationContext.getSessionManager().getSession(sessionId, false);
        cookie.setMaxAge(0);
        if (session != null) {
            cookie.setMaxAge(session.getMaxInactiveInterval());
        }
        cookie.setComment(scc.getComment());
        if (scc.isSecure() || secure) {
            cookie.setSecure(true);
        }
        if (scc.isHttpOnly()) {
            cookie.setHttpOnly(true);
        }
        String contextPath = scc.getPath();
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = context.getContextPath();
        }
        if (!contextPath.endsWith("/")) {
            contextPath = contextPath + "/";
        }
        cookie.setPath(contextPath);
        return cookie;
    }

    private class SessionProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (CollectionUtils.isEmpty(expiredSessions)) {
                    try {
                        Thread.sleep(1000 * 60 * 60);
                    } catch (InterruptedException e) {
                    }
                } else {
                    Instant now = Instant.now();
                    final int[] sleepSecond = {Integer.MAX_VALUE};
                    expiredSessions.forEach(expiredSession -> {
                        if (expiredSession.getInstant().isBefore(now)) {
                            expiredSession.invalidate();
                            expiredSessions.remove(expiredSession);
                        } else {
                            int sleep = Long.valueOf(Duration.between(now, expiredSession.getInstant()).getSeconds()).intValue();
                            sleepSecond[0] = sleepSecond[0] > sleep ? sleep : sleepSecond[0];
                        }
                    });
                    try {
                        Thread.sleep(sleepSecond[0] * 1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private static class ExpiredSession {
        private Session session;

        public ExpiredSession(Session session) {
            this.session = session;
        }

        public Instant getInstant() {
            return Instant.ofEpochMilli(session.getLastAccessedTime()).plusSeconds(session.getMaxInactiveInterval());
        }

        public void invalidate() {
            session.invalidate();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExpiredSession that = (ExpiredSession) o;
            return Objects.equals(session.getId(), that.session.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(session.getId());
        }
    }
}

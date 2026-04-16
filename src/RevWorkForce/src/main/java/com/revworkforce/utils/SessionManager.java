package com.revworkforce.utils;

import com.revworkforce.model.User;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Date loginTime;
    private Timer sessionTimer;
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        this.loginTime = new Date();
        startSessionTimer();
    }

    public void logout() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }
        this.currentUser = null;
        this.loginTime = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    private void startSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }
        sessionTimer = new Timer();
        sessionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    long sessionDuration = new Date().getTime() - loginTime.getTime();
                    if (sessionDuration >= SESSION_TIMEOUT) {
                        logout();
                        System.out.println("\n️ Session expired due to inactivity. Please login again.");
                        System.exit(0);
                    }
                }
            }
        }, SESSION_TIMEOUT, 60000); // Check every minute
    }
}
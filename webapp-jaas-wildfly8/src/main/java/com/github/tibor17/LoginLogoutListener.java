package com.github.tibor17;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class LoginLogoutListener
        implements HttpSessionListener {
    private static final Logger LOG = LoggerFactory.getLogger(SecureService.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.info("LOGIN {}", se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.info("LOGOUT {}", se.getSession().getId());
    }
}

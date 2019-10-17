package com.github.tibor17;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.security.Principal;

@LocalBean
@Stateless
@RolesAllowed("ADMIN")
public class SecureService implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(SecureService.class);

    @Inject
    private Principal principal;

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private HttpSession httpSession;

    @Inject
    private ServletContext servletContext;

    public void signedIn() {
        LOG.info("[{}]: Received a request from {} ({}:{}).",
                httpServletRequest.getRemoteUser(),
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getRemoteHost(),
                httpServletRequest.getLocalPort());

        LOG.info("[{}]: Login page {}.",
                httpServletRequest.getRemoteUser(),
                httpServletRequest.getRequestURI());

        LOG.info("[{}]: You are logged in with principal name \"{}\" and {} SESSIONID \"{}\".",
                httpServletRequest.getRemoteUser(),
                principal.getName(),
                httpSession.isNew() ? "new" : "old",
                httpSession.getId());
    }

    public String signOut() {
        if (httpServletRequest.isUserInRole("ADMIN")) {
            LOG.info("[{}]: You are about to logout.", httpServletRequest.getRemoteUser());

            if (httpServletRequest.isRequestedSessionIdValid()) {
                // Invalidate current HTTP session.
                // Will call JAAS LoginModule logout() method
                httpSession.invalidate();
                // httpServletRequest.logout();
                LOG.info("[{}]: Session invalidated!", httpServletRequest.getRemoteUser());
            }
        }
        return "/index?faces-redirect=true";
    }
}

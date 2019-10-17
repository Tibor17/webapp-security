package com.github.tibor17;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("admin")
@RequestScoped
public class JsfController {
    @Inject
    private SecureService secureService;

    public void signedIn() {
        secureService.signedIn();
    }

    public String signOut() {
        return secureService.signOut();
    }
}

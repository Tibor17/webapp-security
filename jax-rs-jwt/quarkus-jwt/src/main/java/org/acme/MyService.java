package org.acme;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyService {
    public MyObj newObj() {
        MyObj pojo = new MyObj();
        pojo.setName("John Smith");
        return pojo;
    }
}

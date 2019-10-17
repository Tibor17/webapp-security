package com.github.tibor17;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;

@Named
@RequestScoped
public class Pokus
{
    @PersistenceContext(unitName = "user")
    private EntityManager em;

    public void button() {
        String query = "SELECT passwd FROM users WHERE username=?";
        Query q = em.createNativeQuery(query)
                .setParameter( 1, "myfear" );
        Object author = q.getSingleResult();
        System.out.println(author);
    }
}

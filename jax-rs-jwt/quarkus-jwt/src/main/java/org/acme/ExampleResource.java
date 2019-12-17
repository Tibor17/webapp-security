package org.acme;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonString;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.ok;

@Path("/resources")
@RequestScoped
public class ExampleResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleResource.class);

    @Inject
    MyService myService;

    @Inject
    JsonWebToken jwt;

    @Inject
    @Claim(standard = Claims.birthdate)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<JsonString> birthdate;

    @Inject
    @Claim(standard = Claims.kid)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<JsonString> kid;

    @POST
    @Path("admin")
    @RolesAllowed("admin")
    public void x() {
    }

    @GET
    @Path("hello")
    @Produces({TEXT_PLAIN, APPLICATION_JSON, APPLICATION_XML})
    @RolesAllowed("Subscriber")
    public Response hello() {
        LOGGER.info("subject '{}'", jwt.getSubject());
        LOGGER.info("audience set {}", jwt.getAudience());
        LOGGER.info("expiration {}", jwt.getExpirationTime());
        LOGGER.info("issuer {}", jwt.getIssuer());
        LOGGER.info("issued at {}", jwt.getIssuedAtTime());
        LOGGER.info("groups set {}", jwt.getGroups());
        LOGGER.warn("principal '{}'", jwt.getName());
        LOGGER.warn("token id '{}'", jwt.getTokenID());
        kid.ifPresent(kid -> LOGGER.warn("kid '{}'", kid));
        birthdate.ifPresent(bd -> LOGGER.warn("birthday exists {}", bd));

        String jsonb = JsonbBuilder.create().toJson(myService.newObj());
        return ok()
                .type(APPLICATION_JSON_TYPE)
                .entity(jsonb)
                .build();
    }
}
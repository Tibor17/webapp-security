import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.net.URI;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class ClaimsTest {
    @Test
    public void test() throws Exception {
        URI iss = new URI("https://www.identity-access-management.com/iam");
        URI sub = new URI("/idm/users/1234567");
        URI[] aud = {
                new URI("https://www.facebook.com/name.surname")
        };
        long iat = System.currentTimeMillis() / 1000;
        long nbf = iat + 1;
        long exp = iat + 2*60;
        UUID jwtId = UUID.randomUUID();
        Claims claims = new Claims(iss, sub, aud, iat, nbf, exp, jwtId);

        JsonbConfig config = new JsonbConfig()
                .withNullValues(true)
                .withFormatting(true);

        Jsonb jsonb = JsonbBuilder.create(config);
        String json = jsonb.toJson(claims);

        assertThatJson(json)
                .node("iss")
                .isEqualTo("https://www.identity-access-management.com/iam")
                .node("sub")
                .isEqualTo("/idm/users/1234567")
                .node("iat")
                .isEqualTo(iat)
                .node("nbf")
                .isEqualTo(1 + iat)
                .node("exp")
                .isEqualTo(120 + iat)
                .node("jti")
                .isEqualTo(jwtId)
                .node("aud")
                .isArray()
                .ofLength(1)
                .thatContains("https://www.facebook.com/name.surname");
    }
}

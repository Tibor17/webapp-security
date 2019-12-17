import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class JoseHeaderTest {

    @Test
    public void shouldBeCustomJoseHeader() {
        JsonbConfig config = new JsonbConfig()
                .withNullValues(true)
                .withFormatting(true);

        Jsonb jsonb = JsonbBuilder.create(config);
        String json = jsonb.toJson(new JoseHeader("JSON", "JSON", "HS256"));

        assertThatJson(json)
                .node("type")
                .isEqualTo("JSON")
                .node("cty")
                .isEqualTo("JSON")
                .node("alg")
                .isEqualTo("HS256");
    }
}

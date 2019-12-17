import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class RFC7519Test {
    private static Base64.Encoder encoder;

    @BeforeClass
    public static void setup() {
        encoder = Base64.getUrlEncoder().withoutPadding();
    }

    @Test
    public void shouldBeJoseHeaderRFC7519() {
        String joseHeader = "{\"typ\":\"JWT\",\r\n \"alg\":\"HS256\"}";
        String joseHeaderBase64UrlEncodedAscii = encoder.encodeToString(joseHeader.getBytes(UTF_8));
        assertThat(joseHeaderBase64UrlEncodedAscii)
                .isEqualTo("eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9");
    }

    @Test
    public void shouldBeClaimsRFC7519() {
        String claims = "{\"iss\":\"joe\",\r\n \"exp\":1300819380,\r\n \"http://example.com/is_root\":true}";
        String claimsBase64UrlEncodedAscii = encoder.encodeToString(claims.getBytes(UTF_8));
        assertThat(claimsBase64UrlEncodedAscii)
          .isEqualTo("eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ");
    }

    @Test
    public void shouldBeHmacSignature() throws Exception {
        String jwtHash = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9"
                + ".eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ";
        byte[] src = jwtHash.getBytes(UTF_8);
        Mac hMac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec("your-256-bit-secret".getBytes(UTF_8), "HmacSHA256");
        hMac.init(keySpec);
        byte[] digest = hMac.doFinal(src);
        assertThat(encoder.encodeToString(digest))
                .isEqualTo("GKDsyPCodxYIait_SnHYpt3iGC0IQHg9WoRVj4te1nM");
    }

    @Test
    public void compatibleSignatureWithJwtIo() throws Exception {
        String json = "{\"type\":\"JWT\",\"alg\":\"HS256\"}";
        String joseHeaderBase64UrlEncodedAscii = encoder.encodeToString(json.getBytes(UTF_8));

        String claims = "{\"iss\":\"joe\",\"exp\":1300819380,\"http://example.com/is_root\":true}";
        String claimsBase64UrlEncodedAscii = encoder.encodeToString(claims.getBytes(UTF_8));

        String jwt = joseHeaderBase64UrlEncodedAscii + "." + claimsBase64UrlEncodedAscii;

        Mac hMac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec("your-256-bit-secret".getBytes(UTF_8), "HmacSHA256");
        hMac.init(keySpec);
        byte[] digest = hMac.doFinal(jwt.getBytes(US_ASCII));
        String digitalSignatureMaced = encoder.encodeToString(digest);

        assertThat(digitalSignatureMaced)
                .isEqualTo("L1Ajx9DMe_F8zAMxU1RlBbAqs0Yg0pBGecYNQL5d-IU");
    }
}

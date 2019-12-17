package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.util.*;

import static io.restassured.RestAssured.given;
import static java.lang.Integer.toHexString;
import static java.lang.Math.min;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.*;
import static java.util.Calendar.JULY;
import static java.util.Collections.addAll;
import static org.hamcrest.CoreMatchers.is;
import static org.jose4j.jws.AlgorithmIdentifiers.RSA_USING_SHA256;

@QuarkusTest
public class MyTest {
    private static String BASEDIR = System.getProperty("user.dir");
    private static InputStream KEYSTORE_STREAM;
    private static Base64.Encoder BASE64 = Base64.getEncoder();
    private static PrivateKey PRIVATE_KEY;
    private static PublicKey PUBLIC_KEY;

    @BeforeAll
    public static void streamKeystore() throws Exception {
        Path pathToKeystore = Paths.get(BASEDIR, "target", "keystore.jks");
        KEYSTORE_STREAM = new FileInputStream(pathToKeystore.toFile());

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(KEYSTORE_STREAM, "test pswd".toCharArray());
        PRIVATE_KEY = (PrivateKey) keystore.getKey("quarkus", "test pswd".toCharArray());
        Certificate certificate = keystore.getCertificate("quarkus");
        PUBLIC_KEY = certificate.getPublicKey();

        Path publicKeyTxt = Paths.get(BASEDIR, "target", "test-quarkus.pk");
        printPublicKey(publicKeyTxt, PUBLIC_KEY);
    }

    @AfterAll
    public static void closeKeystore() throws IOException {
        KEYSTORE_STREAM.close();
    }

    @Test
    public void test() throws Exception {
        JsonWebSignature jws = generateJwtFromAuthorizationServer();

        System.out.println(jws.toString());

        System.out.println(jws.getCompactSerialization());
        // {"alg":"RS256","typ":"JWT","cty":"JWT","kid":"/privateKey.pem"}
        // {"iss":"https://quarkus.io/using-jwt-rbac","jti":"a-123","sub":"jdoe-using-jwt-rbac","upn":"jdoe@quarkus.io","preferred_username":"jdoe","aud":"using-jwt-rbac","birthday":994975200,"roleMappings":{"group1":"Group1MappedRole","group2":"Group2MappedRole"},"groups":["Echoer","Tester","Subscriber","group2"],"iat":1578372572,"exp":1578372632}

        given()
                .auth()
                .oauth2(jws.getCompactSerialization())
                .when()
                .get("/resources/hello")
                .then()
                .statusCode(HTTP_OK)
                .body(is("{\"name\":\"John Smith\"}"));
    }

    private static JsonWebSignature generateJwtFromAuthorizationServer() throws NoSuchAlgorithmException {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setKey(PRIVATE_KEY);
        jws.setAlgorithmHeaderValue(RSA_USING_SHA256);
        jws.setHeader("typ", "JWT");
        //jws.setHeader("cty", "JWT");
        jws.setKeyIdHeaderValue(kid(PUBLIC_KEY));

        JwtClaims claims = new JwtClaims();
        claims.setIssuer("https://quarkus.io/using-jwt-rbac");
        claims.setJwtId("a-123");
        claims.setSubject("jdoe-using-jwt-rbac");
        claims.setClaim("upn", "jdoe@quarkus.io"); // user-principal-name
        claims.setClaim("preferred_username", "jdoe");
        claims.setAudience("using-jwt-rbac");
        Date birthday = new Date(2001 - 1900, JULY, 13 ); // 2001-07-13
        claims.setNumericDateClaim("birthday", NumericDate.fromMilliseconds(birthday.getTime()));
        Map<String, Object> roleMappings = new LinkedHashMap<>();
        roleMappings.put("group1", "Group1MappedRole");
        roleMappings.put("group2", "Group2MappedRole");
        claims.setClaim("roleMappings", roleMappings);
        Collection<String> groups = new ArrayList<>();
        addAll(groups, "Echoer", "Tester", "Subscriber", "group2");
        claims.setClaim("groups", groups);

        long currentTimeInSecs = System.currentTimeMillis() / 1_000L;
        claims.setIssuedAt(NumericDate.fromSeconds(currentTimeInSecs));
        //claims.setClaim(Claims.auth_time.name(), NumericDate.fromSeconds(currentTimeInSecs).toString());
        long exp = currentTimeInSecs + 60L;
        claims.setExpirationTime(NumericDate.fromSeconds(exp));

        jws.setPayload(claims.toJson());

        System.out.println(claims.toJson());
        return jws;
    }

    private static String kid(PublicKey publicKey) throws NoSuchAlgorithmException {
        StringJoiner joiner = new StringJoiner(":");
        for (byte b : MessageDigest.getInstance("SHA-256").digest(publicKey.getEncoded())) {
            joiner.add(toHexString(b >= 0 ? b : 256 + b).toUpperCase(Locale.ROOT));
        }
        return joiner.toString();
    }

    private static void printPublicKey(Path publicKeyTxt, PublicKey publicKey) throws IOException {
        byte[] publicKeyBinary = publicKey.getEncoded();
        write(publicKeyTxt, "-----BEGIN PUBLIC KEY-----".getBytes(US_ASCII), CREATE, WRITE, TRUNCATE_EXISTING);
        write(publicKeyTxt, new byte[] {'\n'}, WRITE, APPEND);
        String base64 = BASE64.encodeToString(publicKeyBinary);
        for (int i = 0, length = base64.length(), tokens = length % 64 == 0 ? length >>> 6 : 1 + (length >>> 6);
             i < tokens; i++) {
            String token = base64.substring(64 * i, min(64 * (1 + i), length));
            write(publicKeyTxt, token.getBytes(US_ASCII), WRITE, APPEND);
            write(publicKeyTxt, new byte[] {'\n'}, WRITE, APPEND);
        }
        write(publicKeyTxt, "-----END PUBLIC KEY-----".getBytes(US_ASCII), WRITE, APPEND);
    }
}

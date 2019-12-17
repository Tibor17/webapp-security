import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.URI;
import java.util.UUID;

public final class Claims {
    private final URI iss;
    private final URI sub;
    private final URI[] aud;
    private final long iat;
    private final long nbf;
    private final long exp;
    private final UUID jti;

    public Claims(@Nonnull URI iss, @Nonnull URI sub, @Nonnull URI[] aud,
                  @Nonnegative long iat, @Nonnegative long nbf, @Nonnegative long exp, @Nonnull UUID jti) {
        this.iss = iss;
        this.sub = sub;
        this.aud = aud;
        this.iat = iat;
        this.nbf = nbf;
        this.exp = exp;
        this.jti = jti;
    }

    public URI getIss() {
        return iss;
    }

    public URI getSub() {
        return sub;
    }

    public URI[] getAud() {
        return aud;
    }

    public long getIat() {
        return iat;
    }

    public long getNbf() {
        return nbf;
    }

    public long getExp() {
        return exp;
    }

    public UUID getJti() {
        return jti;
    }
}

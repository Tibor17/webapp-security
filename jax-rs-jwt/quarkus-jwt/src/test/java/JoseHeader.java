import javax.annotation.Nonnull;

public final class JoseHeader {
    private final String cty;
    private final String type;
    private final String alg;

    public JoseHeader(@Nonnull String cty, @Nonnull String type, @Nonnull String alg) {
        this.cty = cty;
        this.type = type;
        this.alg = alg;
    }

    public String getCty() {
        return cty;
    }

    public String getType() {
        return type;
    }

    public String getAlg() {
        return alg;
    }
}

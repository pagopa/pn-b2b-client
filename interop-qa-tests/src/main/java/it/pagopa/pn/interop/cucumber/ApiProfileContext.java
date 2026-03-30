package it.pagopa.pn.interop.cucumber;

public final class ApiProfileContext {
    private static final ThreadLocal<ApiProfileConfig> CONTEXT = new ThreadLocal<>();

    private ApiProfileContext() {}

    public static void set(ApiProfileConfig config) {
        CONTEXT.set(config);
    }

    public static ApiProfileConfig getRequired() {
        ApiProfileConfig config = CONTEXT.get();
        if (config == null) {
            throw new IllegalStateException("ApiProfileConfig non impostato nel thread corrente");
        }
        return config;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

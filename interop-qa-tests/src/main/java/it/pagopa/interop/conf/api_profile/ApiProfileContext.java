package it.pagopa.interop.conf.api_profile;

public final class ApiProfileContext {
    private static final ThreadLocal<ApiProfileConfig> CONTEXT = new ThreadLocal<>();

    private ApiProfileContext() {}

    public static void set(ApiProfileConfig config) {
        CONTEXT.set(config);
    }

    public static ApiProfileConfig getRequired() {
        ApiProfileConfig config = CONTEXT.get();
        if (config == null) {
            // Default: V2, RIGHT_FIT, V1, M2M
            return ApiProfileConfig.defaultConfig();
        }
        return config;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

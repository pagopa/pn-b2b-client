package it.pagopa.interop.conf.api_profile;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public final class ApiProfileConfig {
    private final String apiMode;
    private final String apiM2mVersion;
    private final String apiBffVersion;
    private final String apiSet;

    public ApiProfileConfig(String apiMode, String apiM2mVersion, String apiBffVersion, String apiSet) {
        this.apiMode = Objects.requireNonNull(apiMode, "api.mode is required");
        this.apiM2mVersion = Objects.requireNonNull(apiM2mVersion, "api.m2m.version is required");
        this.apiBffVersion = Objects.requireNonNull(apiBffVersion, "api.bff.version is required");
        this.apiSet = Objects.requireNonNull(apiSet, "api.set is required");
    }

    public static ApiProfileConfig defaultConfig() {
        return new ApiProfileConfig("RIGHT_FIT", "V2", "V1", "M2M");
    }

}

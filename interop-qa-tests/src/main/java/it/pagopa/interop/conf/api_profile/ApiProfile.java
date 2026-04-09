package it.pagopa.interop.conf.api_profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiProfile {
    public enum ApiMode { BEST_FIT, RIGHT_FIT }
    public enum ApiM2MVersion { V2, V3 }
    public enum ApiBFFVersion { V1 }
    public enum ApiSet { BFF, M2M }

    private final ApiMode apiMode;
    private final ApiM2MVersion apiM2MVersion;
    private final ApiBFFVersion apiBFFVersion;
    private final ApiSet apiSet;

    public static ApiProfile from(ApiProfileConfig config) {
        ApiMode mode = ApiMode.valueOf(config.getApiMode());
        ApiM2MVersion m2MVersion = ApiM2MVersion.valueOf(config.getApiM2mVersion());
        ApiBFFVersion bffVersion = ApiBFFVersion.valueOf(config.getApiBffVersion());
        ApiSet set = ApiSet.valueOf(config.getApiSet());

        return new ApiProfile(mode, m2MVersion, bffVersion, set);
    }
}

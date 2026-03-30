package it.pagopa.interop.config.springconfig.springconfig;

import lombok.Getter;

@Getter
public class ApiProfile {
    public enum ApiMode { BEST_FIT, RIGHT_FIT }
    public enum ApiM2MVersion { V2, V3 }
    public enum ApiBFFVersion { V1 }
    public enum ApiSet { BFF, M2M }

    ApiMode apiMode;
    ApiM2MVersion apiM2MVersion;
    ApiBFFVersion apiBFFVersion;
    ApiSet apiSet;

    public static ApiProfile from(String apiMode, String apiM2MVersion, String apiBFFVersion, String apiSet) {
        ApiMode mode = ApiMode.valueOf(apiMode);
        ApiM2MVersion m2MVersion = ApiM2MVersion.valueOf(apiM2MVersion);
        ApiBFFVersion bffVersion = ApiBFFVersion.valueOf(apiBFFVersion);
        ApiSet set = ApiSet.valueOf(apiSet);

        return new ApiProfile(mode, m2MVersion, bffVersion, set);
    }

    public ApiProfile(ApiMode apiMode, ApiM2MVersion apiM2MVersion, ApiBFFVersion apiBFFVersion, ApiSet apiSet) {
        this.apiMode = apiMode;
        this.apiM2MVersion = apiM2MVersion;
        this.apiBFFVersion = apiBFFVersion;
        this.apiSet = apiSet;
    }
}

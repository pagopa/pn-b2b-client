package it.pagopa.interop.config.springconfig.springconfig;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class ApiProfile {
    public enum ApiMode { BEST_FIT, RIGHT_FIT }
    public enum ApiM2MVersion { V2, V3 }
    public enum ApiBFFVersion { V1 }

    ApiMode apiMode;
    ApiM2MVersion apiM2MVersion;
    ApiBFFVersion apiBFFVersion;

    public static ApiProfile from(String apiMode, String apiM2MVersion, String apiBFFVersion) {
        ApiMode mode = ApiMode.valueOf(apiMode);
        ApiM2MVersion m2MVersion = ApiM2MVersion.valueOf(apiM2MVersion);
        ApiBFFVersion bffVersion = ApiBFFVersion.valueOf(apiBFFVersion);

        return ApiProfile.of(mode, m2MVersion, bffVersion);
    }
}

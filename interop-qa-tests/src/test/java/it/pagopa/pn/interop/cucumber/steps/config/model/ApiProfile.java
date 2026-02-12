package it.pagopa.pn.interop.cucumber.steps.config.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "from")
@Getter
public class ApiProfile {
    public enum Mode {
        AUTO,
        M2M,
        BFF
    }

    public enum M2mVersion {
        V2,
        V3
    }

    private final Mode mode;
    private final M2mVersion m2MVersion;
}

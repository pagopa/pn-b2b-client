package it.pagopa.interop.authorization.domain.dpop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DpopHeaderPolicy {

    public enum Mode {
        NORMAL,              // comportamento standard
        MISSING_AUTH,        // non mettere Authorization
        INVALID_AUTH,        // mettere Authorization con token invalido
        MISSING_DPOP,        // non mettere header DPoP
        INVALID_DPOP         // mettere header DPoP invalido
    }

    private Mode mode = Mode.NORMAL;

    public static DpopHeaderPolicy of(Mode mode) {
        DpopHeaderPolicy policy = new DpopHeaderPolicy();
        policy.setMode(mode);
        return policy;
    }

    // opzionale: token invalido configurabile
    private String invalidAccessToken = "invalid-token";

    // opzionale: dpop invalido configurabile
    private String invalidDpopProof = "invalid-dpop-proof";
}

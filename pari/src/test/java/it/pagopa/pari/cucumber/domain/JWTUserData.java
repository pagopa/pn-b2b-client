package it.pagopa.pari.cucumber.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWTUserData {
    @Builder.Default
    private String aud = "idpay.register.welfare.pagopa.it";
    @Builder.Default
    private String iss = "https://api-io.dev.cstar.pagopa.it";
    private String uid;
    private String name;
    private String familyName;
    private String orgId;
    private String orgRole;
}

package it.pagopa.pari.cucumber.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWTUserData {
    @Builder.Default
    private String aud = "idpay.register.welfare.pagopa.it";
    @Builder.Default
    private String iss = "https://api-io.dev.cstar.pagopa.it";
    private String uid;
    private String name;
    private String familyName;
    private String email;
    private String acquirerId;
    private String merchantId;
    private String orgId;
//    private String orgVat;
//    private String orgFc;
    private String orgName;
    private String orgPartyRole;
    private String orgRole;
}

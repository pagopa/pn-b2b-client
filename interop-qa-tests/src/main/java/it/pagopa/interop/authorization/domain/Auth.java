package it.pagopa.interop.authorization.domain;

import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import lombok.*;

import java.security.KeyPair;

@AllArgsConstructor(staticName = "of")
@Getter
@Setter
@EqualsAndHashCode
public class Auth {
    private DpopHeaderPolicy dpopHeaderPolicy;
    private String clientId;
    private String tenantType;
    private String role;
    private KeyPair keyPair;
}

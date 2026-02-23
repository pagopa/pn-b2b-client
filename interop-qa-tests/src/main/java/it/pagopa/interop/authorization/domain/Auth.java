package it.pagopa.interop.authorization.domain;

import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;

@RequiredArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
public class Auth {
    private final DpopHeaderPolicy dpopHeaderPolicy;
    private final String clientId;
    private final String tenantType;
    private final String role;
    private final KeyPair keyPair;
}

package it.pagopa.interop.authorization.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;

@RequiredArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
public class Auth {
    private final String clientId;
    private final String tenantType;
    private final String role;
    private final KeyPair keyPair;
}

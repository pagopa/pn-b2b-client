package it.pagopa.interop.utils.jwt;

import java.util.Map;

public interface JWTPayload {

    Map<String, Object> toJWTClaims();
}

package it.pagopa.interop.common.interceptor.dpop.utils;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class DPoPAccessTokenSupplier implements java.util.function.Supplier<String> {

    private final DPoPTokenService tokenService;
    private volatile Auth auth;

    private static final class TokenState {
        final String token;
        final long expiresAtMs;
        TokenState(String token, long expiresAtMs) {
            this.token = token;
            this.expiresAtMs = expiresAtMs;
        }
    }

    private volatile TokenState state;

    public synchronized void setAuth(Auth newAuth) {
        if (Objects.equals(this.auth, newAuth)) {
            return;
        }
        this.auth = newAuth;
        this.state = null;
    }

    @Override
    public String get() {
        Auth a = this.auth;
        if (a == null) {
            throw new IllegalStateException("DPoPAccessTokenSupplier: auth non impostata. Chiama setAuth(Auth) prima di usare il client.");
        }

        long now = System.currentTimeMillis();
        TokenState s = this.state;
        if (s != null && now < (s.expiresAtMs - 10_000)) {
            return s.token;
        }

        synchronized (this) {
            a = this.auth;
            if (a == null) {
                throw new IllegalStateException("DPoPAccessTokenSupplier: auth non impostata. Chiama setAuth(Auth) prima di usare il client.");
            }

            now = System.currentTimeMillis();
            s = this.state;
            if (s != null && now < (s.expiresAtMs - 10_000)) {
                return s.token;
            }

            String dpopForTokenEndpoint = tokenService.buildDpopProof(a.getKeyPair());

            var pair = tokenService.getAccessTokenWithoutCache(
                    dpopForTokenEndpoint,
                    a.getClientId(),
                    a.getKeyPair(),
                    ClientAssertionOptions.ClientType.API,
                    a.getTenantType(),
                    null
            );

            VoucherResponse vr = pair.getRight();
            String newToken = vr.getAccessToken();
            if (newToken == null || newToken.isBlank()) {
                throw new IllegalStateException("Access token vuoto/null dal token endpoint");
            }

            Long expiresIn = vr.getExpiresIn();
            long newExpiresAt = (expiresIn != null)
                    ? System.currentTimeMillis() + expiresIn * 1000
                    : System.currentTimeMillis() + 240_000;

            this.state = new TokenState(newToken, newExpiresAt);
            return newToken;
        }
    }
}



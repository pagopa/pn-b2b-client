package it.pagopa.interop.common.interceptor.dpop.utils;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DPoPAccessTokenSupplier implements java.util.function.Supplier<String> {

    private final DPoPTokenService tokenService;
    private volatile Auth auth;

    private volatile String token;
    private volatile long expiresAtMs = 0;

    public synchronized void setAuth(Auth auth) {
        this.auth = auth;
        this.token = null;
        this.expiresAtMs = 0;
    }

    public void prefetch() {
        get();
    }

    @Override
    public String get() {
        Auth a = this.auth;
        if (a == null) {
            throw new IllegalStateException("DPoPAccessTokenSupplier: auth non impostata. Chiama setBearerToken(Auth) prima di usare il client.");
        }

        long now = System.currentTimeMillis();
        if (token != null && now < (expiresAtMs - 10_000)) return token;

        synchronized (this) {
            a = this.auth;
            if (a == null) {
                throw new IllegalStateException("DPoPAccessTokenSupplier: auth non impostata. Chiama setBearerToken(Auth) prima di usare il client.");
            }

            now = System.currentTimeMillis();
            if (token != null && now < (expiresAtMs - 10_000)) return token;

            // 1) DPoP proof per auth server (token endpoint)
            String dpopForTokenEndpoint = tokenService.buildDpopProof(a.getKeyPair());

            // 2) chiama token endpoint per ottenere voucher
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

            this.token = newToken;

            Long expiresIn = vr.getExpiresIn();
            this.expiresAtMs = (expiresIn != null)
                    ? System.currentTimeMillis() + expiresIn * 1000
                    : System.currentTimeMillis() + 240_000;

            return this.token;
        }
    }
}



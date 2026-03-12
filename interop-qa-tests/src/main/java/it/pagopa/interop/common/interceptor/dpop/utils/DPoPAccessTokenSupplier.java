package it.pagopa.interop.common.interceptor.dpop.utils;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DPoPAccessTokenSupplier implements Supplier<String> {

    private final DPoPTokenService tokenService;

    private static final long EXPIRY_SKEW_MS = 10_000;
    private static final long DEFAULT_EXPIRES_MS = 240_000;

    private static final class TokenState {
        final String token;
        final long expiresAtMs;

        TokenState(String token, long expiresAtMs) {
            this.token = token;
            this.expiresAtMs = expiresAtMs;
        }

        boolean isValid(long now) {
            return now < (expiresAtMs - EXPIRY_SKEW_MS);
        }
    }

    private static final class Snapshot {
        final Auth auth;
        final TokenState tokenState;

        Snapshot(Auth auth, TokenState tokenState) {
            this.auth = auth;
            this.tokenState = tokenState;
        }
    }

    private final ThreadLocal<Snapshot> snapshot =
            ThreadLocal.withInitial(() -> new Snapshot(null, null));

    public void setAuth(Auth newAuth) {
        Objects.requireNonNull(newAuth, "Auth must not be null");
        snapshot.set(new Snapshot(newAuth, null));
    }

    @Override
    public String get() {
        Snapshot snap = snapshot.get();
        Auth auth = snap.auth;

        if (auth == null) {
            throw new IllegalArgumentException(
                    "DPoPAccessTokenSupplier: auth non impostata. Chiama setAuth(Auth) prima di usare il client."
            );
        }

        long now = System.currentTimeMillis();
        TokenState cached = snap.tokenState;
        if (cached != null && cached.isValid(now)) {
            return cached.token;
        }

        synchronized (this) {
            snap = snapshot.get();
            auth = snap.auth;

            if (auth == null) {
                throw new IllegalStateException(
                        "DPoPAccessTokenSupplier: auth non impostata. Chiama setAuth(Auth) prima di usare il client."
                );
            }

            now = System.currentTimeMillis();
            cached = snap.tokenState;
            if (cached != null && cached.isValid(now)) {
                return cached.token;
            }

            String dpopForTokenEndpoint = tokenService.buildDpopProof(auth.getKeyPair());

            var pair = tokenService.getAccessTokenWithoutCache(
                    dpopForTokenEndpoint,
                    auth.getClientId(),
                    auth.getKeyPair(),
                    ClientAssertionOptions.ClientType.API,
                    auth.getTenantType(),
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
                    : System.currentTimeMillis() + DEFAULT_EXPIRES_MS;

            TokenState newState = new TokenState(newToken, newExpiresAt);

            // salva il token solo se l'auth corrente è ancora quella letta sotto lock
            snapshot.set(new Snapshot(auth, newState));

            return newToken;
        }
    }

    public void clear() {
        snapshot.remove();
    }

    public Auth getCurrentAuth() {
        Snapshot snap = snapshot.get();
        return snap.auth;
    }
}
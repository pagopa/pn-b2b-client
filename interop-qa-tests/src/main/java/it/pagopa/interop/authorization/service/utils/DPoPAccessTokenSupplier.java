package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class DPoPAccessTokenSupplier implements java.util.function.Supplier<String> {

    private final DPoPTokenService tokenService;
    @Setter
    private Auth auth;

    private volatile String token;
    private volatile long expiresAtMs = 0;

    @Override
    public String get() {
        long now = System.currentTimeMillis();
        if (token != null && now < (expiresAtMs - 10_000)) return token;

        synchronized (this) {
            now = System.currentTimeMillis();
            if (token != null && now < (expiresAtMs - 10_000)) return token;

            // 1) DPoP proof per auth server
            String dpopForTokenEndpoint = tokenService.buildDpopProof(auth.getKeyPair());

            // 2) chiama token endpoint per ottenere voucher
            var pair = tokenService.getAccessTokenWithoutCache(dpopForTokenEndpoint, auth.getClientId(), auth.getKeyPair(), ClientAssertionOptions.ClientType.API, auth.getTenantType(), auth.getPurposeId());
            VoucherResponse vr = pair.getRight();

            this.token = vr.getAccessToken();

            // 3) expiry: se hai expiresIn lo usi; altrimenti fallback prudente
            Long expiresIn = vr.getExpiresIn(); // se c'è nel tuo DTO
            if (expiresIn != null) {
                this.expiresAtMs = System.currentTimeMillis() + expiresIn * 1000;
            } else {
                this.expiresAtMs = System.currentTimeMillis() + 240_000; // fallback 4 minuti
            }
            return this.token;
        }
    }
}


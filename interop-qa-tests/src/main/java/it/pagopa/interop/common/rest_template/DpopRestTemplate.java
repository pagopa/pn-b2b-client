package it.pagopa.interop.common.rest_template;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.domain.dpop.AgidJwtProperties;
import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.common.interceptor.dpop.utils.AgidJwtSignatureVerifier;
import it.pagopa.interop.common.interceptor.dpop.utils.DPoPAccessTokenSupplier;
import it.pagopa.interop.common.interceptor.dpop.DPoPAuthInterceptor;
import it.pagopa.interop.common.interceptor.dpop.DPoPTokenInterceptor;
import it.pagopa.interop.common.interceptor.dpop.IntegrityValidationInterceptor;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DpopRestTemplate {

    @Getter
    @Delegate
    private final RestTemplate restTemplate;

    private final DPoPAuthInterceptor dpopInterceptor;
    private final DPoPTokenInterceptor dpopTokenInterceptor;
    private final DPoPAccessTokenSupplier dpopAccessTokenSupplier;

    public DpopRestTemplate(RestTemplate restTemplate,
                            DPoPTokenService dpopTokenService,
                            DPoPAccessTokenSupplier dpopAccessTokenSupplier,
                            List<ClientHttpRequestInterceptor> baseInterceptors,
                            KeyPair initialKeyPair,
                            AgidJwtProperties agidJwtProperties) {

        this.restTemplate = restTemplate;
        this.dpopAccessTokenSupplier = dpopAccessTokenSupplier;

        // Policy iniziale applicata finché non viene impostata una Auth specifica
        DpopHeaderPolicy initialPolicy = DpopHeaderPolicy.of(DpopHeaderPolicy.Mode.NORMAL);

        // Interceptor che aggiunge il token DPoP alle richieste
        this.dpopTokenInterceptor = new DPoPTokenInterceptor(
                dpopAccessTokenSupplier::get,
                initialPolicy
        );

        // Interceptor che costruisce la proof DPoP usando la key pair corrente
        this.dpopInterceptor = new DPoPAuthInterceptor(
                dpopTokenService,
                dpopAccessTokenSupplier,
                initialPolicy,
                initialKeyPair
        );

        // RestTemplate separato usato solo per recuperare il JWKS remoto.
        // Non deve riusare questo DpopRestTemplate per evitare ricorsioni
        // durante la validazione della response.
        RestTemplate jwksRestTemplate = new RestTemplate();

        // Componente che verifica la firma del token Agid-JWT-Signature
        // recuperando la chiave pubblica corretta dal JWKS configurato.
        AgidJwtSignatureVerifier agidJwtSignatureVerifier =
                new AgidJwtSignatureVerifier(jwksRestTemplate, agidJwtProperties);

        // Lista finale degli interceptor applicati al RestTemplate principale
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        if (baseInterceptors != null) {
            interceptors.addAll(baseInterceptors);
        }

        // L'ordine è importante:
        // - DPoPTokenInterceptor gestisce il token di accesso
        // - DPoPAuthInterceptor aggiunge la proof DPoP
        // - IntegrityValidationInterceptor valida digest e Agid-JWT-Signature sulla response
        interceptors.add(0, dpopInterceptor);
        interceptors.add(0, dpopTokenInterceptor);
        interceptors.add(new IntegrityValidationInterceptor(
                true,
                true,
                true,
                dpopAccessTokenSupplier::getCurrentAuth,
                agidJwtSignatureVerifier
        ));

        restTemplate.setInterceptors(interceptors);
    }

    public void setAuth(Auth auth) {
        if (auth == null) {
            return;
        }

        DpopHeaderPolicy incoming = auth.getDpopHeaderPolicy();
        DpopHeaderPolicy effectivePolicy = new DpopHeaderPolicy();

        if (incoming != null) {
            effectivePolicy.setMode(incoming.getMode());
            effectivePolicy.setInvalidAccessToken(incoming.getInvalidAccessToken());
            effectivePolicy.setInvalidDpopProof(incoming.getInvalidDpopProof());
        } else {
            effectivePolicy.setMode(DpopHeaderPolicy.Mode.NORMAL);
        }

        dpopInterceptor.setKeyPair(auth.getKeyPair());
        dpopInterceptor.setPolicy(effectivePolicy);
        dpopTokenInterceptor.setPolicy(effectivePolicy);

        dpopAccessTokenSupplier.setAuth(auth);
    }


}
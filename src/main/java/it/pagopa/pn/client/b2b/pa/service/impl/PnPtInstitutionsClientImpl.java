package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.api.InfoPtApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtDelegationStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitutionsResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPtInstitutionsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

/**
 * Client dell'openapi pn-pt-internal (pn-external-registries): elenco degli Enti PA che hanno delegato
 * un Partner Tecnologico. L'Ente PT che effettua la richiesta si seleziona con {@link #setPt(String)}.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPtInstitutionsClientImpl implements IPnPtInstitutionsClient {
    public static final String PT_A = "PT_A";
    public static final String PT_B = "PT_B";
    public static final String PT_SENZA_DELEGHE = "PT_SENZA_DELEGHE";

    private static final String PT_INSTITUTIONS_PATH = "/ext-registry-private/pt/v1/institutions";
    private static final String X_PAGOPA_PN_UID = "TestAutomation";
    private static final String SRC_CH_B2B = "B2B";

    private final RestTemplate restTemplate;
    private final String basePath;
    private final InfoPtApi infoPtApi;

    // TODO: placeholder, valorizzare le properties con i cx-id dei Partner Tecnologici di test
    private final String ptACxId;
    private final String ptBCxId;
    private final String ptSenzaDelegheCxId;

    private final CxTypeAuthFleet xPagopaPnCxType = CxTypeAuthFleet.PT;
    private String ptSetted;
    private String xPagopaPnCxId;

    public PnPtInstitutionsClientImpl(RestTemplate restTemplate,
                                      @Value("${pn.external.registry.base-url:${pn.delivery.base-url}}") String basePath,
                                      @Value("${pn.external.pt.cx-id.pt-a:PLACEHOLDER_PT_A}") String ptACxId,
                                      @Value("${pn.external.pt.cx-id.pt-b:PLACEHOLDER_PT_B}") String ptBCxId,
                                      @Value("${pn.external.pt.cx-id.pt-senza-deleghe:PLACEHOLDER_PT_SENZA_DELEGHE}") String ptSenzaDelegheCxId) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.ptACxId = ptACxId;
        this.ptBCxId = ptBCxId;
        this.ptSenzaDelegheCxId = ptSenzaDelegheCxId;
        this.infoPtApi = new InfoPtApi(newApiClient(restTemplate, basePath));
        setPt(PT_A);
    }

    // le API internal non richiedono bearer token: l'identita' e' veicolata dagli header cx-*
    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    @Override
    public void setPt(String ptName) {
        this.xPagopaPnCxId = getPtCxId(ptName);
        this.ptSetted = ptName.toUpperCase();
    }

    @Override
    public String getPtCxId(String ptName) {
        return switch (ptName.toUpperCase()) {
            case PT_A -> ptACxId;
            case PT_B -> ptBCxId;
            case PT_SENZA_DELEGHE -> ptSenzaDelegheCxId;
            default -> throw new IllegalArgumentException("Ente PT non gestito: " + ptName);
        };
    }

    @Override
    public String getPtSetted() {
        return ptSetted;
    }

    @Override
    public PtInstitutionsResponse getPtInstitutions(String search, PtDelegationStatus status, Integer limit, String lastKey) throws RestClientException {
        return infoPtApi.getPtInstitutions(xPagopaPnCxId, xPagopaPnCxType, X_PAGOPA_PN_UID, SRC_CH_B2B, search, status, limit, lastKey);
    }

    @Override
    public ResponseEntity<String> getPtInstitutionsRaw(Map<String, String> queryParams, boolean sendCxId) throws RestClientException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(basePath).path(PT_INSTITUTIONS_PATH);
        queryParams.forEach(uriBuilder::queryParam);
        URI uri = uriBuilder.build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        if (sendCxId) {
            headers.add("x-pagopa-pn-cx-id", xPagopaPnCxId);
        }
        headers.add("x-pagopa-pn-cx-type", xPagopaPnCxType.getValue());
        headers.add("x-pagopa-pn-uid", X_PAGOPA_PN_UID);
        headers.add("x-pagopa-pn-src-ch", SRC_CH_B2B);

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
}

package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.api.MandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.MandateCountsDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.SearchMandateRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.SearchMandateResponseDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.UpdateRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CUCUMBER_SPA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GHERKIN_SRL;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CUCUMBER;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_GHERKIN;

/**
 * Implementazione di IPnWebMandateClient sull'openapi pn-mandate-internal: usata dal flusso
 * @useB2B per tutte le utenze che NON sono PG dedicate _B2B (PF e PG classiche). Selezionata
 * a runtime dal router {@link B2bMandateServiceClientImpl}.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MandateInternalClientImpl implements IPnWebMandateClient {
    // xPagopaPnUid: identificativo tecnico di chi esegue la chiamata (richiesto solo da createMandate);
    // per le chiamate di test automation si usa convenzionalmente questo valore fisso, coerente con
    // NotificationSearchParamMapper. Diverso da xPagopaPnCxId, che identifica il cliente/destinatario.
    private static final String X_PAGOPA_PN_UID = "TestAutomation";
    private static final String SRC_CH_B2B = "B2B";

    private final RestTemplate restTemplate;
    private final String webBasePath;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String pg1ClassicToken;
    private final String pg2ClassicToken;
    private final DestinatarioRegistry destinatarioRegistry;
    private final MandateServiceApi mandateServiceApi;

    private BearerTokenType bearerTokenSetted;
    private CxTypeAuthFleet xPagopaPnCxType;
    private String xPagopaPnCxRole = null;
    private String xPagopaPnCxId;

    public MandateInternalClientImpl(RestTemplate restTemplate,
                                     @Value("${pn.delivery.base-url}") String webBasePath,
                                     @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                     @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                     @Value("${pn.bearer-token.pg1}") String pg1ClassicToken,
                                     @Value("${pn.bearer-token.pg2}") String pg2ClassicToken,
                                     DestinatarioRegistry destinatarioRegistry) {
        this.restTemplate = restTemplate;
        this.webBasePath = webBasePath;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.pg1ClassicToken = pg1ClassicToken;
        this.pg2ClassicToken = pg2ClassicToken;
        this.destinatarioRegistry = destinatarioRegistry;
        this.mandateServiceApi = new MandateServiceApi(newApiClient(restTemplate, webBasePath, pg1ClassicToken));
        setBearerToken(BearerTokenType.PG_1);
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return apiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        String token;
        CxTypeAuthFleet cxType;
        String uid;
        switch (bearerToken) {
            case PG_1 -> {
                token = pg1ClassicToken;
                cxType = CxTypeAuthFleet.PG;
                uid = destinatarioRegistry.destinatario(GHERKIN_SRL).getUid();
                this.xPagopaPnCxRole = "admin";
            }
            case PG_2 -> {
                token = pg2ClassicToken;
                cxType = CxTypeAuthFleet.PG;
                uid = destinatarioRegistry.destinatario(CUCUMBER_SPA).getUid();
                this.xPagopaPnCxRole = "admin";
            }
            case USER_1 -> {
                token = marioCucumberBearerToken;
                cxType = CxTypeAuthFleet.PF;
                uid = destinatarioRegistry.destinatario(MARIO_CUCUMBER).getUid();
            }
            case USER_2 -> {
                token = marioGherkinBearerToken;
                cxType = CxTypeAuthFleet.PF;
                uid = destinatarioRegistry.destinatario(MARIO_GHERKIN).getUid();
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        this.mandateServiceApi.setApiClient(newApiClient(restTemplate, webBasePath, token));
        this.xPagopaPnCxType = cxType;
        this.xPagopaPnCxId = cxType.getValue() + "-" + uid;
        this.bearerTokenSetted = bearerToken;
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }

    @Override
    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        mandateServiceApi.acceptMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, null, xPagopaPnCxRole, acceptRequestDto);
    }

    @Override
    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return mandateServiceApi.countMandatesByDelegate(xPagopaPnCxId, xPagopaPnCxType, null, xPagopaPnCxRole, status);
    }

    @Override
    public void createMandate(MandateDto mandateDto) throws RestClientException {
        mandateServiceApi.createMandate(X_PAGOPA_PN_UID, xPagopaPnCxId, xPagopaPnCxType, SRC_CH_B2B, null, xPagopaPnCxRole, mandateDto);
    }

    @Override
    public void updateMandate(String mandateId, UpdateRequestDto updateRequestDto) throws RestClientException {
        mandateServiceApi.updateMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, null, xPagopaPnCxRole, updateRequestDto);
    }

    @Override
    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
        mandateServiceApi.updateMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, updateRequestDto);
    }

    @Override
    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return mandateServiceApi.listMandatesByDelegate1(xPagopaPnCxId, xPagopaPnCxType, null, xPagopaPnCxRole, status);
    }

    @Override
    public List<MandateDto> listMandatesByDelegator1() throws RestClientException {
        return mandateServiceApi.listMandatesByDelegator1(xPagopaPnCxId, xPagopaPnCxType, null, xPagopaPnCxRole);
    }

    @Override
    public void rejectMandate(String mandateId) throws RestClientException {
        mandateServiceApi.rejectMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, null, xPagopaPnCxRole);
    }

    @Override
    public void revokeMandate(String mandateId) throws RestClientException {
        mandateServiceApi.revokeMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, null, xPagopaPnCxRole);
    }

    @Override
    public List<MandateDto> searchMandatesByDelegate(String taxId, List<String> groups) throws RestClientException {
        return search(taxId, null, groups);
    }

    @Override
    public List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId, List<String> status, List<String> groups) throws RestClientException {
        return search(taxId, status, groups);
    }

    private List<MandateDto> search(String taxId, List<String> status, List<String> groups) {
        SearchMandateRequestDto requestDto = new SearchMandateRequestDto();
        requestDto.setTaxId(taxId);
        requestDto.setGroups(groups);
        requestDto.setStatus(status);
        SearchMandateResponseDto responseDto = mandateServiceApi.searchMandatesByDelegate(xPagopaPnCxId, xPagopaPnCxType, 10, null, xPagopaPnCxRole, null, requestDto);
        return responseDto != null ? responseDto.getResultsPage() : null;
    }
}

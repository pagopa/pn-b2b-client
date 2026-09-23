package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.api.MandateServiceApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateRequestDto;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.MandateCountsDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.SearchMandateResponseDto;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.UpdateRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;

/**
 * Implementazione di IPnWebMandateClient sull'openapi pn-mandate-b2b-pg-external: riservata alle
 * PG dedicate con token _B2B (pn.bearer-token-b2b.pg1/pg2). Selezionata a runtime dal router
 * {@link B2bMandateServiceClientImpl}.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MandateB2BExternalClientImpl implements IPnWebMandateClient {
    private final RestTemplate restTemplate;
    private final String b2bBasePath;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final MandateServiceApi mandateServiceApi;

    private BearerTokenType bearerTokenSetted;

    public MandateB2BExternalClientImpl(RestTemplate restTemplate,
                                        @Value("${pn.external.dest.base-url}") String b2bBasePath,
                                        @Value("${pn.bearer-token-b2b.pg1}") String gherkinSrlBearerToken,
                                        @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.restTemplate = restTemplate;
        this.b2bBasePath = b2bBasePath;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.mandateServiceApi = new MandateServiceApi(newApiClient(restTemplate, b2bBasePath, gherkinSrlBearerToken));
        setBearerToken(BearerTokenType.PG_B2B_1);
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        String token = switch (bearerToken) {
            case PG_B2B_1 -> gherkinSrlBearerToken;
            case PG_B2B_2 -> cucumberSpaBearerToken;
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        };
        this.mandateServiceApi.setApiClient(newApiClient(restTemplate, b2bBasePath, token));
        this.bearerTokenSetted = bearerToken;
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }

    @Override
    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        mandateServiceApi.acceptMandate(mandateId, deepCopy(acceptRequestDto, it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.AcceptRequestDto.class));
    }

    @Override
    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        // pn-mandate-b2b-pg-external non espone il conteggio deleghe
        throw new UnsupportedOperationException();
    }

    @Override
    public void createMandate(MandateDto mandateDto) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto converted =
                deepCopy(mandateDto, it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto.class);
        deepCopy(mandateServiceApi.createMandate(converted), MandateDto.class);
    }

    @Override
    public void updateMandate(String mandateId, UpdateRequestDto updateRequestDto) throws RestClientException {
        mandateServiceApi.updateMandate(mandateId, deepCopy(updateRequestDto, it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UpdateRequestDto.class));
    }

    @Override
    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
        // l'identita' e' gia' determinata dal bearer token della PG _B2B: gli header cx-* espliciti non sono supportati da questa openapi
        updateMandate(mandateId, updateRequestDto);
    }

    @Override
    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return mandateServiceApi.listMandatesByDelegate1(status).stream()
                .map(x -> deepCopy(x, MandateDto.class))
                .toList();
    }

    @Override
    public List<MandateDto> listMandatesByDelegator1() throws RestClientException {
        return mandateServiceApi.listMandatesByDelegator1().stream()
                .map(x -> deepCopy(x, MandateDto.class))
                .toList();
    }

    @Override
    public void rejectMandate(String mandateId) throws RestClientException {
        mandateServiceApi.rejectMandate(mandateId);
    }

    @Override
    public void revokeMandate(String mandateId) throws RestClientException {
        mandateServiceApi.revokeMandate(mandateId);
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
        SearchMandateResponseDto responseDto = deepCopy(
                mandateServiceApi.searchMandatesByDelegate(10, null, requestDto),
                SearchMandateResponseDto.class);
        return responseDto != null ? responseDto.getResultsPage() : null;
    }
}

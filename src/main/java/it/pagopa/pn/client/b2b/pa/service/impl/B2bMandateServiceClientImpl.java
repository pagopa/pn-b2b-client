package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.api.MandateServiceApi;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2bMandateServiceClientImpl implements IPnWebMandateClient {
    private final RestTemplate restTemplate;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final MandateServiceApi mandateServiceApi;
    private BearerTokenType bearerTokenSetted;

    public B2bMandateServiceClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.external.dest.base-url}") String basePath,
                                       @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                       @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.restTemplate = restTemplate;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.bearerTokenSetted = BearerTokenType.PG_2;
        this.mandateServiceApi = new MandateServiceApi(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.AcceptRequestDto acceptRequestDto1 = deepCopy(acceptRequestDto, it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.AcceptRequestDto.class);
        mandateServiceApi.acceptMandate(mandateId, acceptRequestDto1);
    }

    @Override
    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MandateDto createMandate(MandateDto mandateDto) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto convertedMandateDto = deepCopy(mandateDto, it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto.class);
        return deepCopy(mandateServiceApi.createMandate(convertedMandateDto), MandateDto.class);
    }

    @Override
    public void updateMandate(String mandateId, UpdateRequestDto updateRequestDto) throws RestClientException {
        this.mandateServiceApi.updateMandate(mandateId,deepCopy(updateRequestDto,it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UpdateRequestDto.class));
    }

    @Override
    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return mandateServiceApi.listMandatesByDelegate1(status)
                .stream()
                .map(x -> deepCopy(x, MandateDto.class))
                .toList();
    }

    @Override
    public List<MandateDto> listMandatesByDelegator1() throws RestClientException {
        return mandateServiceApi.listMandatesByDelegator1()
                .stream()
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
        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateRequestDto searchMandateRequestDto = new it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateRequestDto();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateResponseDto responseDto= mandateServiceApi.searchMandatesByDelegate(10, null, searchMandateRequestDto);
        return (responseDto != null && responseDto.getResultsPage() != null)
                ? responseDto.getResultsPage().stream().map(x -> deepCopy(x, MandateDto.class)).toList() : null;
    }

    @Override
    public List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId, List<String> status, List<String> groups) throws RestClientException {
        SearchMandateRequestDto searchMandateRequestDto = new SearchMandateRequestDto();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        searchMandateRequestDto.setStatus(status);
        SearchMandateResponseDto responseDto = deepCopy(
                mandateServiceApi.searchMandatesByDelegate(10, null,
                        deepCopy(searchMandateRequestDto,
                                it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateRequestDto.class)),
                it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.SearchMandateResponseDto.class);
        return responseDto != null ? responseDto.getResultsPage() : null;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }

    private <T> T deepCopy( Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        try {
            String json = objMapper.writeValueAsString( obj );
            return objMapper.readValue( json, toClass );
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }
}

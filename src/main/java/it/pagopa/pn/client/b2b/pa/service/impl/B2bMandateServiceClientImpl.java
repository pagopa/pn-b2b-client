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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class B2bMandateServiceClientImpl implements IPnWebMandateClient {
    private final RestTemplate restTemplate;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final MandateServiceApi mandateServiceApi;

    public B2bMandateServiceClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.delivery.base-url}") String basePath,
                                       @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                       @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                       @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                       @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.mandateServiceApi = new MandateServiceApi(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
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
    public ResponseEntity<Void> acceptMandateWithHttpInfo(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.AcceptRequestDto acceptRequestDto1 = deepCopy(acceptRequestDto, it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.AcceptRequestDto.class);
        return  mandateServiceApi.acceptMandateWithHttpInfo(mandateId, acceptRequestDto1);
    }

    @Override
    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return null;
    }

    @Override
    public MandateDto createMandate(MandateDto mandateDto) throws RestClientException {
        return null;
    }

    @Override
    public void updateMandate(String mandateId, UpdateRequestDto updateRequestDto) throws RestClientException {
        this.mandateServiceApi.updateMandate(mandateId,deepCopy(updateRequestDto,it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UpdateRequestDto.class));
    }

    @Override
    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {

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
        return List.of();
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
    public List<MandateDto> searchMandatesByDelegate(String taxId,List<String> groups) throws RestClientException {

        SearchMandateRequestDto searchMandateRequestDto = new SearchMandateRequestDto();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);

        List<MandateDto> result = null;
        SearchMandateResponseDto res = deepCopy(
                mandateServiceApi.searchMandatesByDelegate(10, null,
                        deepCopy(searchMandateRequestDto,
                                it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateRequestDto.class)),
                it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.SearchMandateResponseDto.class);
        if (res!= null){
            result = res.getResultsPage();
        }
        return result;
    }
    @Override
    public List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId,List<String> status, List<String> groups) throws RestClientException {

        SearchMandateRequestDto searchMandateRequestDto = new SearchMandateRequestDto();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        searchMandateRequestDto.setStatus(status);

        List<MandateDto> result = null;
        SearchMandateResponseDto res = deepCopy(
                mandateServiceApi.searchMandatesByDelegate(10, null,
                deepCopy(searchMandateRequestDto,
                        it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.SearchMandateRequestDto.class)),
                it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.SearchMandateResponseDto.class);
        if (res!= null){
            result = res.getResultsPage();
        }
        return result;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
                beenSet = true;
            }
            case USER_2 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
                beenSet = true;
            }
            case PG_1 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                beenSet = true;
            }
            case PG_2 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                beenSet = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
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

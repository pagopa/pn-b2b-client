package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.api.MandateApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffAcceptRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffNewMandateRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffSearchMandateRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffSearchMandateResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffUpdateRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.internal.mandate.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebMandateExternalClientImpl implements IPnWebMandateClient {
    private final RestTemplate restTemplate;
    private final MandateApi mandateServiceApi;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private BearerTokenType bearerTokenSetted;
    private final String userAgent;
    private final String basePath;

    public PnWebMandateExternalClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.webapi.external.base-url}") String basePath,
                                          @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                          @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                          @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                          @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                          @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                          @Value("${pn.webapi.external.user-agent}") String userAgent) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.mandateServiceApi = new MandateApi(newApiClient(restTemplate, basePath, marioCucumberBearerToken,userAgent));
        this.bearerTokenSetted = BearerTokenType.USER_1;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
            }
            case USER_2 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
            }
            case USER_3 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
            }
            case PG_1 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
            }
            case PG_2 -> {
                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    @Override
    public void updateMandate(String mandateId, UpdateRequestDto updateRequestDto) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        mandateServiceApi.acceptMandateV1(mandateId, new BffAcceptRequest().verificationCode(acceptRequestDto.getVerificationCode())
                .groups(acceptRequestDto.getGroups()));
    }

    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return new MandateCountsDto().value(mandateServiceApi.countMandatesByDelegateV1(status).getValue());
    }


    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
         mandateServiceApi.updateMandateV1(mandateId, new BffUpdateRequest().groups(updateRequestDto.getGroups()));
    }

    public void createMandate(MandateDto mandateDto) throws RestClientException {
        mandateServiceApi.createMandateV1(deepCopy(mandateDto, BffNewMandateRequest.class));
    }


    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return mandateServiceApi.getMandatesByDelegateV1(status).stream()
                .map(item -> deepCopy(item, MandateDto.class))
                .toList();
    }

    public List<MandateDto> searchMandatesByDelegate(String taxId, List<String> groups) throws RestClientException {
        BffSearchMandateRequest searchMandateRequestDto = new BffSearchMandateRequest();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        BffSearchMandateResponse responseDto = mandateServiceApi.searchMandatesByDelegateV1(10, searchMandateRequestDto, null);
        return responseDto != null ? responseDto.getResultsPage().stream().map(item -> deepCopy(item, MandateDto.class)).toList() : null;
    }

    public List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId, List<String> status, List<String> groups) throws RestClientException {
        BffSearchMandateRequest searchMandateRequestDto = new BffSearchMandateRequest();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        searchMandateRequestDto.setStatus(status);
        BffSearchMandateResponse responseDto = mandateServiceApi.searchMandatesByDelegateV1(10, searchMandateRequestDto, null);
        return responseDto != null ? responseDto.getResultsPage().stream().map(item -> deepCopy(item, MandateDto.class)).toList() : null;
    }

    public List<MandateDto> listMandatesByDelegator1() throws RestClientException {
        return mandateServiceApi.getMandatesByDelegatorV1().stream().map(item -> deepCopy(item, MandateDto.class)).toList();
    }


    public void rejectMandate(String mandateId) throws RestClientException {
        mandateServiceApi.rejectMandateV1(mandateId);
    }


    public void revokeMandate(String mandateId) throws RestClientException {
        mandateServiceApi.revokeMandateV1(mandateId);
    }

}

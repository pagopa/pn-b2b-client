package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenConfigs;
import it.pagopa.pn.client.b2b.pa.config.PnExternalApiKeyConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.api.MandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebMandateExternalClientImpl implements IPnWebMandateClient {
    private final RestTemplate restTemplate;
    private final MandateServiceApi mandateServiceApi;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private BearerTokenType bearerTokenSetted;
    private final String userAgent;
    private final String basePath;


    @Autowired
    public PnWebMandateExternalClientImpl(RestTemplate restTemplate,
                                          PnBearerTokenConfigs pnBearerTokenConfigs,
                                          PnBaseUrlConfig pnBaseUrlConfig,
                                          PnExternalApiKeyConfig pnExternalApiKeyConfig) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = pnBearerTokenConfigs.getUser1();
        this.marioGherkinBearerToken = pnBearerTokenConfigs.getUser2();
        this.leonardoBearerToken = pnBearerTokenConfigs.getUser3();
        this.gherkinSrlBearerToken = pnBearerTokenConfigs.getPg1();
        this.cucumberSpaBearerToken = pnBearerTokenConfigs.getPg2();
        this.basePath = pnBaseUrlConfig.getWebApiExternalBaseUrl();
        this.userAgent = pnExternalApiKeyConfig.getUserAgent();
        this.mandateServiceApi = new MandateServiceApi(newApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
        this.bearerTokenSetted = BearerTokenType.USER_1;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.setBearerToken(bearerToken);
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
        mandateServiceApi.acceptMandate(mandateId, acceptRequestDto);
    }

    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return mandateServiceApi.countMandatesByDelegate(status);
    }


    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
        mandateServiceApi.updateMandate(xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, updateRequestDto);
    }

    public MandateDto createMandate(MandateDto mandateDto) throws RestClientException {
        return mandateServiceApi.createMandate(mandateDto);
    }


    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return mandateServiceApi.listMandatesByDelegate1(status);
    }

    public List<MandateDto> searchMandatesByDelegate(String taxId, List<String> groups) throws RestClientException {
        SearchMandateRequestDto searchMandateRequestDto = new SearchMandateRequestDto();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        SearchMandateResponseDto responseDto = mandateServiceApi.searchMandatesByDelegate(10, null, searchMandateRequestDto);
        return responseDto != null ? responseDto.getResultsPage() : null;
    }

    public List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId, List<String> status, List<String> groups) throws RestClientException {
        SearchMandateRequestDto searchMandateRequestDto = new SearchMandateRequestDto();
        searchMandateRequestDto.setTaxId(taxId);
        searchMandateRequestDto.setGroups(groups);
        searchMandateRequestDto.setStatus(status);
        SearchMandateResponseDto responseDto = mandateServiceApi.searchMandatesByDelegate(10, null, searchMandateRequestDto);
        return responseDto != null ? responseDto.getResultsPage() : null;
    }

    public List<MandateDto> listMandatesByDelegator1() throws RestClientException {
        return mandateServiceApi.listMandatesByDelegator1();
    }


    public void rejectMandate(String mandateId) throws RestClientException {
        mandateServiceApi.rejectMandate(mandateId);
    }


    public void revokeMandate(String mandateId) throws RestClientException {
        mandateServiceApi.revokeMandate(mandateId);
    }

}

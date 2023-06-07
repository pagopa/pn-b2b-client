package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.api.MandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebMandateExternalClientImpl implements IPnWebMandateClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final MandateServiceApi mandateServiceApi;

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_1;
    private final String userAgent;
    private final String basePath;

    private final String idOrganizationGherkinSrl;

    private final String idOrganizationCucumberSpa;

    public PnWebMandateExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
            @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
            @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
            @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
            @Value("${pn.external.bearer-token-pg1.id}") String idOrganizationGherkinSrl,
            @Value("${pn.external.bearer-token-pg2.id}") String idOrganizationCucumberSpa,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.idOrganizationGherkinSrl = idOrganizationGherkinSrl;
        this.idOrganizationCucumberSpa = idOrganizationCucumberSpa;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.mandateServiceApi = new MandateServiceApi( newApiClient( restTemplate, basePath, marioCucumberBearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken){
            case USER_1:
                this.mandateServiceApi.setApiClient(newApiClient( restTemplate, basePath, marioCucumberBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.mandateServiceApi.setApiClient(newApiClient( restTemplate, basePath, marioGherkinBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
                break;
            case PG_1:
                this.mandateServiceApi.setApiClient(newApiClient( restTemplate, basePath, gherkinSrlBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
                break;
            case PG_2:
                this.mandateServiceApi.setApiClient(newApiClient( restTemplate, basePath, cucumberSpaBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }



    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        mandateServiceApi.acceptMandate(mandateId, acceptRequestDto);
    }


    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return mandateServiceApi.countMandatesByDelegate(status);
    }


    public void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException {
         mandateServiceApi.updateMandate( xPagopaPnCxId,  xPagopaPnCxType,  mandateId, xPagopaPnCxGroups,  xPagopaPnCxRole,  updateRequestDto);
    }

    public MandateDto createMandate(MandateDto mandateDto) throws RestClientException {
        return mandateServiceApi.createMandate(mandateDto);
    }


    public List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException {
        return mandateServiceApi.listMandatesByDelegate1(status);
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

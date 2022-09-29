package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.api.MandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateCountsDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class PnWebMandateExternalClientImpl implements IPnWebMandateClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final MandateServiceApi mandateServiceApi;

    private final String fieramoscaEBearerToken;
    private final String cristoforoCBearerToken;
    private final String userAgent;
    private final String basePath;

    public PnWebMandateExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.external.bearer-token-FieramoscaE.pagopa}") String fieramoscaEBearerToken,
            @Value("${pn.external.bearer-token-CristoforoC.pagopa}") String cristoforoCBearerToken,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.fieramoscaEBearerToken = fieramoscaEBearerToken;
        this.cristoforoCBearerToken = cristoforoCBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.mandateServiceApi = new MandateServiceApi( newApiClient( restTemplate, basePath, fieramoscaEBearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    public boolean setBearerToken(String user){
        boolean beenSet = false;
        user = user.toUpperCase();
        switch (user){
            case "CLMCST42R12D969Z":
                this.mandateServiceApi.setApiClient(newApiClient( restTemplate, basePath, cristoforoCBearerToken,userAgent));
                beenSet = true;
                break;
            case "FRMTTR76M06B715E":
                this.mandateServiceApi.setApiClient(newApiClient( restTemplate, basePath, fieramoscaEBearerToken,userAgent));
                beenSet = true;
                break;
        }
        return beenSet;
    }

    public void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException {
        mandateServiceApi.acceptMandate(mandateId, acceptRequestDto);
    }


    public MandateCountsDto countMandatesByDelegate(String status) throws RestClientException {
        return mandateServiceApi.countMandatesByDelegate(status);
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

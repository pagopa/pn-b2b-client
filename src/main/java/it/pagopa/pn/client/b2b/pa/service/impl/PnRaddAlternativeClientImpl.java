package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.api.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddAlternativeClientImpl implements IPnRaddAlternativeClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;

    private final String basePath;
    private final String Raddista1;
    private final String Raddista2;
    private final String RaddistaNonCensito;
    private final String RaddistaDatiErrati;
    private final String RaddistaJwtScaduto;
    private final String RaddistaAudErrato;
    private final String RaddistaJwtKidDiverso;
    private final String RaddistaJwtPrivateDiverso;

    private AuthTokenRaddType IssuerTokenSetted = AuthTokenRaddType.ISSUER_1;

    private final ActOperationsApi actOperationsApi;
    private final AorOperationsApi aorOperationsApi;
    private final DocumentOperationsApi documentOperationsApi;
    private final NotificationInquiryApi notificationInquiryApi;


    public PnRaddAlternativeClientImpl(ApplicationContext ctx, RestTemplate restTemplate, @Value("${pn.radd.alt.external.base-url}") String basePath,
                                       @Value("${pn.external.bearer-token-radd-1}") String Raddista1,
                                       @Value("${pn.external.bearer-token-radd-2}") String Raddista2,
                                       @Value("${pn.external.bearer-token-radd-non-censito}") String RaddistaNonCensito,
                                       @Value("${pn.external.bearer-token-radd-dati-errati}") String RaddistaDatiErrati,
                                       @Value("${pn.external.bearer-token-radd-jwt-scaduto}") String RaddistaJwtScaduto,
                                       @Value("${pn.external.bearer-token-radd-aud-erratto}") String RaddistaAudErrato,
                                       @Value("${pn.external.bearer-token-radd-kid-diverso}") String RaddistaJwtKidDiverso,
                                       @Value("${pn.external.bearer-token-radd-privateKey-diverso}") String RaddistaJwtPrivateDiverso) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.Raddista1=Raddista1;
        this.Raddista2=Raddista2;
        this.RaddistaNonCensito=RaddistaNonCensito;
        this.RaddistaDatiErrati=RaddistaDatiErrati;
        this.RaddistaJwtScaduto=RaddistaJwtScaduto;
        this.RaddistaAudErrato=RaddistaAudErrato;
        this.RaddistaJwtKidDiverso=RaddistaJwtKidDiverso;
        this.RaddistaJwtPrivateDiverso=RaddistaJwtPrivateDiverso;


        this.actOperationsApi = new ActOperationsApi(newApiClientExternal(restTemplate,basePath, Raddista1));
        this.aorOperationsApi = new AorOperationsApi(newApiClientExternal(restTemplate,basePath, Raddista1));
        this.documentOperationsApi = new DocumentOperationsApi(newApiClientExternal(restTemplate,basePath, Raddista1));
        this.notificationInquiryApi = new NotificationInquiryApi(newApiClient(restTemplate,basePath));

    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClientExternal(RestTemplate restTemplate, String basePath,String token ) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClient = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("Authorization", "Bearer " + token);
        return newApiClient;
    }


    public void selectRaddista(String token){
        this.actOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.aorOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.documentOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
    }


    public ActInquiryResponse actInquiry( String uid, String recipientTaxId, String recipientType, String qrCode, String iun) throws RestClientException {
        return this.actOperationsApi.actInquiryWithHttpInfo(uid, recipientTaxId, recipientType, qrCode, iun).getBody();
    }

    public AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
        return this.actOperationsApi.abortActTransactionWithHttpInfo(uid, abortTransactionRequest).getBody();
    }


    public CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
        return this.actOperationsApi.completeActTransactionWithHttpInfo(uid, completeTransactionRequest).getBody();
    }


    public StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest actStartTransactionRequest) throws RestClientException {
        return this.actOperationsApi.startActTransactionWithHttpInfo(uid, actStartTransactionRequest).getBody();
    }

    public AORInquiryResponse aorInquiry( String uid, String recipientTaxId, String recipientType) throws RestClientException {
        return this.aorOperationsApi.aorInquiryWithHttpInfo( uid, recipientTaxId, recipientType).getBody();
    }


    public AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
        return this.aorOperationsApi.abortAorTransactionWithHttpInfo(uid, abortTransactionRequest).getBody();
    }


    public CompleteTransactionResponse completeAorTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
        return this.aorOperationsApi.completeAorTransactionWithHttpInfo(uid, completeTransactionRequest).getBody();
    }


    public StartTransactionResponse startAorTransaction(String uid, AorStartTransactionRequest aorStartTransactionRequest) throws RestClientException {
        return this.aorOperationsApi.startAorTransactionWithHttpInfo(uid, aorStartTransactionRequest).getBody();
    }


    public DocumentUploadResponse documentUpload( String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException {
        return this.documentOperationsApi.documentUploadWithHttpInfo( uid, documentUploadRequest).getBody();
    }

    public OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        return this.notificationInquiryApi.getActPracticesByInternalIdWithHttpInfo(internalId, filterRequest).getBody();
    }

    public OperationsResponse getActPracticesByIun(String iun) throws RestClientException {
        return this.notificationInquiryApi.getActPracticesByIunWithHttpInfo(iun).getBody();
    }

    public OperationActResponse getActTransactionByOperationId(String transactionId) throws RestClientException {
        return this.notificationInquiryApi.getActTransactionByTransactionIdWithHttpInfo(transactionId).getBody();
    }

    public OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        return this.notificationInquiryApi.getAorPracticesByInternalIdWithHttpInfo(internalId, filterRequest).getBody();
    }

    public OperationsResponse getAorPracticesByIun(String iun) throws RestClientException {
        return this.notificationInquiryApi.getAorPracticesByIunWithHttpInfo(iun).getBody();
    }

    public OperationAorResponse getAorTransactionByOperationId(String transactionId) throws RestClientException {
        return this.notificationInquiryApi.getAorTransactionByTransactionIdWithHttpInfo(transactionId).getBody();
    }

    @Override
    public byte[] documentDownload(String operationType, String operationId, String attchamentId) throws RestClientException {
        return this.documentOperationsApi.documentDownload(operationType,operationId, attchamentId);
    }




    @Override
    public boolean setAuthTokenRadd(AuthTokenRaddType issuerToken) {
        boolean beenSet = false;
        switch (issuerToken){
            case ISSUER_1 -> {
                selectRaddista(this.Raddista1);
                beenSet=true;
            }
            case ISSUER_2 -> {
                selectRaddista(this.Raddista2);
                beenSet=true;
            }
            case ISSUER_NON_CENSITO -> {
                selectRaddista(this.RaddistaNonCensito);
                beenSet=true;
            }
            case DATI_ERRATI -> {
                selectRaddista(this.RaddistaDatiErrati);
                beenSet=true;
            }
            case ISSUER_SCADUTO -> {
                selectRaddista(this.RaddistaJwtScaduto);
                beenSet=true;
            }
            case AUD_ERRATA -> {
                selectRaddista(this.RaddistaAudErrato);
                beenSet=true;
            }
            case KID_DIVERSO -> {
                selectRaddista(this.RaddistaJwtKidDiverso);
                beenSet=true;
            }
            case PRIVATE_DIVERSO -> {
                selectRaddista(this.RaddistaJwtPrivateDiverso);
                beenSet=true;
            }
        }

        return beenSet;
    }

    @Override
    public AuthTokenRaddType getAuthTokenRaddSetted() {
        return this.IssuerTokenSetted;
    }
}

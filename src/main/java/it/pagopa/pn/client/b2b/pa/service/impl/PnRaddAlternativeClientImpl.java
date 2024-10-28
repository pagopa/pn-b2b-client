package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.CreateRegistryRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.CreateRegistryResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.RegistriesResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.UpdateRegistryRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.VerifyRequestResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.api.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api_AnagraficaCsv.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api_AnagraficaCRUD.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddAlternativeClientImpl implements IPnRaddAlternativeClient {
    private final String raddista1;
    private final String raddista2;
    private final String raddistaNonCensito;
    private final String raddistaDatiErrati;
    private final String raddistaJwtScaduto;
    private final String raddistaAudErrato;
    private final String raddistaJwtKidDiverso;
    private final String raddistaJwtPrivateDiverso;
    private final String raddistaJwksOver50Kb;
    private final String raddista3;
    private final AuthTokenRaddType issuerTokenSetted;
    private final ActOperationsApi actOperationsApi;
    private final AorOperationsApi aorOperationsApi;
    private final DocumentOperationsApi documentOperationsApi;
    private final NotificationInquiryApi notificationInquiryApi;
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final ImportApi apiCaricamentoCsv;
    private final RegistryApi apiAnagraficaCRUD;

    public PnRaddAlternativeClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.radd.alt.external.base-url}") String basePath,
                                       @Value("${pn.external.bearer-token-radd-1}") String raddista1,
                                       @Value("${pn.external.bearer-token-radd-2}") String raddista2,
                                       @Value("${pn.external.bearer-token-radd-non-censito}") String raddistaNonCensito,
                                       @Value("${pn.external.bearer-token-radd-dati-errati}") String raddistaDatiErrati,
                                       @Value("${pn.external.bearer-token-radd-3}") String raddista3,
                                       @Value("${pn.external.bearer-token-radd-jwt-scaduto}") String raddistaJwtScaduto,
                                       @Value("${pn.external.bearer-token-radd-aud-erratto}") String raddistaAudErrato,
                                       @Value("${pn.external.bearer-token-radd-kid-diverso}") String raddistaJwtKidDiverso,
                                       @Value("${pn.external.bearer-token-radd-privateKey-diverso}") String raddistaJwtPrivateDiverso,
                                       @Value("${pn.external.bearer-token-radd-over-50KB}") String raddistaJwksOver50Kb
                                       ) {
        this.raddista1 = raddista1;
        this.raddista2 = raddista2;
        this.raddistaNonCensito = raddistaNonCensito;
        this.raddistaDatiErrati = raddistaDatiErrati;
        this.raddistaJwtScaduto = raddistaJwtScaduto;
        this.raddistaAudErrato = raddistaAudErrato;
        this.raddistaJwtKidDiverso = raddistaJwtKidDiverso;
        this.raddistaJwtPrivateDiverso = raddistaJwtPrivateDiverso;
        this.raddistaJwksOver50Kb = raddistaJwksOver50Kb;
        this.raddista3 = raddista3;
        this.actOperationsApi = new ActOperationsApi(newApiClientExternal(restTemplate,basePath, raddista1));
        this.aorOperationsApi = new AorOperationsApi(newApiClientExternal(restTemplate,basePath, raddista1));
        this.documentOperationsApi = new DocumentOperationsApi(newApiClientExternal(restTemplate,basePath, raddista1));
        this.notificationInquiryApi = new NotificationInquiryApi(newApiClient(restTemplate,basePath));
        this.apiCaricamentoCsv = new ImportApi(newApiClientExternal(restTemplate,basePath, raddista1));
        this.apiAnagraficaCRUD = new RegistryApi(newApiClientExternal(restTemplate,basePath, raddista1));
        this.issuerTokenSetted = AuthTokenRaddType.ISSUER_1;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClientExternal(RestTemplate restTemplate, String basePath,String token ) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClient = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + token);
        return newApiClient;
    }

    public void selectRaddista(String token){
        this.actOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.aorOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.documentOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.apiCaricamentoCsv.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.apiAnagraficaCRUD.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.actOperationsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + token);
        this.aorOperationsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + token);
        this.documentOperationsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + token);
    }

    public void selectRaddistaHeaderErrato(String token){
        this.actOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer: " + token);
        this.aorOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer: " + token);
        this.documentOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer: " + token);
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
    public RequestResponse retrieveRequestItems(String uid, String requestId, Integer limit, String lastKey) throws RestClientException {
        return this.apiCaricamentoCsv.retrieveRequestItems(uid, requestId, limit, lastKey);
    }

    @Override
    public RegistryUploadResponse uploadRegistryRequests(String uid, RegistryUploadRequest registryUploadRequest) throws RestClientException {
        return this.apiCaricamentoCsv.uploadRegistryRequests(uid, registryUploadRequest);
    }

    @Override
    public VerifyRequestResponse verifyRequest(String uid, String requestId) throws RestClientException {
        return this.apiCaricamentoCsv.verifyRequest(uid, requestId);
    }

    @Override
    public CreateRegistryResponse addRegistry(String uid, CreateRegistryRequest createRegistryRequest) throws RestClientException {
        return this.apiAnagraficaCRUD.addRegistry(uid, createRegistryRequest);
    }

    @Override
    public void deleteRegistry(String uid, String registryId, String endDate) throws RestClientException {
        this.apiAnagraficaCRUD.deleteRegistry(uid, registryId, endDate);
    }

    @Override
    public RegistriesResponse retrieveRegistries(String uid, Integer limit, String lastKey, String cap, String city, String pr, String externalCode) throws RestClientException {
        return this.apiAnagraficaCRUD.retrieveRegistries(uid, limit, lastKey, cap, city, pr, externalCode);
    }

    @Override
    public void updateRegistry(String uid, String registryId, UpdateRegistryRequest updateRegistryRequest) throws RestClientException {
        this.apiAnagraficaCRUD.updateRegistry(uid, registryId, updateRegistryRequest);
    }


    @Override
    public boolean setAuthTokenRadd(AuthTokenRaddType issuerToken) {
        boolean beenSet = false;
        switch (issuerToken){
            case ISSUER_1 -> {
                selectRaddista(this.raddista1);
                beenSet=true;
            }
            case ISSUER_2 -> {
                selectRaddista(this.raddista2);
                beenSet=true;
            }
            case ISSUER_3 -> {
                selectRaddista(this.raddista3);
                beenSet=true;
            }
            case ISSUER_NON_CENSITO -> {
                selectRaddista(this.raddistaNonCensito);
                beenSet=true;
            }
            case DATI_ERRATI -> {
                selectRaddista(this.raddistaDatiErrati);
                beenSet=true;
            }
            case ISSUER_SCADUTO -> {
                selectRaddista(this.raddistaJwtScaduto);
                beenSet=true;
            }
            case AUD_ERRATA -> {
                selectRaddista(this.raddistaAudErrato);
                beenSet=true;
            }
            case KID_DIVERSO -> {
                selectRaddista(this.raddistaJwtKidDiverso);
                beenSet=true;
            }
            case PRIVATE_DIVERSO -> {
                selectRaddista(this.raddistaJwtPrivateDiverso);
                beenSet=true;
            }
            case HEADER_ERRATO -> {
                selectRaddistaHeaderErrato(this.raddista1);
                beenSet=true;
            }
            case OVER_50KB -> {
                selectRaddista(this.raddistaJwksOver50Kb);
                beenSet=true;
            }
        }
        return beenSet;
    }

    @Override
    public AuthTokenRaddType getAuthTokenRaddSetted() {
        return this.issuerTokenSetted;
    }
}
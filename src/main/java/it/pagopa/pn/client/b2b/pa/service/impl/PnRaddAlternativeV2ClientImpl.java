package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeV2Client;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api_AnagraficaCRUD_V2.RegistryApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.CreateRegistryRequestV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.GetRegistryResponseV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.RegistryV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.UpdateRegistryRequestV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddAlternativeV2ClientImpl implements IPnRaddAlternativeV2Client {
//    private final String raddista1;
//    private final String raddista2;
//    private final String raddistaNonCensito;
//    private final String raddistaDatiErrati;
//    private final String raddistaJwtScaduto;
//    private final String raddistaAudErrato;
//    private final String raddistaJwtKidDiverso;
//    private final String raddistaJwtPrivateDiverso;
//    private final String raddistaJwksOver50Kb;
//    private final String raddista3;
//    private final AuthTokenRaddType issuerTokenSetted;
//    private final ActOperationsApi actOperationsApi;
//    private final AorOperationsApi aorOperationsApi;
//    private final DocumentOperationsApi documentOperationsApi;
//    private final NotificationInquiryApi notificationInquiryApi;
    private static final String AUTHORIZATION = "Authorization";
   private static final String BEARER = "Bearer ";
//    private final ImportApi apiCaricamentoCsv;
    private final RegistryApi apiAnagraficaCRUDV2;


    private String tokenCognito;

    public PnRaddAlternativeV2ClientImpl(RestTemplate restTemplate,
                                         @Value("${pn.radd.alt.external.base-url}") String basePath
//                                         @Value("${pn.external.bearer-token-radd-1}") String raddista1
//                                         @Value("${pn.external.bearer-token-radd-2}") String raddista2,
//                                         @Value("${pn.external.bearer-token-radd-non-censito}") String raddistaNonCensito,
//                                         @Value("${pn.external.bearer-token-radd-dati-errati}") String raddistaDatiErrati,
//                                         @Value("${pn.external.bearer-token-radd-3}") String raddista3,
//                                         @Value("${pn.external.bearer-token-radd-jwt-scaduto}") String raddistaJwtScaduto,
//                                         @Value("${pn.external.bearer-token-radd-aud-erratto}") String raddistaAudErrato,
//                                         @Value("${pn.external.bearer-token-radd-kid-diverso}") String raddistaJwtKidDiverso,
//                                         @Value("${pn.external.bearer-token-radd-privateKey-diverso}") String raddistaJwtPrivateDiverso,
//                                         @Value("${pn.external.bearer-token-radd-over-50KB}") String raddistaJwksOver50Kb
                                       ) {

//        this.raddista1 = raddista1;
//        this.raddista2 = raddista2;
//        this.raddistaNonCensito = raddistaNonCensito;
//        this.raddistaDatiErrati = raddistaDatiErrati;
//        this.raddistaJwtScaduto = raddistaJwtScaduto;
//        this.raddistaAudErrato = raddistaAudErrato;
//        this.raddistaJwtKidDiverso = raddistaJwtKidDiverso;
//        this.raddistaJwtPrivateDiverso = raddistaJwtPrivateDiverso;
//        this.raddistaJwksOver50Kb = raddistaJwksOver50Kb;
//        this.raddista3 = raddista3;
//        this.actOperationsApi = new ActOperationsApi(newApiClientExternal(restTemplate,basePath, raddista1));
//        this.aorOperationsApi = new AorOperationsApi(newApiClientExternal(restTemplate,basePath, raddista1));
//        this.documentOperationsApi = new DocumentOperationsApi(newApiClientExternal(restTemplate,basePath, raddista1));
//        this.notificationInquiryApi = new NotificationInquiryApi(newApiClient(restTemplate,basePath));
//        this.apiCaricamentoCsv = new ImportApi(newApiClientExternal(restTemplate,basePath, raddista1));
        this.apiAnagraficaCRUDV2 = new RegistryApi(newApiClientExternal(restTemplate,basePath, null));
//        this.issuerTokenSetted = AuthTokenRaddType.ISSUER_1;
    }




    //todo t radd

    @Override
    public void deleteRegistry(String partnerId, String locationId) throws RestClientException {
        this.apiAnagraficaCRUDV2.deleteRegistry( partnerId, locationId);
    }

    @Override
    public GetRegistryResponseV2 retrieveRegistries(String xPagopaPnCxId, Integer limit, String lastKey) throws RestClientException {
        return this.apiAnagraficaCRUDV2.retrieveRegistries(xPagopaPnCxId, limit, lastKey);
    }

    @Override
    public RegistryV2 updateRegistry(String partnerId, String locationId, UpdateRegistryRequestV2 updateRegistryRequestV2) throws RestClientException {
        return this.apiAnagraficaCRUDV2.updateRegistry(partnerId, locationId, updateRegistryRequestV2);
    }

    @Override
    public ResponseEntity<Void> deleteRegistryWithHttpInfo(String partnerId, String locationId) {
        return this.apiAnagraficaCRUDV2.deleteRegistryWithHttpInfo(partnerId, locationId );
    }


//    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
//        ApiClient newApiClient = new ApiClient( restTemplate );
//        newApiClient.setBasePath( basePath );
//        return newApiClient;
//    }

    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClientExternal(RestTemplate restTemplate, String basePath,String token ) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClient = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + token);
        return newApiClient;
    }

    public void selectRaddista(String token){
//        this.actOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
//        this.aorOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
//        this.documentOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
//        this.apiCaricamentoCsv.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
        this.apiAnagraficaCRUDV2.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);
//        this.actOperationsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + token);
//        this.aorOperationsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + token);
//        this.documentOperationsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + token);
    }

//    public void selectRaddistaHeaderErrato(String token){
//        this.actOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer: " + token);
//        this.aorOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer: " + token);
//        this.documentOperationsApi.getApiClient().addDefaultHeader("Authorization", "Bearer: " + token);
//    }
//
//    public ActInquiryResponse actInquiry( String uid, String recipientTaxId, String recipientType, String qrCode, String iun) throws RestClientException {
//        return this.actOperationsApi.actInquiryWithHttpInfo(uid, recipientTaxId, recipientType, qrCode, iun).getBody();
//    }
//
//    public AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
//        return this.actOperationsApi.abortActTransactionWithHttpInfo(uid, abortTransactionRequest).getBody();
//    }
//
//    public CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
//        return this.actOperationsApi.completeActTransactionWithHttpInfo(uid, completeTransactionRequest).getBody();
//    }
//
//    public StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest actStartTransactionRequest) throws RestClientException {
//        return this.actOperationsApi.startActTransactionWithHttpInfo(uid, actStartTransactionRequest).getBody();
//    }
//
//    public AORInquiryResponse aorInquiry( String uid, String recipientTaxId, String recipientType) throws RestClientException {
//        return this.aorOperationsApi.aorInquiryWithHttpInfo( uid, recipientTaxId, recipientType).getBody();
//    }
//
//    public AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
//        return this.aorOperationsApi.abortAorTransactionWithHttpInfo(uid, abortTransactionRequest).getBody();
//    }
//
//    public CompleteTransactionResponse completeAorTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
//        return this.aorOperationsApi.completeAorTransactionWithHttpInfo(uid, completeTransactionRequest).getBody();
//    }
//
//    public StartTransactionResponse startAorTransaction(String uid, AorStartTransactionRequest aorStartTransactionRequest) throws RestClientException {
//        return this.aorOperationsApi.startAorTransactionWithHttpInfo(uid, aorStartTransactionRequest).getBody();
//    }
//
//    public DocumentUploadResponse documentUpload( String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException {
//        return this.documentOperationsApi.documentUploadWithHttpInfo( uid, documentUploadRequest).getBody();
//    }
//
//    public OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
//        return this.notificationInquiryApi.getActPracticesByInternalIdWithHttpInfo(internalId, filterRequest).getBody();
//    }
//
//    public OperationsResponse getActPracticesByIun(String iun) throws RestClientException {
//        return this.notificationInquiryApi.getActPracticesByIunWithHttpInfo(iun).getBody();
//    }
//
//    public OperationActResponse getActTransactionByOperationId(String transactionId) throws RestClientException {
//        return this.notificationInquiryApi.getActTransactionByTransactionIdWithHttpInfo(transactionId).getBody();
//    }
//
//    public OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
//        return this.notificationInquiryApi.getAorPracticesByInternalIdWithHttpInfo(internalId, filterRequest).getBody();
//    }
//
//    public OperationsResponse getAorPracticesByIun(String iun) throws RestClientException {
//        return this.notificationInquiryApi.getAorPracticesByIunWithHttpInfo(iun).getBody();
//    }
//
//    public OperationAorResponse getAorTransactionByOperationId(String transactionId) throws RestClientException {
//        return this.notificationInquiryApi.getAorTransactionByTransactionIdWithHttpInfo(transactionId).getBody();
//    }
//
//    @Override
//    public byte[] documentDownload(String operationType, String operationId, String attchamentId) throws RestClientException {
//        return this.documentOperationsApi.documentDownload(operationType,operationId, attchamentId);
//    }
//
//    @Override
//    public RegistryUploadResponse uploadRegistryRequests(String uid, RegistryUploadRequest registryUploadRequest) throws RestClientException {
//        return this.apiCaricamentoCsv.uploadRegistryRequests(uid, registryUploadRequest);
//    }
//
//    @Override
//    public VerifyRequestResponse verifyRequest(String uid, String requestId) throws RestClientException {
//        return this.apiCaricamentoCsv.verifyRequest(uid, requestId);
//    }
//
    @Override
    public RegistryV2 addRegistry(String partnerId, CreateRegistryRequestV2 createRegistryRequestV2) throws RestClientException {
        return this.apiAnagraficaCRUDV2.addRegistry(partnerId ,createRegistryRequestV2);
    }
//
////    @Override
////    public RegistriesResponse retrieveRegistries(String uid, Integer limit, String lastKey, String cap, String city, String pr, String externalCode) throws RestClientException {
////        return this.apiAnagraficaCRUDV2.retrieveRegistries(uid, limit, lastKey, cap, city, pr, externalCode);
////    }
//
//
//    @Override
//    public boolean setAuthTokenRadd(AuthTokenRaddType issuerToken) {
//        boolean beenSet = false;
//        switch (issuerToken){
//            case ISSUER_1 -> {
//                selectRaddista(this.raddista1);
//                beenSet=true;
//            }
//            case ISSUER_2 -> {
//                selectRaddista(this.raddista2);
//                beenSet=true;
//            }
//            case ISSUER_3 -> {
//                selectRaddista(this.raddista3);
//                beenSet=true;
//            }
//            case ISSUER_NON_CENSITO -> {
//                selectRaddista(this.raddistaNonCensito);
//                beenSet=true;
//            }
//            case DATI_ERRATI -> {
//                selectRaddista(this.raddistaDatiErrati);
//                beenSet=true;
//            }
//            case ISSUER_SCADUTO -> {
//                selectRaddista(this.raddistaJwtScaduto);
//                beenSet=true;
//            }
//            case AUD_ERRATA -> {
//                selectRaddista(this.raddistaAudErrato);
//                beenSet=true;
//            }
//            case KID_DIVERSO -> {
//                selectRaddista(this.raddistaJwtKidDiverso);
//                beenSet=true;
//            }
//            case PRIVATE_DIVERSO -> {
//                selectRaddista(this.raddistaJwtPrivateDiverso);
//                beenSet=true;
//            }
//            case HEADER_ERRATO -> {
//                selectRaddistaHeaderErrato(this.raddista1);
//                beenSet=true;
//            }
//            case OVER_50KB -> {
//                selectRaddista(this.raddistaJwksOver50Kb);
//                beenSet=true;
//            }
//        }
//        return beenSet;
//    }

//    @Override
//    public AuthTokenRaddType getAuthTokenRaddSetted() {
//        return this.issuerTokenSetted;
//    }
}
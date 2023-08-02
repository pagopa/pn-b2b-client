package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.ResponsePaperNotificationFailedDto;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface IPnInternalAnnullamentoDeliveryPushExternalClientImpl {

    public void notificationCancellation(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, List<String> xPagopaPnCxGroups) throws RestClientException ;
    public LegalFactDownloadMetadataResponse getLegalFactById(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, String legalFactId, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException ;
    public LegalFactDownloadMetadataResponse getLegalFact(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, LegalFactCategory legalFactType, String legalFactId, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException ;
    public List<LegalFactListElement> getNotificationLegalFacts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException;

    public DocumentDownloadMetadataResponse getDocumentsWeb(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, DocumentCategory documentType, String documentId, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException;

    }

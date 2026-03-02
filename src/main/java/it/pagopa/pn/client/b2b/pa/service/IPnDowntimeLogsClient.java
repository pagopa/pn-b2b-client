package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnStatusResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnStatusUpdateEvent;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.web.client.RestClientException;


public interface IPnDowntimeLogsClient {
    PnStatusResponse currentStatus() throws RestClientException;
    LegalFactDownloadMetadataResponse getLegalFact(String legalFactId) throws RestClientException;
    PnStatusResponse status() throws RestClientException;
    PnDowntimeHistoryResponse statusHistory(OffsetDateTime fromTime, OffsetDateTime toTime, List<PnFunctionality> functionality, String page, String size) throws RestClientException;
    void addStatusChangeEvent(String xPagopaPnUid, List<PnStatusUpdateEvent> pnStatusUpdateEvent) throws RestClientException;
    PnDowntimeHistoryResponse getResolved(Integer year, Integer month) throws RestClientException;
}
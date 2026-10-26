package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/** Extends the body-only client with access to the original HTTP response metadata. */
public interface IPnDowntimeLogsHttpClient extends IPnDowntimeLogsClient {
    ResponseEntity<LegalFactDownloadMetadataResponse> getLegalFactWithHttpInfo(String legalFactId) throws RestClientException;
    ResponseEntity<PnDowntimeHistoryResponse> statusHistoryWithHttpInfo(OffsetDateTime fromTime, OffsetDateTime toTime, List<PnFunctionality> functionality, String page, String size) throws RestClientException;
    ResponseEntity<PnDowntimeHistoryResponse> getResolvedWithHttpInfo(Integer year, Integer month) throws RestClientException;
}

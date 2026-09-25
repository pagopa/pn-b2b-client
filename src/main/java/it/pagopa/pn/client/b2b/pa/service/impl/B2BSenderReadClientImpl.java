package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.api.SenderInformalReadWebApi;
import it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.model.InformalNotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.model.InformalNotificationStatusV1;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.api.SenderReadWebApi;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.CxTypeAuthFleet;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatusV26;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2BSenderReadClientImpl {
    private final SenderReadWebApi senderReadWebApi;
    private final SenderInformalReadWebApi senderInformalReadWebApi;

    public B2BSenderReadClientImpl(RestTemplate restTemplate,
                                   @Value("${pn.delivery.base-url}") String basePath) {
        this.senderReadWebApi = new SenderReadWebApi(newApiClient(restTemplate, basePath));
        this.senderInformalReadWebApi = new SenderInformalReadWebApi(newInformalApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.ApiClient newInformalApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.ApiClient newApiClient = new it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public LegalNotificationSearchResponse searchSentNotification(NotificationSearchParam searchParam) throws RestClientException {
        String cxType = resolveActual(searchParam.xPagopaPnCxType, "PA");
        NotificationStatusV26 statusV26 = searchParam.status != null ? NotificationStatusV26.fromValue(searchParam.status) : null;
        return senderReadWebApi.searchSentNotification(searchParam.xPagopaPnUid, cxType != null ? CxTypeAuthFleet.fromValue(cxType) : null, searchParam.senderId,
                searchParam.startDate, searchParam.endDate, searchParam.xPagopaPnCxGroups, searchParam.recipientId,
                statusV26, searchParam.iunMatch, searchParam.size, searchParam.nextPagesKey);
    }

    public InformalNotificationSearchResponse searchInformalSentNotification(NotificationSearchParam searchParam) throws RestClientException {
        String cxType = resolveActual(searchParam.xPagopaPnCxType, "PA");
        it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.model.CxTypeAuthFleet cxTypeAuthFleet = cxType != null ? it.pagopa.pn.client.web.generated.openapi.clients.informal.web.pa.model.CxTypeAuthFleet.fromValue(cxType) : null;
        InformalNotificationStatusV1 status = searchParam.status != null ? InformalNotificationStatusV1.fromValue(searchParam.status) : null;
        return senderInformalReadWebApi.searchInformalSentNotification(searchParam.xPagopaPnUid, cxTypeAuthFleet, searchParam.senderId,
                searchParam.campaignId, searchParam.startDate, searchParam.endDate, searchParam.xPagopaPnCxGroups, searchParam.recipientId,
                searchParam.iunMatch, status, searchParam.viewed, searchParam.delivered, searchParam.size, searchParam.nextPagesKey);
    }

    // NotificationSearchParam.RESOLVE_FROM_CALLER (default quando il campo non è specificato in tabella) -> valore derivato dal destinatario;
    // qualunque altro valore, incluso null esplicito (per simulare un campo obbligatorio mancante), passa invariato
    private static String resolveActual(String value, String actualValue) {
        return NotificationSearchParam.RESOLVE_FROM_CALLER.equals(value) ? actualValue : value;
    }


}
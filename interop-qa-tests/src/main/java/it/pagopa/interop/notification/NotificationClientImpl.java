package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
public class NotificationClientImpl extends AbstractClient implements INotificationClient {
    private static final int RESULTS_LIMIT = 30;

    private final InAppNotificationsApi notificationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public NotificationClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.notificationsApi = new InAppNotificationsApi(createApiClient("dummyBearer"));
        super.httpCallExecutor = httpCallExecutor;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public List<Notification> getByBodyLike(String like) {
        Notifications notifications = performOperation(
                () -> notificationsApi.getNotificationsWithHttpInfo(0, RESULTS_LIMIT, like, null, null)
        ).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero notifiche (response non 2xx o body nullo)"
        ));

        return notifications.getResults();
    }

    @Override
    public Optional<Notification> getByBody(String body) {
        List<Notification> results = getByBodyLike(body);

        if(results.size() > 1) throw new IllegalStateException("Trovate diverse notifiche con lo stesso body: " + body);
        return Optional.ofNullable(results.get(0));
    }

    @Override
    public List<Notification> getAll() {
        Notifications notifications = performOperation(
                () -> notificationsApi.getNotificationsWithHttpInfo(0, RESULTS_LIMIT, null, null, null)
        ).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero notifiche (response non 2xx o body nullo)"
        ));

        return notifications.getResults();
    }

    @Override
    public void deleteAll(List<UUID> ids) {
        var arg = new InlineObject10();
        arg.setIds(ids);

        performOperation(
                () -> notificationsApi.deleteNotificationsWithHttpInfo(arg)
        );
    }

    @Override
    public void delete(UUID id) {
        performOperation(
                () -> notificationsApi.deleteNotificationWithHttpInfo(id)
        );
    }

    @Override
    public void readAll(List<UUID> ids){
        var arg = new InlineObject11();
        arg.setIds(ids);

        performOperation(
                () -> notificationsApi.markNotificationsAsReadWithHttpInfo(arg)
        );
    }

    @Override
    public void read(UUID id) {
        performOperation(
                () -> notificationsApi.markNotificationAsReadWithHttpInfo(id)
        );
    }

    @Override
    public void unreadAll(List<UUID> ids){
        var arg = new InlineObject12();
        arg.setIds(ids);

        performOperation(
                () -> notificationsApi.markNotificationsAsUnreadWithHttpInfo(arg)
        );
    }

    @Override
    public void unread(UUID id) {
        performOperation(
                () -> notificationsApi.markNotificationAsUnreadWithHttpInfo(id)
        );
    }

    @Override
    public NotificationsCountBySection countBySection(){
        return performOperation(
                notificationsApi::getNotificationsCountBySectionWithHttpInfo
        ).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero count notifiche (response non 2xx o body nullo)"
        ));
    }

    @Override
    public boolean existsAll(List<Notification> expected) {
        return missingIds(expected).isEmpty();
    }

    @Override
    public Set<Notification> missingIds(List<Notification> expected) {
        Set<String> expectedBodies = expected.stream()
                .filter(Objects::nonNull)
                .map(Notification::getBody)
                .collect(java.util.stream.Collectors.toSet());

        if (expectedBodies.isEmpty())
            throw new IllegalArgumentException("La lista expected non contiene body validi da verificare");

        Set<Notification> remaining = new HashSet<>(expected);
        for(String body : expectedBodies) getByBody(body).ifPresent(remaining::remove);

        return remaining; // se vuoto => trovate tutte
    }

    @Override
    public UUID generateId(EntityIdType entityIdType) {
        return switch (entityIdType){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            case VALID_ID -> UUID.randomUUID();
            case NULL_ID -> null;
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.notificationsApi.setApiClient(createApiClient(bearerToken));
    }

}

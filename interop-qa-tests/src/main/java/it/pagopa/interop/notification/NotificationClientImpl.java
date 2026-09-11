package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeleteNotificationsRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notifications;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationsCountBySection;
import it.pagopa.interop.notification.cache.NotificationCache;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Predicate;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotificationClientImpl extends AbstractClient implements INotificationClient {
    private static final int RESULTS_LIMIT = 30;

    private final InAppNotificationsApi notificationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final NotificationCache cache;

    public NotificationClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, NotificationCache cache, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.notificationsApi = new InAppNotificationsApi(createApiClient("dummyBearer"));
        this.cache = cache;
        super.httpCallExecutor = httpCallExecutor;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public Notification get(UUID id) {
        Optional<Notification> cachedNotification = this.cache.get(id);
        return cachedNotification.orElseGet(
            () -> this.get(notif -> notif.getId().equals(id)).orElse(null));
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
        if (results == null || results.isEmpty()) return Optional.empty();
        if (results.size() > 1) throw new IllegalStateException("Trovate diverse notifiche con lo stesso body: " + body);
        return Optional.ofNullable(results.get(0));
    }

    @Override
    public List<Notification> getAll() {
        return this.getAll(0, RESULTS_LIMIT);
    }

    @Override
    public List<Notification> getPage(int page, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID getId(Notification entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Notification> getAll(int offset, int limit) {
        Notifications notifications = performOperation(() ->
            this.notificationsApi.getNotificationsWithHttpInfo(
                offset,
                limit,
                null,
                null,
                null)
        ).orElseThrow(() -> new IllegalStateException(
            "Errore nel recupero configurazione notifiche per tenant (response non 2xx o body nullo)"
        ));
        return notifications.getResults();
    }

    @Override
    public void deleteAll(List<UUID> ids) {
        var arg = new DeleteNotificationsRequest();
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
        var arg = new DeleteNotificationsRequest();
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
        var arg = new DeleteNotificationsRequest();
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
            case INVALID_ID -> UUID.fromString("12345-not-a-valid-uuid"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            case VALID_ID -> UUID.randomUUID();
            case NULL_ID -> null;
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }

    @Override
    public Optional<Notification> get(Predicate<Notification> notificationCatcher) {
        Optional<Notification> cachedNotification = this.cache.find(notificationCatcher);
        if(cachedNotification.isPresent()) {
           return cachedNotification;
        }

        List<Notification> actualNotificationsRead = new ArrayList<>();
        Notifications notifications = new Notifications();
        int i = 0;

        // condizione d'uscita: aver trovato la notifica OPPURE non aver altre notifiche da leggere
        for(; notifications.getResults().stream().noneMatch(notificationCatcher) && !(notifications.getResults().isEmpty() && i != 0); i++) {
            notifications = this.notificationsApi.getNotifications(
                i * RESULTS_LIMIT,
                RESULTS_LIMIT,
                null,
                null,
                null);
            actualNotificationsRead.addAll(notifications.getResults());
        }
        this.cache.putAll(actualNotificationsRead);

        return notifications.getResults().stream().filter(notificationCatcher).findFirst();
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.notificationsApi.setApiClient(createApiClient(bearerToken));
    }

}

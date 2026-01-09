package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject10;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject11;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notifications;
import it.pagopa.interop.notification.cache.NotificationCache;

import java.util.*;
import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public NotificationClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, NotificationCache cache) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.notificationsApi = new InAppNotificationsApi(createApiClient("dummyBearer"));
        this.cache = cache;
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

    public Notification getByIdNoCache(UUID id) {
        Notifications notifications = performOperation(
                () -> notificationsApi.getNotificationsWithHttpInfo(0, RESULTS_LIMIT, id.toString(), null, null)
        ).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero notifiche (response non 2xx o body nullo)"
        ));

        List<Notification> results = notifications.getResults();
        if(results.size() > 1) throw new IllegalStateException("Trovate diverse notifiche con lo stesso id: " + id);
        return notifications.getResults().get(0);
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

        //TODO: eliminare le notifiche cancellata da dentro la cache
    }

    @Override
    public void readAll(List<UUID> ids){
        var arg = new InlineObject11();
        arg.setIds(ids);

        performOperation(
                () -> notificationsApi.markNotificationsAsReadWithHttpInfo(arg)
        );

        //TODO: eliminare le notifiche lette da dentro la cache
    }

    @Override
    public UUID getId(Notification entity) {
        return entity.getId();
    }

    @Override
    public boolean existsAll(List<Notification> expected) {
        return missingIds(expected).isEmpty();
    }

    @Override
    public Set<UUID> missingIds(List<Notification> expected) {
        Set<UUID> expectedIds = expected.stream()
                .filter(Objects::nonNull)
                .map(Notification::getId)
                .collect(java.util.stream.Collectors.toSet());

        // se non ho id su cui verificare, non posso fare matching sensato
        if (expectedIds.isEmpty()) {
            throw new IllegalArgumentException("La lista expected non contiene id validi da verificare");
        }

        Set<UUID> remaining = new HashSet<>(expectedIds);

        for (int page = 0; !remaining.isEmpty(); page++) {
            final int offset = page * RESULTS_LIMIT;

            Notifications notifications = performOperation(
                    () -> notificationsApi.getNotificationsWithHttpInfo(offset, RESULTS_LIMIT, null, null, null)
            ).orElseThrow(() -> new IllegalStateException(
                    "Errore nel recupero notifiche (response non 2xx o body nullo)"
            ));

            List<Notification> results = Optional.ofNullable(notifications.getResults()).orElseGet(List::of);
            if (results.isEmpty()) { // fine pagine
                break;
            }

            for (Notification n : results) {
                UUID id = n != null ? n.getId() : null;
                if (id != null) {
                    remaining.remove(id);
                    if (remaining.isEmpty()) break;
                }
            }
        }

        return remaining; // se vuoto => trovate tutte
    }

    @Override
    public UUID generateId(EntityIdType entityIdType) {
        return switch (entityIdType){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            case VALID_ID -> UUID.randomUUID();
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }

    @Override
    public Optional<Notification> get(Predicate<Notification> notificationCatcher) {
        Optional<Notification> cached = this.cache.find(notificationCatcher);
        if (cached.isPresent()) return cached;

        List<Notification> read = new ArrayList<>();

        for (int page = 0; ; page++) {
            final int offset = page * RESULTS_LIMIT;

            Notifications notifications = performOperation(
                    () -> notificationsApi.getNotificationsWithHttpInfo(offset, RESULTS_LIMIT, null, null, null)
            ).orElseThrow(() -> new IllegalStateException(
                    "Errore nel recupero notifiche (response non 2xx o body nullo)"
            ));

            List<Notification> results = Optional.of(notifications.getResults()).orElseGet(List::of);
            if (results.isEmpty()) break;

            read.addAll(results);

            Optional<Notification> found = results.stream().filter(notificationCatcher).findFirst();
            if (found.isPresent()) {
                this.cache.putAll(read);
                return found;
            }
        }

        this.cache.putAll(read);
        return Optional.empty();
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.notificationsApi.setApiClient(createApiClient(bearerToken));
    }

}

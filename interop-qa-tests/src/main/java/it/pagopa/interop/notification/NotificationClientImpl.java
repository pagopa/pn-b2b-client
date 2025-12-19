package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notifications;
import it.pagopa.interop.notification.cache.NotificationCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public List<Notification> getAll() {
        return this.notificationsApi.getNotifications(
            0,
            RESULTS_LIMIT,
            null,
            null,
            null).getResults();
    }

    @Override
    public UUID getId(Notification entity) {
        return entity.getId();
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
        Optional<Notification> cachedNotification = this.cache.find(notificationCatcher);
        if(cachedNotification.isPresent()) {
           return cachedNotification;
        }

        List<Notification> actualNotificationsRead = new ArrayList<>();
        Notifications notifications = new Notifications();
        int i = 0;

        // condizione d'uscita: aver trovato la notifica con id in input OPPURE non aver altre notifiche da leggere
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

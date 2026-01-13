package it.pagopa.pn.interop.cucumber.steps.notification;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.AssertCheckType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class NotificationSteps extends AbstractCommonSteps<Notification, UUID> {
    private final NotificationClientImpl notificationClient;
    private final NotificationConfigClient notificationConfigClient;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final FeatureLifecycleManager notificationTestsManager;
    private final ConfigFileReader configFileReader;

    public NotificationSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator,
        ConfigFileReader configFileReader,
        @Qualifier("notificationFeatureLifecycleManager") FeatureLifecycleManager notificationTestsManager
    ) {
        super("inAppNotification", clientTokenConfigurator.getNotificationClient(), sharedStepsContext);
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.configFileReader = configFileReader;

        // necessario ricorrere all'impl. concreta per usare il suo HttpCallExecutor
        this.notificationClient = (NotificationClientImpl) clientTokenConfigurator.getNotificationClient();
        this.notificationConfigClient = (NotificationConfigClient) clientTokenConfigurator.getNotificationConfigClient();
        this.notificationTestsManager = notificationTestsManager;
    }

    @When("l'utente tenta di {word} le notifiche recuperate")
    public void readOrDeleteNotifications(String op){
        switch(op){
            case "eliminare" -> {
                //TODO: inserisco in unexpected le notifiche che saranno eliminate
            }
            case "leggere" -> {}
            default -> {}
        }
    }

    @When("l'utente tenta di {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    public void readOrDeleteNotifications(String op, EntityIdType entityIdType){
        UUID invalidOrNotExistentId = super.generateId(entityIdType);
        UUID validId = super.generateId(null);

        List<UUID> ids = new ArrayList<>();
        ids.add(invalidOrNotExistentId);
        ids.add(validId);

        switch(op){
            case "eliminare" -> {
                //TODO: inserisco in unexpected le notifiche che saranno eliminate
            }
            case "leggere" -> {}
            default -> {}
        }
    }

    @When("l'utente tenta di {word} la notifica recuperata specificando un id {entityIdType}")
    public void readOrDeleteNotification(String op, EntityIdType entityIdType){
        UUID invalidOrNotExistentId = super.generateId(entityIdType);
        UUID validId = super.generateId(null);

        switch(op){
            case "eliminare" -> {
                //TODO: inserisco in unexpected le notifiche che saranno eliminate
                }
            case "leggere" -> {}
            default -> {}
        }
    }

    @When("l'utente tenta di recuperare la lista di notifiche create")
    @When("nessuna notifica è stata eliminata")
    public void pollUntil(){
        //TODO: implementare metodo bind e equals
        //TODO: dovrei rendere piu efficiente la ricerca usando gli id se disponibili
        super.getAllUntil(() -> null, AssertCheckType.PRESENT_AND_MATCHING);
    }

    @When("le notifiche create sono state eliminate")
    public void checkDelete(){
        // TODO: devo assegnare le notifiche create a unexpected
        super.unexpectedEntities.clear();
        super.getAllUntil(() -> null, AssertCheckType.EXPECTED_NOT_PRESENT);
    }

    @When("le notifiche recuperate sono nello stato {word}")
    public void checkRead(String readState){
        boolean read = readState.equals("read");

        if(read){
            // TODO: createdAt dovrebbe essere un timestamp
        } else {
            // TODO: createdAt dovrebbe essere null
        }
    }

    @Override
    public void bindActual(SharedStepsContext context, List<Notification> actualEntities) {

    }

    @Override
    public List<Notification> bindExpected(SharedStepsContext context) {
      throw new RuntimeException("Not implemented yet");
    }

    @Override
    protected boolean isEqual(Notification a, Notification b) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<Notification> bindUnexpected(SharedStepsContext context) {
        throw new RuntimeException("Not implemented yet");
    }

    @Then("per l'utente {string} di {string} è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern {string} e {string}")
    public void checkInAppNotificationBody(String role, String tenant, String bodyRegex, String deepLinkRegex){
        clientTokenConfigurator.setBearerToken(
            getContext().getIdentityService().getToken(tenant, role));

        PollingService pollingService = getContext().getPollingService();

        List<Notification> notifications = pollingService.makePolling(
            this.notificationClient::getAll,
            list -> !IterableUtils.isEmpty(list),
            "Non è stata restituita alcuna notifica");

        assertThat(notifications)
            .as("Verifica che almeno una notifica soddisfi i pattern di body e deepLink")
            .anySatisfy(notif -> {
                assertThat(notif.getBody()).matches(bodyRegex);
                assertThat(notif.getDeepLink()).matches(deepLinkRegex);
            });

        clientTokenConfigurator.setBearerToken(this.getContext().getUserToken());
    }

}

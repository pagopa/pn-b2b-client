package it.pagopa.pn.interop.cucumber.steps.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.enums.AssertCheckType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import it.pagopa.pn.interop.cucumber.utility.functionalint.Task;
import it.pagopa.pn.interop.cucumber.utility.functionalint.ThrowingConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

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

    @Then("è presente una notifica in-app contenente il seguente messaggio: {string}")
    public void checkInAppNotificationBody_old(String body){
        // TODO
    }

    // FIXME 09/01/2026 alternativa alla strategia checkInAppNotificationBody_old, sceglierne una
    @Then("per {string} è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern {string} e {string}")
    public void checkInAppNotificationBody(String tenant, String bodyRegex, String deepLinkRegex){
        clientTokenConfigurator.setBearerToken(getContext().getIdentityService().getToken(tenant, null));

        // TODO 09/01/2026 momentaneo, ripiegare sul pollingService, eventualmente adattato alle esigenze del test
        //getContext().getDelayService().delayFor(Duration.ofMinutes(10));

        PollingService pollingService = getContext().getPollingService();

        /* IMPL. BASATA SU CACHE MOMENTANEAMENTE ACCANTONATA nell'ipotesi che funzioni il filtering
        * lato server attraverso il parametro 'q' */
            Predicate<Notification> bodyMatcher = notification -> notification.getBody().matches(bodyRegex);

            /* TODO al momento sconosciute le tempistiche per la generazione delle notifiche, potrebbe
             *  non bastare */
        Optional<Notification> notification = pollingService.makePolling(
            () -> this.notificationClient.get(bodyMatcher),
            Optional::isPresent,
            "Non presente alcuna notifica che corrisponda alla regex fornita");
        Notification notif = notification.get();

        assertSoftly(softly -> {
            softly.assertThat(notif.getBody())
                .as("Verifica corpo della notifica")
                .matches(bodyRegex);
            softly.assertThat(notif.getDeepLink())
                .as("Verifica deepLink della notifica")
                .matches(deepLinkRegex);
        });
    }

    @Before("@bff-notification")
    public void switchOnInAppNotification() throws Exception {
        TenantNotificationConfigUpdateSeed tenantConfig = new TenantNotificationConfigUpdateSeed()
            .enabled(true);

        UserNotificationConfigUpdateSeed userConfig = new UserNotificationConfigUpdateSeed()
            .emailNotificationPreference(false)
            .inAppNotificationPreference(true)
            .emailDigestPreference(true)
            .emailConfig(new NotificationConfig()
                // Producer
                .agreementManagementToProducer(false)
                .agreementSuspendedUnsuspendedToProducer(false)
                .clientAddedRemovedToProducer(false)
                .purposeStatusChangedToProducer(false)
                .templateStatusChangedToProducer(false)
                .purposeQuotaAdjustmentRequestToProducer(false)

                // Consumer
                .agreementSuspendedUnsuspendedToConsumer(false)
                .eserviceStateChangedToConsumer(false)
                .agreementActivatedRejectedToConsumer(false)
                .purposeActivatedRejectedToConsumer(false)
                .purposeSuspendedUnsuspendedToConsumer(false)
                .purposeOverQuotaStateToConsumer(false)

                // Instantiator
                .newEserviceTemplateVersionToInstantiator(false)
                .eserviceTemplateNameChangedToInstantiator(false)
                .eserviceTemplateStatusChangedToInstantiator(false)

                // Delegator/Delegate
                .delegationApprovedRejectedToDelegator(false)
                .eserviceNewVersionSubmittedToDelegator(false)
                .eserviceNewVersionApprovedRejectedToDelegate(false)
                .delegationSubmittedRevokedToDelegate(false)

                // Altri
                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
            )
            .inAppConfig(new NotificationConfig()
                // Producer
                .agreementManagementToProducer(true)
                .agreementSuspendedUnsuspendedToProducer(true)
                .clientAddedRemovedToProducer(true)
                .purposeStatusChangedToProducer(true)
                .templateStatusChangedToProducer(true)
                .purposeQuotaAdjustmentRequestToProducer(true)

                // Consumer
                .agreementSuspendedUnsuspendedToConsumer(true)
                .eserviceStateChangedToConsumer(true)
                .agreementActivatedRejectedToConsumer(true)
                .purposeActivatedRejectedToConsumer(true)
                .purposeSuspendedUnsuspendedToConsumer(true)
                .purposeOverQuotaStateToConsumer(true)

                // Instantiator
                .newEserviceTemplateVersionToInstantiator(true)
                .eserviceTemplateNameChangedToInstantiator(true)
                .eserviceTemplateStatusChangedToInstantiator(true)

                // Delegator/Delegate
                .delegationApprovedRejectedToDelegator(true)
                .eserviceNewVersionSubmittedToDelegator(true)
                .eserviceNewVersionApprovedRejectedToDelegate(true)
                .delegationSubmittedRevokedToDelegate(true)

                // Altri
                .certifiedVerifiedAttributeAssignedRevokedToAssignee(true)
                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(true)
            );

        this.configNotificationTests(tenantConfig, userConfig, this.notificationTestsManager::before);
    }

    @Before("@bff-notification")
    public void deleteAllNotifications() throws Exception {
        PollingService pollingService = this.getContext().getPollingService();
        IHttpExecutor notificationExecutor = this.notificationClient.getHttpCallExecutor();
        IHttpExecutor executor = this.getContext().getHttpCallExecutor();
        applyTaskForEveryUser(() -> {
            List<Notification> notifications = pollingService.makePolling(
                this.notificationClient::getAll,
                res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
                "Reperimento notifiche fallito");

            while(!notifications.isEmpty()) {
                List<UUID> notificationsIds = notifications.stream().map(Notification::getId).toList();

                /* TODO 12/01/2026 per bypassare nel breve termine una problematica di sviluppo sono
                  * utilizzati due executors distinti. Correggere usandone uno solo appena possibile. */
                pollingService.makePolling(
                    () -> executor.performCall(() -> this.notificationClient.deleteAll(notificationsIds)),
                    HttpStatus::is2xxSuccessful,
                    "Eliminazione notifiche fallita");
                notifications = pollingService.makePolling(
                    this.notificationClient::getAll,
                    res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
                    "Reperimento notifiche fallito");
            }
        });
    }

    @After("@bff-notification")
    public void switchOffInAppNotification() throws Exception {
        UserNotificationConfigUpdateSeed userConfig = new UserNotificationConfigUpdateSeed()
            .emailNotificationPreference(false)
            .inAppNotificationPreference(false)
            .emailDigestPreference(false)
            .emailConfig(new NotificationConfig()
                // Producer
                .agreementManagementToProducer(false)
                .agreementSuspendedUnsuspendedToProducer(false)
                .clientAddedRemovedToProducer(false)
                .purposeStatusChangedToProducer(false)
                .templateStatusChangedToProducer(false)
                .purposeQuotaAdjustmentRequestToProducer(false)

                // Consumer
                .agreementSuspendedUnsuspendedToConsumer(false)
                .eserviceStateChangedToConsumer(false)
                .agreementActivatedRejectedToConsumer(false)
                .purposeActivatedRejectedToConsumer(false)
                .purposeSuspendedUnsuspendedToConsumer(false)
                .purposeOverQuotaStateToConsumer(false)

                // Instantiator
                .newEserviceTemplateVersionToInstantiator(false)
                .eserviceTemplateNameChangedToInstantiator(false)
                .eserviceTemplateStatusChangedToInstantiator(false)

                // Delegator/Delegate
                .delegationApprovedRejectedToDelegator(false)
                .eserviceNewVersionSubmittedToDelegator(false)
                .eserviceNewVersionApprovedRejectedToDelegate(false)
                .delegationSubmittedRevokedToDelegate(false)

                // Altri
                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
            )
            .inAppConfig(new NotificationConfig()
                // Producer
                .agreementManagementToProducer(false)
                .agreementSuspendedUnsuspendedToProducer(false)
                .clientAddedRemovedToProducer(false)
                .purposeStatusChangedToProducer(false)
                .templateStatusChangedToProducer(false)
                .purposeQuotaAdjustmentRequestToProducer(false)

                // Consumer
                .agreementSuspendedUnsuspendedToConsumer(false)
                .eserviceStateChangedToConsumer(false)
                .agreementActivatedRejectedToConsumer(false)
                .purposeActivatedRejectedToConsumer(false)
                .purposeSuspendedUnsuspendedToConsumer(false)
                .purposeOverQuotaStateToConsumer(false)

                // Instantiator
                .newEserviceTemplateVersionToInstantiator(false)
                .eserviceTemplateNameChangedToInstantiator(false)
                .eserviceTemplateStatusChangedToInstantiator(false)

                // Delegator/Delegate
                .delegationApprovedRejectedToDelegator(false)
                .eserviceNewVersionSubmittedToDelegator(false)
                .eserviceNewVersionApprovedRejectedToDelegate(false)
                .delegationSubmittedRevokedToDelegate(false)

                // Altri
                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
            );
        TenantNotificationConfigUpdateSeed tenantConfig = new TenantNotificationConfigUpdateSeed()
            .enabled(false);

        this.configNotificationTests(tenantConfig, userConfig, this.notificationTestsManager::after);
    }

    private void configNotificationTests(
        TenantNotificationConfigUpdateSeed tenantConfig,
        UserNotificationConfigUpdateSeed userConfig,
        ThrowingConsumer<Task> hook
    ) throws Exception {
        PollingService pollingService = this.getContext().getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();
        hook.accept(() -> applyTaskForEveryUser(() -> {
                pollingService.makePolling(
                    () -> {
                        this.notificationConfigClient.updateUserNotificationConfig(userConfig);
                        return null;
                    },
                    res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                    "Configurazione notifiche user fallita");
                pollingService.makePolling(
                    () -> {
                        this.notificationConfigClient.updateTenantNotificationConfig(tenantConfig);
                        return null;
                    },
                    res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                    "Configurazione notifiche tenant fallita");
            }
        ));
    }

    // TODO generalizzabile in utility separata
    private void applyTaskForEveryUser(Task task) throws Exception {
        List<Tenant> tenantList = this.configFileReader.getTenantList();
        for (Tenant tenant : tenantList) {
            Map<String, List<String>> rolesCopy = new HashMap<>(tenant.getUserRoles());

            // ruoli non permessi dalle APIs in oggetto (che dunque produrrebbero un errore)
            rolesCopy.remove("api");
            rolesCopy.remove("security");
            rolesCopy.remove("support");
            rolesCopy.remove("api,security");

            Set<Entry<String, List<String>>> roles = rolesCopy.entrySet();
            for (Entry<String, List<String>> roleEntry : roles) {
                List<String> users = roleEntry.getValue();
                for (int i = 0; i < users.size(); i++) {
                    String token = this.getContext().getIdentityService()
                        .getToken(tenant.getName(), roleEntry.getKey(), i);
                    this.clientTokenConfigurator.setBearerToken(token);

                    task.run();
                }
            }
        }
    }

}

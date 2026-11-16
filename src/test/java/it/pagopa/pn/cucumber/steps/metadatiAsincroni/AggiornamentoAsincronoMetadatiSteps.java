package it.pagopa.pn.cucumber.steps.metadatiAsincroni;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.PaSummary;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.PgGroup;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationPriceResponseV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationRecipientV24;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalRegistryPrivateUserApi;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.NotificationProcessCostResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Step del TestSet "Aggiornamento asincrono dei metadati della notifica" (Fase 1 e Fase 2).
 * Le verifiche infrastrutturali usano gli stessi strumenti già presenti nel progetto:
 * DynamoDB tramite {@link DynamoDbService}, CloudWatch tramite gli step di AwsServiceSteps.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AggiornamentoAsincronoMetadatiSteps {

    private static final String DYNAMO_KEY_SEPARATOR = "##";
    // Valore della componente groupId della pk di pn-NotificationDelegationMetadata per deleghe senza gruppi
    private static final String NO_GROUP_ID = "";

    private final SharedSteps sharedSteps;
    private final DynamoDbService dynamoDbService;
    private final IPnPaB2bClient b2bClient;
    private final IPnExternalRegistryPrivateUserApi externalRegistryPrivateApi;
    private final PnPaB2bInternalInformalClientImpl internalDeliveryClient;
    private final PnExternalServiceClientImpl externalServiceClient;

    @Value("${pn.authentication.pg.organization.id}")
    private String pgOrganizationId;

    @Value("${pn.authentication.pg.admin.uid}")
    private String pgAdminUid;

    public AggiornamentoAsincronoMetadatiSteps(SharedSteps sharedSteps,
                                               DynamoDbService dynamoDbService,
                                               IPnPaB2bClient b2bClient,
                                               IPnExternalRegistryPrivateUserApi externalRegistryPrivateApi,
                                               PnPaB2bInternalInformalClientImpl internalDeliveryClient,
                                               PnExternalServiceClientImpl externalServiceClient) {
        this.sharedSteps = sharedSteps;
        this.dynamoDbService = dynamoDbService;
        this.b2bClient = b2bClient;
        this.externalRegistryPrivateApi = externalRegistryPrivateApi;
        this.internalDeliveryClient = internalDeliveryClient;
        this.externalServiceClient = externalServiceClient;
    }

    // ===================================== API =====================================

    /**
     * creditorTaxId e noticeCode sono letti dalla request inviata (e non dalla notifica) perché lo step
     * è pensato per essere eseguito prima che la notifica passi in stato ACCEPTED.
     */
    @When("viene richiesto il costo della notifica tramite api v2.3 per l'avviso {int} del destinatario {int}")
    public void requestNotificationPriceV23(int paymentIndex, int recipientIndex) {
        String creditorTaxId = sharedSteps.getRecipientCreditorTaxId(recipientIndex, paymentIndex);
        String noticeCode = sharedSteps.getRecipientNoticeCode(recipientIndex, paymentIndex);
        sharedSteps.setNotificationError(null);
        try {
            NotificationPriceResponseV23 response = b2bClient.getNotificationPriceV23(creditorTaxId, noticeCode);
            log.info("Costo notifica v2.3 per creditorTaxId {} e noticeCode {}: {}", creditorTaxId, noticeCode, response);
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @Then("viene invocata l'API di calcolo costo della notifica per il destinatario {int} che risponde correttamente")
    public void notificationProcessCostRespondsCorrectly(int recipientIndex) {
        FullSentNotificationV29 notification = sharedSteps.getSentNotificationLastVersion();
        Boolean applyCost = notification.getRecipients().get(recipientIndex).getPayments().get(0).getPagoPa().getApplyCost();
        try {
            NotificationProcessCostResponse response = b2bClient.getNotificationProcessCost(
                    sharedSteps.getNotificationIun(),
                    recipientIndex,
                    notification.getNotificationFeePolicy().getValue(),
                    applyCost,
                    notification.getPaFee(),
                    notification.getVat());
            log.info("Risposta notification-process-cost destinatario {}: {}", recipientIndex, response);
            assertThat(response).as("La risposta di notification-process-cost non deve essere nulla").isNotNull();
        } catch (HttpStatusCodeException e) {
            throw new AssertionError(String.format("notification-process-cost per IUN %s e destinatario %d ha risposto %s: %s",
                    sharedSteps.getNotificationIun(), recipientIndex, e.getStatusCode(), e.getResponseBodyAsString()), e);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @When("viene invocata l'API di recupero degli enti attivi su PN")
    public void listOnboardedPa() {
        sharedSteps.setNotificationError(null);
        try {
            List<PaSummary> onboardedPa = externalRegistryPrivateApi.listOnboardedPa(null, null);
            log.info("Enti attivi su PN restituiti: {}", onboardedPa == null ? 0 : onboardedPa.size());
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    /**
     * Usa la PG e l'utente amministratore configurati in application.properties
     * (pn.authentication.pg.organization.id / pn.authentication.pg.admin.uid), gli stessi di LegalPersonAuthSteps.
     */
    @When("viene invocata l'API di recupero dei gruppi utente della PG per l'utente amministratore")
    public void getPgUserGroups() {
        sharedSteps.setNotificationError(null);
        try {
            List<PgGroup> groups = externalRegistryPrivateApi.getPgUserGroups(pgOrganizationId, pgAdminUid);
            log.info("Gruppi utente PG restituiti: {}", groups == null ? 0 : groups.size());
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @When("viene invocata l'API deprecata di aggiornamento stato della notifica")
    public void callDeprecatedUpdateStatus() {
        sharedSteps.setNotificationError(null);
        try {
            internalDeliveryClient.callDeprecatedUpdateStatus();
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    // =================================== DynamoDB ===================================

    /**
     * Su pn-PnDeliveryPushShedLock la pk (_id) coincide con lo IUN della notifica.
     * Il lock viene rilasciato a fine elaborazione: si attende fino a 2 minuti che lo stato atteso venga raggiunto.
     */
    @Then("verifico che su DynamoDB {is} presente il lock della notifica nella tabella pn-PnDeliveryPushShedLock")
    public void checkShedLock(boolean isPresent) {
        String iun = sharedSteps.getNotificationIun();
        try {
            await().atMost(2, TimeUnit.MINUTES).pollInterval(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
                QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.SHEDLOCK_DELIVERY_PUSH,
                        Map.of(":v__id", AttributeValue.builder().s(iun).build()));
                log.info("Record trovati su pn-PnDeliveryPushShedLock per _id {}: {}", iun, queryResponse.count());
                if (isPresent) {
                    assertThat(queryResponse.items()).as("pn-PnDeliveryPushShedLock deve contenere il lock per lo IUN %s", iun).isNotEmpty();
                } else {
                    assertThat(queryResponse.items()).as("pn-PnDeliveryPushShedLock non deve contenere il lock per lo IUN %s", iun).isEmpty();
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * pk di pn-NotificationDelegationMetadata: iun##recipientInternalId##delegateInternalId##groupId.
     */
    @Then("verifico che su DynamoDB {is} presente il record di delega della notifica per il destinatario {int} e il delegato {string} nella tabella pn-NotificationDelegationMetadata")
    public void checkNotificationDelegationMetadata(boolean isPresent, int recipientIndex, String delegateName) {
        String iun = sharedSteps.getNotificationIun();
        NotificationRecipientV24 recipient = sharedSteps.getSentNotificationLastVersion().getRecipients().get(recipientIndex);
        Destinatario delegate = sharedSteps.getDestinatarioRegistry().destinatario(delegateName);
        assertThat(delegate).as("Delegato %s non censito in DestinatarioRegistry", delegateName).isNotNull();

        String recipientInternalId = externalServiceClient.getInternalIdFromTaxId(recipient.getRecipientType().getValue(), recipient.getTaxId());
        String delegateInternalId = externalServiceClient.getInternalIdFromTaxId(delegate.getRecipientType(), delegate.getTaxId());
        String pk = String.join(DYNAMO_KEY_SEPARATOR, iun, recipientInternalId, delegateInternalId, NO_GROUP_ID);

        try {
            await().atMost(2, TimeUnit.MINUTES).pollInterval(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
                QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.NOTIFICATION_DELEGATION_METADATA,
                        Map.of(":v_iun_recipientId_delegateId_groupId", AttributeValue.builder().s(pk).build()));
                log.info("Record trovati su pn-NotificationDelegationMetadata per pk {}: {}", pk, queryResponse.count());
                if (isPresent) {
                    assertThat(queryResponse.items()).as("pn-NotificationDelegationMetadata deve contenere un record per la pk %s", pk).isNotEmpty();
                } else {
                    assertThat(queryResponse.items()).as("pn-NotificationDelegationMetadata non deve contenere record per la pk %s", pk).isEmpty();
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * pk di pn-NotificationsCost: creditorTaxId##noticeCode. Gli avvisi sono letti dalla request inviata,
     * dato che una notifica REFUSED non è recuperabile tramite IUN.
     * La rimozione avviene in modo asincrono (lambda): si attende fino a 2 minuti.
     */
    @Then("verifico che su DynamoDB non siano presenti i pagamenti del destinatario {int} nella tabella pn-NotificationsCost")
    public void checkNotificationsCostRemoved(int recipientIndex) {
        List<String> costKeys = collectPagoPaCostKeys(recipientIndex);
        assertThat(costKeys).as("La request del destinatario %d non contiene avvisi pagoPA", recipientIndex).isNotEmpty();
        log.info("Chiavi pn-NotificationsCost da verificare: {}", costKeys);
        try {
            await().atMost(2, TimeUnit.MINUTES).pollInterval(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
                for (String costKey : costKeys) {
                    QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.NOTIFICATIONS_COST,
                            Map.of(":v_creditorTaxId_noticeCode", AttributeValue.builder().s(costKey).build()));
                    assertThat(queryResponse.items())
                            .as("pn-NotificationsCost contiene ancora il pagamento %s", costKey)
                            .isEmpty();
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Non esiste un accessor per il numero di pagamenti della request: si scorrono gli indici finché presenti.
     */
    private List<String> collectPagoPaCostKeys(int recipientIndex) {
        List<String> keys = new ArrayList<>();
        for (int paymentIndex = 0; ; paymentIndex++) {
            try {
                String creditorTaxId = sharedSteps.getRecipientCreditorTaxId(recipientIndex, paymentIndex);
                String noticeCode = sharedSteps.getRecipientNoticeCode(recipientIndex, paymentIndex);
                keys.add(creditorTaxId + DYNAMO_KEY_SEPARATOR + noticeCode);
            } catch (IndexOutOfBoundsException endOfPayments) {
                return keys;
            }
        }
    }
}

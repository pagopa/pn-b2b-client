package it.pagopa.pn.interop.cucumber.steps.notification;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.AssertCheckType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.notification.INotificationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.utility.property_resolver.PropertyResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class NotificationSteps extends AbstractCommonSteps<Notification, UUID> {
    private final PropertyResolver propertyResolver;
    private final INotificationClient notificationClient;

    // FIXME 09/01/2026 ad uso interno temporaneo, rimuovere
    private String regex;

    public NotificationSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator, PropertyResolver propertyResolver) {
        super("inAppNotification", clientTokenConfigurator.getNotificationClient(), sharedStepsContext);
        this.notificationClient = clientTokenConfigurator.getNotificationClient();
        this.propertyResolver = propertyResolver;
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
    @Then("è presente una notifica in-app il cui messaggio aderisce al seguente pattern: {string}")
    public void checkInAppNotificationBody(String body){
        PollingService pollingService = getContext().getPollingService();
        Predicate<Notification> bodyMatcher = notification -> notification.getBody().matches(body);

        /* TODO al momento sconosciute le tempistiche per la generazione delle notifiche, potrebbe
         *  non bastare */
        pollingService.makePolling(
            () -> this.notificationClient.get(bodyMatcher),
            Optional::isPresent,
            "Non presente alcuna notifica che corrisponda alla regex fornita");
    }

    // FIXME 09/01/2026 ad uso interno temporaneo, rimuovere
    @Then("la property {string} estratta è coerente con l'id dell'e-service creato")
    public void testResolver(String placeHolder){
        String propertyPath = placeHolder.substring(2, placeHolder.length() - 1);
        String contextProperty = propertyResolver.getContextProperty(propertyPath);
        assertThat(contextProperty).isEqualTo(this.getContext().getEServicesCommonContext().getEserviceId().toString());
    }

    // FIXME 09/01/2026 ad uso interno temporaneo, rimuovere
    @When("pongo il seguente template di body in forma di regex {string}")
    public void testRegex1(String regex) {
        this.regex = regex;
    }

    // FIXME 09/01/2026 ad uso interno temporaneo, rimuovere
    @When("riesco a fare il match con la seguente stringa {string}")
    public void testRegex2(String toMatch) {
        assertThat(toMatch).matches(regex);
    }
}

package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
@Data
public class EServiceTemplateVersionUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final EasyRandom easyRandom;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateVersionUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
    }

    @Given("l'utente effettua delle modifiche alla versione dell'e-service template con successo")
    public void updateEServiceTemplateVersionSuccessfully() {
        updateEServiceTemplateVersion();
        checkEServiceTemplateVersionUpdate();
    }

    @When("l'utente tenta delle modifiche alla versione di un e-service template inesistente")
    public void updateNonExistentEServiceTemplateVersion() {
        UpdateEServiceTemplateVersionSeed updateSeed = new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            .attributes(testAssistant.nextAttributesSeed())
            .dailyCallsPerConsumer(500)
            .dailyCallsTotal(5000)
            .voucherLifespan(86400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), updateSeed);
    }

    @When("l'utente tenta delle modifiche alla versione dell'e-service template con una descrizione di lunghezza {int}")
    public void updateEServiceTemplateVersion(int descriptionLength) {
        String description = (new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue();
        sharedStepsContext.getEServiceTemplateStepContext().setLastTemplateVersionUpdateSeed(new UpdateEServiceTemplateVersionSeed()
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .attributes(new EServiceTemplateAttributesSeed())
                //.attributes(new EServiceTemplateAttributesSeed().declared(
                //    List.of(List.of(new EServiceTemplateVersionAttributeSeed().setId(UUID.randomUUID()).explicitAttributeVerification(false)))))
                .dailyCallsPerConsumer(100)
                .dailyCallsTotal(1000)
                .voucherLifespan(86400)
                .description(description));
        updateEServiceTemplateVersion(
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed());
    }

    @When("l'utente tenta delle modifiche alla versione dell'e-service template")
    public void updateEServiceTemplateVersion() {
        sharedStepsContext.getEServiceTemplateStepContext().setLastTemplateVersionUpdateSeed(new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            .attributes(new EServiceTemplateAttributesSeed())
            //.attributes(new EServiceTemplateAttributesSeed().declared(
            //    List.of(List.of(new EServiceTemplateVersionAttributeSeed().setId(UUID.randomUUID()).explicitAttributeVerification(false)))))
            .dailyCallsPerConsumer(100)
            .dailyCallsTotal(1000)
            .voucherLifespan(86400)
            .description("Nuova descrizione della versione"));
        updateEServiceTemplateVersion(
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed());
    }

    @When("l'utente tenta di aggiungere l'attributo creato alla versione dell'e-service template")
    public void assignAttributeToEServiceTemplateVersion() {
        Attribute lastCreatedAttribute = sharedStepsContext.getAttributeCommonContext()
            .getLastCreatedAttribute();
        // TODO cablato solo per attributi certificati, generalizzare per ogni tipo di attributo
        EServiceTemplateAttributesSeed lastEServiceTemplateAttributesSeed = new EServiceTemplateAttributesSeed().certified(
            List.of(List.of(
                new EServiceTemplateVersionAttributeSeed().id(lastCreatedAttribute.getId())
                    .explicitAttributeVerification(false))));
        sharedStepsContext.getEServiceTemplateStepContext().setLastTemplateVersionAttributesSeed(lastEServiceTemplateAttributesSeed);
        sharedStepsContext.getEServiceTemplateStepContext().setLastTemplateVersionUpdateSeed(new UpdateEServiceTemplateVersionSeed()
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .attributes(lastEServiceTemplateAttributesSeed)
                .dailyCallsPerConsumer(100)
                .dailyCallsTotal(1000)
                .voucherLifespan(86400)
                .description("Nuova descrizione della versione"));
        updateEServiceTemplateVersion(
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed());
    }

    @When("l'utente effettua l'aggiunta dell'attributo creato alla versione dell'e-service template con successo")
    public void addAttributeToEServiceTemplateVersionSuccessfully() {
        assignAttributeToEServiceTemplateVersion();
        checkEServiceTemplateVersionAttributesEdited();
    }

    @When("l'utente tenta di modificare la versione dell'e-service template indicando una specifica vuota")
    public void updateEServiceTemplateVersionWithEmptySpec() {
        updateEServiceTemplateVersion(
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
            new UpdateEServiceTemplateVersionSeed());
    }

    @When("l'utente modifica la versione dell'e-service template con:")
    public void updateEServiceTemplateVersionWithSpec(UpdateEServiceTemplateVersionSeed eServiceTemplateVersionSeed) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        this.updateEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, eServiceTemplateVersionSeed);
    }


    @Then("le modifiche alla versione sono state applicate correttamente")
    public void checkEServiceTemplateVersionUpdate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        if(!httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            fail("Le modifiche alla versione dell'e-service template non sono state "
                    + "applicate correttamente. Ultimo errore noto: %s", httpCallExecutor.getErrorMessage());
        }

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && testAssistant.areConsistent(this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed(), res.getBody()),
                "La versione dell'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            fail("Le modifiche alla versione dell'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed(), httpCallExecutor.getResponse());
        }
    }

    @Then("la modifica degli attributi della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionAttributesEdited() {
        Predicate<EServiceTemplateVersionDetails> attributesMatch = version -> {
            DescriptorAttributes retrievedAttributes = version.getAttributes();
            EServiceTemplateAttributesSeed retrievedAttributesSeed = this.descriptorAttributesMapper.mapAttributesToSeeds(retrievedAttributes);
            return retrievedAttributesSeed.equals(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionAttributesSeed());
        };
        testAssistant.checkEServiceTemplateVersion(attributesMatch, "Gli attributi della versione dell'e-service template non sono stati modificati correttamente");
    }

    @Then("la modifica degli attributi della versione dell'e-service template è stata effettuata correttamente e la descrizione è lunga {int} caratteri")
    public void checkEServiceTemplateVersionAttributesEdited(int descriptionLength) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        if(!httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            fail("Le modifiche alla versione dell'e-service template non sono state "
                    + "applicate correttamente. Ultimo errore noto: %s", httpCallExecutor.getErrorMessage());
        }

        try {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(
                            () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                                    eServiceTemplateId,
                                    eServiceTemplateVersionId),
                            ResponseEntity::getStatusCode),
                    res -> nonNull(res.getBody()) && testAssistant.areConsistent(this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed(), res.getBody()),
                    "La versione dell'e-service template non corrisponde alle modifiche apportate"
            );

            Assertions.assertEquals(
                    descriptionLength,
                    ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody().getDescription().length()
            );
        } catch (PollingPredicateException e) {
            fail("Le modifiche alla versione dell'e-service template non sono state "
                            + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                    this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed(), httpCallExecutor.getResponse());
        }
    }

    private void updateEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UpdateEServiceTemplateVersionSeed sameNameUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersion(
                eServiceTemplateId,
                eServiceTemplateVersionId,
                sameNameUpdateSeed));
    }

}

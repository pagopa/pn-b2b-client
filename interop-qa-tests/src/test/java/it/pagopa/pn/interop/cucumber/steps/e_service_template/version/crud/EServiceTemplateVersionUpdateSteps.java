package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
@Data
public class EServiceTemplateVersionUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;
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
        BFFDataPreparationService dataPreparationService,
        EServiceTemplateTestAssistant testAssistant,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
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

    @When("l'utente tenta di aggiungere i seguenti attributi alla versione dell'e-service template:")
    public void assignAttributeToEServiceTemplateVersion(List<EServiceAttributeSpec> attributeSpecs) {

        EServiceTemplateAttributesSeed eServiceTemplateAttributesSeed = new EServiceTemplateAttributesSeed();

        AttributeCommonContext attributeCommonContext = sharedStepsContext.getAttributeCommonContext();

        attributeCommonContext.getRequiredCertifiedAttributes().clear();
        attributeCommonContext.getRequiredDeclaredAttributes().clear();
        attributeCommonContext.getRequiredVerifiedAttributes().clear();

        for (int i = 0; i < attributeSpecs.size(); i++) {
            EServiceAttributeSpec attributeSpec = attributeSpecs.get(i);
            Attribute attribute;

            if (attributeSpec.getKind() != AttributeKind.CERTIFIED_DISCRETE) {
                int millis = Instant.now().get(ChronoField.MILLI_OF_SECOND);
                String attrName = "attribute-%d-%d-%s".formatted(2 * i, sharedStepsContext.getTestSeed() + millis, attributeSpec.getKind());
                attribute = dataPreparationService.createAttribute(attributeSpec.getKind(), attrName);
            } else {
                CertifiedDiscreteTenantAttribute ownedCertifiedDiscreteAttr = sharedStepsContext.getAttributeCommonContext().getOwnedCertifiedDiscreteAttributes().get(0);
                attribute = new Attribute();
                attribute.setId(ownedCertifiedDiscreteAttr.getId());
            }

            EServiceTemplateVersionAttributeSeed seed = new EServiceTemplateVersionAttributeSeed()
                    .explicitAttributeVerification(true)
                    .id(attribute.getId());

            int group = attributeSpec.getGroup();

            switch (attributeSpec.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> {
                    if (attributeSpec.getDailyCallsPerConsumer() != null) {
                        throw new UnsupportedOperationException("Daily calls per consumer not supported for certified attributes in templates");
                    }
                    if (attributeSpec.getKind() == AttributeKind.CERTIFIED_DISCRETE) {
                        seed.setDiscreteConfig(new EServiceAttributeCertifiedDiscreteConfig()
                            .comparator(attributeSpec.getComparator())
                            .threshold(attributeSpec.getValue()));
                    }
                    addAttributeSeedToGroup(eServiceTemplateAttributesSeed.getCertified(), group, seed);
                    addAttributeToGroup(attributeCommonContext.getRequiredCertifiedAttributes(), group, attribute.getId());
                }
                case DECLARED -> {
                    addAttributeSeedToGroup(eServiceTemplateAttributesSeed.getDeclared(), group, seed);
                    addAttributeToGroup(attributeCommonContext.getRequiredDeclaredAttributes(), group, attribute.getId());
                }
                case VERIFIED -> {
                    addAttributeSeedToGroup(eServiceTemplateAttributesSeed.getVerified(), group, seed);
                    addAttributeToGroup(attributeCommonContext.getRequiredVerifiedAttributes(), group, attribute.getId());
                }
            }
        }

        EServiceTemplateInfo lastTemplateManaged = this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();

        UpdateEServiceTemplateVersionSeed updateSeed = new UpdateEServiceTemplateVersionSeed();
        updateSeed.attributes(eServiceTemplateAttributesSeed)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .dailyCallsPerConsumer(100)
                .dailyCallsTotal(1000)
                .voucherLifespan(86400)
                .description(lastTemplateManaged.getEServiceDescription());

        updateEServiceTemplateVersion(
                lastTemplateManaged.getId(),
                lastTemplateManaged.getLastVersionId(),
                updateSeed
        );
    }

    @When("l'utente modifica il primo attributo certificato discreto nel primo gruppo degli attributi certificati con discrete threshold {int} e discrete comparator a {string}")
    public void updateCertifiedDiscreteAttribute(int threshold, String comparator) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        DescriptorAttributeSeed attributeSeed = new DescriptorAttributeSeed()
                .id(sharedStepsContext.getAttributeCommonContext().getOwnedCertifiedDiscreteAttributes().get(0).getId())
                .explicitAttributeVerification(true)
                .discreteConfig(
                        new EServiceAttributeCertifiedDiscreteConfig()
                                .comparator(AttributeCertifiedDiscreteComparator.valueOf(comparator)).threshold(threshold)
                                .threshold(threshold)
                );
        DescriptorAttributesSeed seed = new DescriptorAttributesSeed();
        seed.addCertifiedItem(List.of(attributeSeed));

        httpCallExecutor.performCall(
                () -> eServiceTemplateClient.updateEServiceTemplateVersionAttributes(
                        eServiceTemplateId, eServiceTemplateVersionId, seed
                )
        );
    }

    @When("gli attributi del template e-service hanno la seguente configurazione:")
    public void checkAssignedAttributesToEServiceTemplateVersion(List<EServiceAttributeSpec> attributeSpecs) {

        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> eServiceTemplateClient.getEServiceTemplateVersion(
                                eServiceTemplateId,
                                eServiceTemplateVersionId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service template"
        );

        EServiceTemplateVersionDetails retrievedTemplateVersion = (EServiceTemplateVersionDetails) this.httpCallExecutor.getResponse();


        List<AttributeKind> attributeKinds = new ArrayList<>(attributeSpecs.stream().map(EServiceAttributeSpec::getKind).distinct().toList());

        // Certified discrete attributes are managed as certified attributes
        if (attributeKinds.contains(AttributeKind.CERTIFIED_DISCRETE)) {
            if (attributeKinds.contains(AttributeKind.CERTIFIED)) {
                attributeKinds.remove(AttributeKind.CERTIFIED_DISCRETE);
            }
        }

        for (AttributeKind attributeKind : attributeKinds) {
            List<Integer> groupPerKind = attributeSpecs.stream()
                    .filter(a -> {
                        if (attributeKind.equals(AttributeKind.CERTIFIED)) {
                            return (a.getKind().equals(attributeKind) || a.getKind().equals(AttributeKind.CERTIFIED_DISCRETE));
                        } else {
                            return a.getKind().equals(attributeKind);
                        }
                    })
                    .map(EServiceAttributeSpec::getGroup)
                    .distinct()
                    .toList();

            for (int groupIndex : groupPerKind) {
                // Certified discrete attributes are managed as certified attributes
                List<EServiceAttributeSpec> attributeSpecsByKindInGroup = attributeSpecs.stream()
                        .filter(a -> {
                            if (attributeKind.equals(AttributeKind.CERTIFIED)) {
                                return (a.getKind().equals(attributeKind) || a.getKind().equals(AttributeKind.CERTIFIED_DISCRETE)) && a.getGroup().equals(groupIndex);
                            } else {
                                return a.getKind().equals(attributeKind) && a.getGroup().equals(groupIndex);
                            }
                        })
                        .toList();

                for (int x = 0; x < attributeSpecsByKindInGroup.size(); x++) {
                    EServiceAttributeSpec attributeSpec = attributeSpecsByKindInGroup.get(x);

                    List<List<UUID>> contextAttributes = switch (attributeKind) {
                        case CERTIFIED, CERTIFIED_DISCRETE -> sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
                        case DECLARED -> sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes();
                        case VERIFIED -> sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes();
                    };

                    UUID attributeId = contextAttributes.get(attributeSpec.getGroup()).get(x);
                    // L'ordine degli attributi non deve essere necessariamente rispettato
                    List<DescriptorAttribute> templateAttrs = switch (attributeKind) {
                        case CERTIFIED, CERTIFIED_DISCRETE -> retrievedTemplateVersion.getAttributes().getCertified().get(groupIndex);
                        case DECLARED -> retrievedTemplateVersion.getAttributes().getDeclared().get(groupIndex);
                        case VERIFIED -> retrievedTemplateVersion.getAttributes().getVerified().get(groupIndex);
                    };
                    DescriptorAttribute attr = templateAttrs.stream().filter(a -> a.getId().equals(attributeId)).findFirst().orElse(null);

                    Assertions.assertNotNull(attr);
                    Assertions.assertEquals(attr.getId(), attributeId);

                    AttributeCertifiedDiscreteComparator discreteComparator = attributeSpec.getComparator();
                    Integer discreteThreshold = attributeSpec.getValue();
                    if (discreteComparator != null || discreteThreshold != null) {
                        Assertions.assertNotNull(attr.getDiscreteConfig());
                        Assertions.assertEquals(attr.getDiscreteConfig().getComparator().getValue(), discreteComparator.getValue());
                        Assertions.assertEquals(attr.getDiscreteConfig().getThreshold(), discreteThreshold);
                    }
                }
            }
        }
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

    private static void addAttributeSeedToGroup(List<List<EServiceTemplateVersionAttributeSeed>> groups, int groupIndex, EServiceTemplateVersionAttributeSeed seed) {
        while (groups.size() <= groupIndex) {
            groups.add(new ArrayList<>());
        }
        groups.get(groupIndex).add(seed);
    }

    private static void addAttributeToGroup(List<List<UUID>> groups, int groupIndex, UUID uuid) {
        while (groups.size() <= groupIndex) {
            groups.add(new ArrayList<>());
        }
        groups.get(groupIndex).add(uuid);
    }
}

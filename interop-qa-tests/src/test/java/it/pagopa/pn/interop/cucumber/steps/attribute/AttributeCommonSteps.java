package it.pagopa.pn.interop.cucumber.steps.attribute;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.domain.TenantType;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purposes.resolver.PurposeResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.utility.EServiceDescriptorUtils;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.*;

// TODO riformulare così da rimuovere gli inutilizzati parametri "tenantType"
@Slf4j
public class AttributeCommonSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final AttributeCommonContext attributeCommonContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final EServiceDescriptorUtils eServiceDescriptorUtils;
    private final PurposeResolver purposesResolver;
    private final DelayService delayService;

    public AttributeCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        BFFDataPreparationService dataPreparationService,
        DelayService delayService)
    {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.attributeCommonContext = sharedStepsContext.getAttributeCommonContext();
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceDescriptorUtils = new EServiceDescriptorUtils(
            this.clientTokenConfigurator,
            this.sharedStepsContext
        );
        this.purposesResolver = new PurposeResolver(sharedStepsContext);
        this.delayService = delayService;
    }

    @Given("{tenantType} ha già creato {int} attribut(i)(o) {attributeKind}")
    public void createAttributes(TenantType tenantType, int count, AttributeKind attributeKind) {

        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType.name(), null));

        int size = switch (attributeKind) {
            case CERTIFIED -> attributeCommonContext.getRequiredCertifiedAttributes().isEmpty() ? 0 : attributeCommonContext.getRequiredCertifiedAttributes().get(0).size();
            case DECLARED -> attributeCommonContext.getRequiredDeclaredAttributes().isEmpty() ? 0 : attributeCommonContext.getRequiredDeclaredAttributes().get(0).size();
            case VERIFIED -> attributeCommonContext.getRequiredVerifiedAttributes().isEmpty() ? 0 : attributeCommonContext.getRequiredVerifiedAttributes().get(0).size();
            case CERTIFIED_DISCRETE -> attributeCommonContext.getRequiredCertifiedAttributes().isEmpty() ? 0 : attributeCommonContext.getRequiredCertifiedAttributes().get(0).size();
        };

        List<Attribute> createdAttributes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Attribute attribute = dataPreparationService.createAttribute(
                attributeKind,
                "attribute-%d-%d-%s".formatted((size + i), sharedStepsContext.getTestSeed(), attributeKind));
            createdAttributes.add(attribute);
        }

        if (!createdAttributes.isEmpty()) {
            attributeCommonContext.setAttributeId(createdAttributes.get(0).getId());
        }
        attributeCommonContext.setCreatedAttributes(createdAttributes);

        List<UUID> attributeIds = createdAttributes.stream().map(Attribute::getId).toList();

        switch (attributeKind) {
            case CERTIFIED, CERTIFIED_DISCRETE -> {
                if (attributeCommonContext.getRequiredCertifiedAttributes().isEmpty()) {
                    attributeCommonContext.getRequiredCertifiedAttributes().add(new ArrayList<>(attributeIds));
                } else {
                    attributeCommonContext.getRequiredCertifiedAttributes().get(0).addAll(attributeIds);
                }
            }
            case DECLARED -> {
                if (attributeCommonContext.getRequiredDeclaredAttributes().isEmpty()) {
                    attributeCommonContext.getRequiredDeclaredAttributes().add(new ArrayList<>(attributeIds));
                } else {
                    attributeCommonContext.getRequiredDeclaredAttributes().get(0).addAll(attributeIds);
                }
            }
            case VERIFIED -> {
                if (attributeCommonContext.getRequiredVerifiedAttributes().isEmpty()) {
                    attributeCommonContext.getRequiredVerifiedAttributes().add(new ArrayList<>(attributeIds));
                } else {
                    attributeCommonContext.getRequiredVerifiedAttributes().get(0).addAll(attributeIds);
                }
            }
        }
    }

    @Given("{tenantType} ha già creato un attributo {attributeKind} con nome che contiene {string}")
    public void createAttributeWithNameKeyword(TenantType tenantType, AttributeKind attributeKind, String keyword) {
        dataPreparationService.createAttribute(
            attributeKind,
            "%d-%s".formatted(sharedStepsContext.getTestSeed(), keyword)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} attribut(i)(o)")
    public void checkAttributeCreation(int statusCode, int count) {
        assertSoftly(softly -> {
            softly.assertThat(httpCallExecutor.getResponse())
                    .as("Attribute response NULL check")
                    .isNotNull();
            softly.assertThat(httpCallExecutor.getResponseStatus().value())
                    .as("Attribute response status code check")
                    .isEqualTo(statusCode);
            softly.assertThat(httpCallExecutor.getResponse())
                    .as("Attribute response attribute count check")
                    .extracting(Attributes.class::cast)
                    .extracting(Attributes::getResults)
                    .asList()
                    .hasSize(count);
        });
    }

    @Given("l'utente associa l'attributo {attributeKind} {int}-esimo creato all'eservice")
    public void associateAttributeToEService(AttributeKind attributeType, int attributeIndex) {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        ProducerEServiceDescriptor eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified()))
            .declared(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared()))
            .verified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified()));

        switch (attributeType) {
            case CERTIFIED -> attributesSeed.getCertified().get(0).add(
                new DescriptorAttributeSeed().id(attributeCommonContext.getRequiredCertifiedAttributes().get(0).get(attributeIndex))
                        .explicitAttributeVerification(true)
            );
            case VERIFIED -> attributesSeed.getVerified().get(0).add(
                new DescriptorAttributeSeed().id(attributeCommonContext.getRequiredVerifiedAttributes().get(0).get(attributeIndex))
                        .explicitAttributeVerification(true)
            );
            case DECLARED -> attributesSeed.getDeclared().get(0).add(
                new DescriptorAttributeSeed().id(attributeCommonContext.getRequiredDeclaredAttributes().get(0).get(attributeIndex))
                        .explicitAttributeVerification(true)
            );
        }

        eServiceDescriptorUtils.updateEServiceDescriptor(eServiceDescriptor, attributesSeed);

        Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful());

        PollingService.makePolling(
            () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId),
            res -> {
                if (res != null) {
                    return switch (attributeType) {
                        case CERTIFIED -> res.getAttributes().getCertified().get(0).stream()
                            .anyMatch(attr -> attr.getId()
                                    .equals(attributeCommonContext.getRequiredCertifiedAttributes().get(0).get(attributeIndex))
                            );
                        case VERIFIED -> res.getAttributes().getVerified().get(0).stream()
                            .anyMatch(attr -> attr.getId()
                                    .equals(attributeCommonContext.getRequiredVerifiedAttributes().get(0).get(attributeIndex))
                            );
                        case DECLARED -> res.getAttributes().getDeclared().get(0).stream()
                            .anyMatch(attr -> attr.getId()
                                    .equals(attributeCommonContext.getRequiredDeclaredAttributes().get(0).get(attributeIndex))
                            );
                        default -> throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeType));
                    };
                }

                return false;
            } ,
            String.format("Errore durante la verifica dell'associazione dell'attributo %s all'e-service %s", attributeType, eServiceId),
            5,
            2_000
        );
    }

    @Given("l'utente tenta di dichiarare due volte lo stesso attributo certificato ognuno con un dailyCallsPerConsumer differente")
    public void duplicateCertifiedAttrWithDifferentDailyCallsPerConsumer() {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        ProducerEServiceDescriptor eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        if ((eServiceDescriptor.getAttributes().getCertified().isEmpty()) || (eServiceDescriptor.getAttributes().getCertified().get(0).isEmpty())) {
            throw new IllegalStateException("L'e-service non ha attributi certificati");
        }
        DescriptorAttribute existingCertifiedAttr = eServiceDescriptor.getAttributes().getCertified().get(0).get(0);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified()))
            .declared(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared()))
            .verified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified()));

        int newDailyCallsPerConsumer = existingCertifiedAttr.getDailyCallsPerConsumer() == null ? 1 : existingCertifiedAttr.getDailyCallsPerConsumer() + 1;

        attributesSeed.getCertified().get(0).add(
            new DescriptorAttributeSeed()
                .id(existingCertifiedAttr.getId())
                .explicitAttributeVerification(existingCertifiedAttr.getExplicitAttributeVerification())
                .dailyCallsPerConsumer(newDailyCallsPerConsumer)
        );

        eServiceDescriptorUtils.updateEServiceDescriptor(eServiceDescriptor, attributesSeed);
    }

    @Given("l'utente tenta di duplicare l'attributo {attributeKind} {int}-esimo nel gruppo {int}-esimo")
    public void duplicateAttributeInGroup(AttributeKind attributeKind, int attributeIndex, int srcGroupIndex) {
        duplicateAttributeInGroup(attributeKind, attributeIndex, srcGroupIndex, srcGroupIndex);
    }

    @Given("l'utente tenta di duplicare l'attributo {attributeKind} {int}-esimo contenuto nel gruppo {int}-esimo nel gruppo {int}-esimo")
    public void duplicateAttributeInGroup(AttributeKind attributeKind, int attributeIndex, int srcGroupIndex, int targetGroupIndex) {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        ProducerEServiceDescriptor eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        List<List<DescriptorAttribute>> existingAttributeGroups = switch (attributeKind) {
            case CERTIFIED -> eServiceDescriptor.getAttributes().getCertified();
            case DECLARED -> eServiceDescriptor.getAttributes().getDeclared();
            case VERIFIED -> eServiceDescriptor.getAttributes().getVerified();
            default -> throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeKind));
        };

        if ((existingAttributeGroups.isEmpty()) || (existingAttributeGroups.get(srcGroupIndex).isEmpty())) {
            throw new IllegalStateException("L'e-service non ha attributi per il gruppo");
        }

        if (targetGroupIndex >= existingAttributeGroups.size()) {
            throw new IllegalStateException(String.format("L'e-service non ha %d gruppi per gli attributi %s", (targetGroupIndex + 1), attributeKind));
        }

        DescriptorAttribute existingAttr = existingAttributeGroups.get(srcGroupIndex).get(attributeIndex);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified()))
            .declared(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared()))
            .verified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified()));

        DescriptorAttributeSeed attributeSeed = new DescriptorAttributeSeed()
            .id(existingAttr.getId())
            .explicitAttributeVerification(existingAttr.getExplicitAttributeVerification());

        switch (attributeKind) {
            case CERTIFIED -> {
                attributesSeed.getCertified().get(targetGroupIndex).add(attributeSeed);
            }
            case DECLARED -> {
                attributesSeed.getDeclared().get(targetGroupIndex).add(attributeSeed);
            }
            case VERIFIED -> {
                attributesSeed.getVerified().get(targetGroupIndex).add(attributeSeed);
            }
        }

        eServiceDescriptorUtils.updateEServiceDescriptor(eServiceDescriptor, attributesSeed);
    }

    @Given("l'utente tenta di aggiungere una soglia differenziata di {int} per l'attributo {attributeKind} {int}-esimo e il gruppo {int}-esimo creato")
    public void addThresholdToCertifiedAttribute(int dailyCallsPerConsumer, AttributeKind attributeKind, int attributeIndex, int groupIndex) {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        ProducerEServiceDescriptor eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        List<List<DescriptorAttribute>> existingAttributeGroups = switch (attributeKind) {
            case CERTIFIED -> eServiceDescriptor.getAttributes().getCertified();
            case DECLARED -> eServiceDescriptor.getAttributes().getDeclared();
            case VERIFIED -> eServiceDescriptor.getAttributes().getVerified();
            default -> throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeKind));
        };

        UUID attributeId = getAttributeIdFromRequiredAttributes(attributeKind, groupIndex, attributeIndex);
        DescriptorAttribute attr = existingAttributeGroups.get(groupIndex).stream()
                .filter(a -> a.getId().equals(attributeId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Attributo %s non trovato nel gruppo %d", attributeId, groupIndex)));
        attr.setDailyCallsPerConsumer(dailyCallsPerConsumer);

        List<List<DescriptorAttributeSeed>> certifiedAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified());
        List<List<DescriptorAttributeSeed>> declaredAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared());
        List<List<DescriptorAttributeSeed>> verifiedAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified());

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
                .certified(certifiedAttributesSeed)
                .declared(declaredAttributesSeed)
                .verified(verifiedAttributesSeed);

        eServiceDescriptorUtils.updateEServiceDescriptor(eServiceDescriptor, attributesSeed);

        Optional<DescriptorAttribute> updatedAttr = eServiceDescriptorUtils.getDescriptorCertifiedAttribute(eServiceId, descriptorId, attr.getId(), groupIndex);

        Assertions.assertTrue(updatedAttr.isPresent());
        Assertions.assertEquals(attr.getId(), updatedAttr.get().getId());
    }

    @Given("i residui relativi alle dailyCalls associati alla finalità sono pari a:")
    public void checkRemainingDailyCalls(DataTable table) throws InterruptedException {

        Map<String, String> expectedData = table.asMap(String.class, String.class);
        int expectedRemainingDailyCallsPerConsumer = Integer.parseInt(expectedData.get("remainingDailyCallsPerConsumer"));
        int expectedRemainingDailyCallsTotals = Integer.parseInt(expectedData.get("remainingDailyCallsTotal"));

        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID();

        // Sleep richiesto poiché l'aggiornamento è asincrono e attendiamo che i dati siano stabili.
        // Il polling non è utilizzabile in quanto non è possibile definire una condizione di uscita affidabile.
        this.delayService.delay();
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getRemainingDailyCalls(purposeId)
        );
        RemainingDailyCallsResponse response = (RemainingDailyCallsResponse) sharedStepsContext.getHttpCallExecutor().getResponse();

        Assertions.assertEquals(expectedRemainingDailyCallsPerConsumer, response.getRemainingDailyCallsPerConsumer());
        Assertions.assertEquals(expectedRemainingDailyCallsTotals, response.getRemainingDailyCallsTotal());
    }

    @Given("l'utente cerca di recuperare le soglie rimanenti per la finalità con ID {string} e si ottiene uno status code {int}")
    public void getRemainingDailyCalls(String purposeId, Integer statusCode) {

        UUID purposeIdAsUUID = this.purposesResolver.resolveOrParse(
                purposeId,
                UUID::fromString,
                () -> this.sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
                null,
                UUID::randomUUID,
                null
        );

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getPurposeApiClient().getRemainingDailyCalls(purposeIdAsUUID)
                ),
                res -> statusCode == null || res.value() == statusCode,
                "Unexpected status code for getRemainingDailyCalls"
        );
    }

    @Given("l'utente {string} possiede almeno un attributo certificato discreto")
    public void hasCertifiedDiscreteAttribute(String tenantType) {
        hasCertifiedDiscreteAttribute(tenantType, true);
    }

    @Given("l'utente {string} non possiede nessun attributo certificato discreto")
    public void hasntCertifiedDiscreteAttribute(String tenantType) {
        hasCertifiedDiscreteAttribute(tenantType, false);
    }

    private void hasCertifiedDiscreteAttribute(String tenantType, boolean hasAttribute) {

        UUID tenantId = identityService.getOrganizationId(tenantType);
        Tenant tenant = clientTokenConfigurator.getTenantsApi().getTenant(tenantId);

        Optional<CertifiedTenantAttribute> discreteAttrOptional = tenant.getAttributes().getCertified()
                .stream()
                .filter(attr -> Objects.equals(attr.getKind().getValue(), AttributeKind.CERTIFIED_DISCRETE.getValue()))
                .findFirst();

        CertifiedDiscreteTenantAttribute discreteAttr = (CertifiedDiscreteTenantAttribute) discreteAttrOptional.orElse(null);

        if (hasAttribute) {
            Assertions.assertNotNull(discreteAttr, "Il tenant non ha nessun attributo certificato discreto");
            Assertions.assertNull(discreteAttr.getRevocationTimestamp(), "L'attributo certificato discreto non deve essere revocato");

            boolean isAttributeAvailable = sharedStepsContext.getAttributeCommonContext()
                    .getAvailableCertifiedDiscreteAttributes()
                    .stream()
                    .anyMatch(attr -> attr.getId().equals(discreteAttr.getId()));
            Assertions.assertTrue(isAttributeAvailable, "L'attributo certificato discreto associato al tenant non è un attributo certificato discreto disponibile");
            log.info("Il tenant {} ha l'attributo certificato discreto {} con ID {} e threshold {}",
                    tenantId,
                    discreteAttr.getName(),
                    discreteAttr.getId(),
                    discreteAttr.getDiscreteValue()
            );
        } else {
            Assertions.assertNull(discreteAttr, "Il tenant ha un attributo certificato discreto");
            log.info("Il tenant {} non ha nessun attributo certificato discreto", tenantId);
        }

        sharedStepsContext.getAttributeCommonContext().setOwnerCertifiedDiscreteAttribute(tenantType);
        sharedStepsContext.getAttributeCommonContext().getOwnedCertifiedDiscreteAttributes().add(discreteAttr);
    }

    private UUID getAttributeIdFromRequiredAttributes(AttributeKind attributeKind, int groupIndex, int attributeIndex) {
        return switch (attributeKind) {
            case CERTIFIED -> sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(groupIndex).get(attributeIndex);
            case DECLARED -> sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes().get(groupIndex).get(attributeIndex);
            case VERIFIED -> sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes().get(groupIndex).get(attributeIndex);
            default -> throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeKind));
        };
    }
}

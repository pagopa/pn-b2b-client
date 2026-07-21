package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Cucumber steps for testing template-instance interface REST and SOAP methods
 * Related to PIN-9920 PST 1.2
 */
@Data
@Slf4j(topic = "EServiceTemplateInstanceInterfaceSteps")
public class EServiceTemplateInstanceInterfaceSteps {

    @FunctionalInterface
    private interface DescriptorVerifier<T> {
        void verify(ProducerEServiceDescriptor descriptor, T expectedSeed, org.assertj.core.api.SoftAssertions softly);
    }

    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IEServiceClient eServiceClient;
    private final IHttpExecutor httpCallExecutor;

    public EServiceTemplateInstanceInterfaceSteps(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente tenta di associare un'interfaccia template instance \"REST\" con:")
    public void addTemplateInstanceRestInterface(TemplateInstanceInterfaceRESTSeed seed) {
        executeTemplateInstanceInterfaceRestCall(
                getActualEServiceIdOrRandom(),
                getActualDescriptorIdOrRandom(),
                seed
        );
    }

    @When("l'utente tenta di associare un'interfaccia template instance \"SOAP\" con:")
    public void addTemplateInstanceSoapInterface(TemplateInstanceInterfaceSOAPSeed seed) {
            executeTemplateInstanceInterfaceSoapCall(
                getActualEServiceIdOrRandom(),
                getActualDescriptorIdOrRandom(),
                seed
        );
    }

    @When("l'utente tenta di associare un'interfaccia template instance \"REST\" senza specifiche")
    public void addTemplateInstanceRestInterfaceWithoutPayload() {
        executeTemplateInstanceInterfaceRestCall(
                getActualEServiceIdOrRandom(),
                getActualDescriptorIdOrRandom(),
                new TemplateInstanceInterfaceRESTSeed()
        );
    }

    @When("l'utente tenta di associare un'interfaccia template instance \"SOAP\" senza specifiche")
    public void addTemplateInstanceSoapInterfaceWithoutPayload() {
        executeTemplateInstanceInterfaceSoapCall(
                getActualEServiceIdOrRandom(),
                getActualDescriptorIdOrRandom(),
                new TemplateInstanceInterfaceSOAPSeed()
        );
    }

    @When("l'utente tenta di associare un'interfaccia template instance \"REST\" con {string} {string} e:")
    public void addTemplateInstanceRestInterfaceWithCustomId(String idField, String idValueToken, TemplateInstanceInterfaceRESTSeed seed) {
        UUID actualEServiceId = getActualEServiceIdOrRandom();
        UUID actualDescriptorId = getActualDescriptorIdOrRandom();

        UUID eServiceId = "eServiceId".equalsIgnoreCase(idField)
                ? resolveIdToken(idValueToken, actualEServiceId)
                : actualEServiceId;

        UUID descriptorId = "descriptorId".equalsIgnoreCase(idField)
                ? resolveIdToken(idValueToken, actualDescriptorId)
                : actualDescriptorId;

        executeTemplateInstanceInterfaceRestCall(eServiceId, descriptorId, seed);
    }

    @When("l'utente tenta di associare un'interfaccia template instance \"SOAP\" con {string} {string} e:")
    public void addTemplateInstanceSoapInterfaceWithCustomId(String idField, String idValueToken, TemplateInstanceInterfaceSOAPSeed seed) {
        UUID actualEServiceId = getActualEServiceIdOrRandom();
        UUID actualDescriptorId = getActualDescriptorIdOrRandom();

        UUID eServiceId = "eServiceId".equalsIgnoreCase(idField)
                ? resolveIdToken(idValueToken, actualEServiceId)
                : actualEServiceId;

        UUID descriptorId = "descriptorId".equalsIgnoreCase(idField)
                ? resolveIdToken(idValueToken, actualDescriptorId)
                : actualDescriptorId;

        executeTemplateInstanceInterfaceSoapCall(eServiceId, descriptorId, seed);
    }

    private void executeTemplateInstanceInterfaceRestCall(UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceRESTSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
                () -> eServiceClient.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
                        eServiceId, descriptorId, seed),
                ResponseEntity::getStatusCode
        );
    }

    private void executeTemplateInstanceInterfaceSoapCall(UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceSOAPSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
                () -> eServiceClient.addEServiceTemplateInstanceInterfaceSoapWithHttpInfo(
                        eServiceId, descriptorId, seed),
                ResponseEntity::getStatusCode
        );
    }

    @Then("l'interfaccia template instance \"REST\" è stata registrata correttamente con i valori:")
    public void verifyRestTemplateInstanceInterface(TemplateInstanceInterfaceRESTSeed expectedSeed) {
        verifyTemplateInstanceInterface(
                expectedSeed,
                desc -> desc != null
                        && desc.getTemplateRef() != null
                        && desc.getTemplateRef().getInterfaceMetadata() != null,
                this::verifyRestInterfaceFields
        );
    }

    @Then("l'interfaccia template instance \"SOAP\" è stata registrata correttamente con i valori:")
    public void verifySoapTemplateInstanceInterface(TemplateInstanceInterfaceSOAPSeed expectedSeed) {
        verifyTemplateInstanceInterface(
                expectedSeed,
                desc -> desc != null && desc.getServerUrls() != null,
                this::verifySoapInterfaceFields
        );
    }

    private <T> void verifyTemplateInstanceInterface(
            T expectedSeed,
            Predicate<ProducerEServiceDescriptor> pollingCondition,
            DescriptorVerifier<T> verifier
    ) {
        ResponseEntity<?> response = (ResponseEntity<?>) httpCallExecutor.getResponse();

        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().is2xxSuccessful())
                    .as("Lo status code della risposta deve essere 2xx")
                    .isTrue();

            if (response.getBody() instanceof CreatedResource) {
                UUID descriptorId = getDescriptorId();
                UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceIdCreatedFromTemplate();

                PollingService pollingService = sharedStepsContext.getPollingService();
                ProducerEServiceDescriptor descriptor = pollingService.makePolling(
                        () -> eServiceClient.getEServiceDescriptor(eServiceId, descriptorId),
                        pollingCondition,
                        "Informazioni di interfaccia non trovate"
                );

                verifier.verify(descriptor, expectedSeed, softly);
            }
        });
    }

    @Nonnull
    private UUID getDescriptorId() {
        CreatedEServiceDescriptor lastEServiceCreatedFromTemplate = Objects.requireNonNull(
                sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate(),
                "Non risulta un e-service template, il quale però precondizione per l'esecuzione dello step. Verificare che negli step precedenti questo sia stato correttamente creato e aggiunto in contesto.");
        return lastEServiceCreatedFromTemplate.getDescriptorId();
    }

    private UUID getActualEServiceIdOrRandom() {
        UUID current = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        return current != null ? current : UUID.randomUUID();
    }

    private UUID getActualDescriptorIdOrRandom() {
        CreatedEServiceDescriptor lastEServiceCreatedFromTemplate = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate();
        return lastEServiceCreatedFromTemplate != null && lastEServiceCreatedFromTemplate.getDescriptorId() != null
                ? lastEServiceCreatedFromTemplate.getDescriptorId()
                : UUID.randomUUID();
    }

    private UUID resolveIdToken(String idValueToken, UUID actualId) {
        String normalizedToken = StepParser.nullOrBlankOrValue(idValueToken);
        ResolvableToken token = ResolvableToken.from(normalizedToken);

        if (normalizedToken == null || token == ResolvableToken.ACTUAL) {
            return actualId;
        }

        UUID parsed = StepParser.uuidOrRandomOrNull(normalizedToken);
        if (parsed != null) {
            return parsed;
        }

        throw new IllegalArgumentException("Valore ID non valido: " + idValueToken + ". Usa %actual, %random o UUID valido");
    }

    /**
     * Verifies REST interface fields in descriptor match expected values
     */
    private void verifyRestInterfaceFields(ProducerEServiceDescriptor descriptor,
                                          TemplateInstanceInterfaceRESTSeed expectedSeed,
                                          org.assertj.core.api.SoftAssertions softly) {
        softly.assertThat(descriptor).isNotNull();
        softly.assertThat(expectedSeed).isNotNull();

        if (descriptor == null || expectedSeed == null) {
            return;
        }

        EServiceTemplateRef templateRef = descriptor.getTemplateRef();
        softly.assertThat(templateRef)
                .as("templateRef del descriptor valorizzato")
                .isNotNull();

        TemplateInstanceInterfaceMetadata actualMetadata = templateRef != null
                ? templateRef.getInterfaceMetadata()
                : null;

        softly.assertThat(actualMetadata)
                .as("templateRef.interfaceMetadata valorizzato")
                .isNotNull();

        if (actualMetadata != null) {
            softly.assertThat(actualMetadata.getContactName())
                    .as("contactName")
                    .isEqualTo(expectedSeed.getContactName());
            softly.assertThat(actualMetadata.getContactEmail())
                    .as("contactEmail")
                    .isEqualTo(expectedSeed.getContactEmail());
            softly.assertThat(actualMetadata.getContactUrl())
                    .as("contactUrl")
                    .isEqualTo(expectedSeed.getContactUrl());
            softly.assertThat(actualMetadata.getTermsAndConditionsUrl())
                    .as("termsAndConditionsUrl")
                    .isEqualTo(expectedSeed.getTermsAndConditionsUrl());
        }

        List<TemplateInstanceInterfaceServerUrlSeed> expectedServerUrls = expectedSeed.getServerUrls();
        List<ProducerEServiceDescriptorServerUrlsInner> actualServerUrls = descriptor.getServerUrls();

        softly.assertThat(actualServerUrls)
                .as("serverUrls presenti nel descriptor")
                .isNotNull();

        if (actualServerUrls != null && expectedServerUrls != null) {
            softly.assertThat(actualServerUrls)
                    .as("numero serverUrls")
                    .hasSameSizeAs(expectedServerUrls);

            int itemsToCheck = Math.min(actualServerUrls.size(), expectedServerUrls.size());
            for (int i = 0; i < itemsToCheck; i++) {
                TemplateInstanceInterfaceServerUrlSeed expectedUrl = expectedServerUrls.get(i);
                ProducerEServiceDescriptorServerUrlsInner actualUrl = actualServerUrls.get(i);

                softly.assertThat(actualUrl.getUrl())
                        .as("serverUrls[%d].url", i)
                        .isEqualTo(expectedUrl.getUrl() != null ? expectedUrl.getUrl().toString() : null);
                softly.assertThat(actualUrl.getDescription())
                        .as("serverUrls[%d].description", i)
                        .isEqualTo(expectedUrl.getDescription());
            }
        }
    }

    /**
     * Verifies SOAP interface fields in descriptor match expected values.
     * SOAP seed contains only serverUrls (no contact metadata fields like REST).
     */
    private void verifySoapInterfaceFields(ProducerEServiceDescriptor descriptor,
                                          TemplateInstanceInterfaceSOAPSeed expectedSeed,
                                          org.assertj.core.api.SoftAssertions softly) {
        softly.assertThat(descriptor).isNotNull();
        softly.assertThat(expectedSeed).isNotNull();

        if (descriptor == null || expectedSeed == null) {
            return;
        }

        List<TemplateInstanceInterfaceServerUrlSeed> expectedServerUrls = expectedSeed.getServerUrls();
        List<ProducerEServiceDescriptorServerUrlsInner> actualServerUrls = descriptor.getServerUrls();

        softly.assertThat(actualServerUrls)
                .as("serverUrls presenti nel descriptor")
                .isNotNull();

        if (actualServerUrls != null && expectedServerUrls != null) {
            softly.assertThat(actualServerUrls)
                    .as("numero serverUrls")
                    .hasSameSizeAs(expectedServerUrls);

            int itemsToCheck = Math.min(actualServerUrls.size(), expectedServerUrls.size());
            for (int i = 0; i < itemsToCheck; i++) {
                TemplateInstanceInterfaceServerUrlSeed expectedUrl = expectedServerUrls.get(i);
                ProducerEServiceDescriptorServerUrlsInner actualUrl = actualServerUrls.get(i);

                softly.assertThat(actualUrl.getUrl())
                        .as("serverUrls[%d].url", i)
                        .isEqualTo(expectedUrl.getUrl() != null ? expectedUrl.getUrl().toString() : null);
                softly.assertThat(actualUrl.getDescription())
                        .as("serverUrls[%d].description", i)
                        .isEqualTo(expectedUrl.getDescription());
            }
        }
    }
}

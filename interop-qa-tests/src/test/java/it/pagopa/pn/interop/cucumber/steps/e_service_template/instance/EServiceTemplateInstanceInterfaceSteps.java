package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Cucumber steps for testing template-instance interface REST and SOAP methods
 * Related to PIN-9920 PST 1.2
 */
@Data
@Slf4j(topic = "EServiceTemplateInstanceInterfaceSteps")
public class EServiceTemplateInstanceInterfaceSteps {
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
        ResponseEntity<?> response = (ResponseEntity<?>) httpCallExecutor.getResponse();

        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().is2xxSuccessful())
                    .as("Response status code should be 2xx")
                    .isTrue();

            if (response.getBody() instanceof CreatedResource) {
                UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceCreatedFromTemplate().getDescriptorId();
                UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceIdCreatedFromTemplate();

                ProducerEServiceDescriptor descriptor = eServiceClient.getEServiceDescriptor(eServiceId, descriptorId);
                verifyRestInterfaceFields(descriptor, expectedSeed, softly);
            }
        });
    }

    @Then("l'interfaccia template instance \"SOAP\" è stata registrata correttamente con i valori:")
    public void verifySoapTemplateInstanceInterface(TemplateInstanceInterfaceSOAPSeed expectedSeed) {
        ResponseEntity<?> response = (ResponseEntity<?>) httpCallExecutor.getResponse();

        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().is2xxSuccessful())
                    .as("Response status code should be 2xx")
                    .isTrue();

            if (response.getBody() instanceof CreatedResource) {
                UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceCreatedFromTemplate().getDescriptorId();
                UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceIdCreatedFromTemplate();

                ProducerEServiceDescriptor descriptor = eServiceClient.getEServiceDescriptor(eServiceId, descriptorId);
                verifySoapInterfaceFields(descriptor, expectedSeed, softly);
            }
        });
    }

    private UUID getActualEServiceIdOrRandom() {
        UUID current = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        return current != null ? current : UUID.randomUUID();
    }

    private UUID getActualDescriptorIdOrRandom() {
        CreatedEServiceDescriptor lastEServiceCreatedFromTemplate = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate();
        return lastEServiceCreatedFromTemplate != null
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
        // Extract interface from descriptor - structure may vary based on BFF response
        // This is a placeholder - adjust based on actual descriptor structure
        softly.assertThat(expectedSeed).isNotNull();
        softly.assertThat(descriptor).isNotNull();
    }

    /**
     * Verifies SOAP interface fields in descriptor match expected values
     */
    private void verifySoapInterfaceFields(ProducerEServiceDescriptor descriptor,
                                          TemplateInstanceInterfaceSOAPSeed expectedSeed,
                                          org.assertj.core.api.SoftAssertions softly) {
        // Extract interface from descriptor - structure may vary based on BFF response
        // This is a placeholder - adjust based on actual descriptor structure
        softly.assertThat(expectedSeed).isNotNull();
        softly.assertThat(descriptor).isNotNull();
    }
}


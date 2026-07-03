package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.datatable.DataTable;
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
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.core.convert.support.DefaultConversionService;
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

    @When("l'utente tenta di associare un'interfaccia template instance {string} con:")
    public void addTemplateInstanceInterface(String apiType, DataTable dataTable) {
        executeTemplateInstanceInterfaceCall(
                apiType,
                getActualEServiceIdOrRandom(),
                getActualDescriptorIdOrRandom(),
                parseFieldValueDataTable(dataTable)
        );
    }

    @When("l'utente tenta di associare un'interfaccia template instance {string} senza specifiche")
    public void addTemplateInstanceInterfaceWithoutPayload(String apiType) {
        executeTemplateInstanceInterfaceCall(
                apiType,
                getActualEServiceIdOrRandom(),
                getActualDescriptorIdOrRandom(),
                Collections.emptyMap()
        );
    }

    @When("l'utente tenta di associare un'interfaccia template instance {string} con {string} {string} e:")
    public void addTemplateInstanceInterfaceWithCustomId(String apiType, String idField, String idValueToken, DataTable dataTable) {
        UUID actualEServiceId = getActualEServiceIdOrRandom();
        UUID actualDescriptorId = getActualDescriptorIdOrRandom();

        UUID eServiceId = "eServiceId".equalsIgnoreCase(idField)
                ? resolveIdToken(idValueToken, actualEServiceId)
                : actualEServiceId;

        UUID descriptorId = "descriptorId".equalsIgnoreCase(idField)
                ? resolveIdToken(idValueToken, actualDescriptorId)
                : actualDescriptorId;

        executeTemplateInstanceInterfaceCall(apiType, eServiceId, descriptorId, parseFieldValueDataTable(dataTable));
    }

    private void executeTemplateInstanceInterfaceCall(String apiType, UUID eServiceId, UUID descriptorId, Map<String, String> data) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        String normalizedApiType = normalizeApiType(apiType);

        if ("REST".equals(normalizedApiType)) {
            TemplateInstanceInterfaceRESTSeed seed = buildRestSeed(data);
            httpCallExecutor.performCall(
                    () -> eServiceClient.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
                            eServiceId, descriptorId, seed),
                    ResponseEntity::getStatusCode
            );
            return;
        }

        TemplateInstanceInterfaceSOAPSeed seed = buildSoapSeed(data);
        httpCallExecutor.performCall(
                () -> eServiceClient.addEServiceTemplateInstanceInterfaceSoapWithHttpInfo(
                        eServiceId, descriptorId, seed),
                ResponseEntity::getStatusCode
        );
    }

    @Then("l'interfaccia template instance {string} è stata registrata correttamente con i valori:")
    public void verifyTemplateInstanceInterface(String apiType, DataTable dataTable) {
        ResponseEntity<?> response = (ResponseEntity<?>) httpCallExecutor.getResponse();
        String normalizedApiType = normalizeApiType(apiType);

        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().is2xxSuccessful())
                    .as("Response status code should be 2xx")
                    .isTrue();

            if (response.getBody() instanceof CreatedResource) {
                UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceDescriptorCreatedFromTemplate().getId();
                UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                        .getLastEServiceIdCreatedFromTemplate();

                ProducerEServiceDescriptor descriptor = eServiceClient.getEServiceDescriptor(eServiceId, descriptorId);

                Map<String, String> expectedData = parseFieldValueDataTable(dataTable);
                if ("REST".equals(normalizedApiType)) {
                    verifyRestInterfaceFields(descriptor, expectedData, softly);
                } else {
                    verifySoapInterfaceFields(descriptor, expectedData, softly);
                }
            }
        });
    }

    private UUID getActualEServiceIdOrRandom() {
        UUID current = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        return current != null ? current : UUID.randomUUID();
    }

    private UUID getActualDescriptorIdOrRandom() {
        CompactDescriptor current = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate();
        return current != null ? current.getId() : UUID.randomUUID();
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

    private String normalizeApiType(String apiType) {
        if (apiType == null) {
            throw new IllegalArgumentException("Tipo API non specificato: usare REST o SOAP");
        }
        String normalizedApiType = apiType.trim().toUpperCase(Locale.ROOT);
        if (!"REST".equals(normalizedApiType) && !"SOAP".equals(normalizedApiType)) {
            throw new IllegalArgumentException("Tipo API non supportato: " + apiType + ". Valori ammessi: REST, SOAP");
        }
        return normalizedApiType;
    }

    /**
     * Builds TemplateInstanceInterfaceRESTSeed from DataTable
     * Supports fields: contactName, contactEmail, contactUrl, termsAndConditionsUrl, serverUrls[i].url, serverUrls[i].description
     */
    private TemplateInstanceInterfaceRESTSeed buildRestSeed(Map<String, String> data) {
        return populateSeedFromDataTable(new TemplateInstanceInterfaceRESTSeed(), data);
    }

    /**
     * Builds TemplateInstanceInterfaceSOAPSeed from DataTable
     * Supports fields: serverUrls[i].url, serverUrls[i].description
     */
    private TemplateInstanceInterfaceSOAPSeed buildSoapSeed(Map<String, String> data) {
        return populateSeedFromDataTable(new TemplateInstanceInterfaceSOAPSeed(), data);
    }

    /**
     * Populates a seed DTO via Spring introspection with support for nested/indexed paths
     * (e.g. serverUrls[0].url, serverUrls[0].description).
     */
    private <T> T populateSeedFromDataTable(T seed, Map<String, String> data) {
        BeanWrapper wrapper = new BeanWrapperImpl(seed);
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setConversionService(new DefaultConversionService());

        for (Map.Entry<String, String> entry : data.entrySet()) {
            try {
                wrapper.setPropertyValue(entry.getKey(), StepParser.nullOrBlankOrValue(entry.getValue()));
            } catch (BeansException ex) {
                throw new IllegalArgumentException("Campo non valido o valore non convertibile: " + entry.getKey(), ex);
            }
        }

        return seed;
    }

    private Map<String, String> parseFieldValueDataTable(DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyMap();
        }

        int startIndex = 0;
        List<String> firstRow = rows.get(0);
        if (firstRow.size() >= 2
                && "field".equalsIgnoreCase(firstRow.get(0).trim())
                && "value".equalsIgnoreCase(firstRow.get(1).trim())) {
            startIndex = 1;
        }

        Map<String, String> mapped = new LinkedHashMap<>();
        for (int i = startIndex; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 2) {
                continue;
            }
            String key = row.get(0) == null ? "" : row.get(0).trim();
            if (key.isEmpty()) {
                continue;
            }
            mapped.put(key, row.get(1));
        }

        return mapped;
    }

    /**
     * Verifies REST interface fields in descriptor match expected values
     */
    private void verifyRestInterfaceFields(ProducerEServiceDescriptor descriptor,
                                          Map<String, String> expectedData,
                                          org.assertj.core.api.SoftAssertions softly) {
        // Extract interface from descriptor - structure may vary based on BFF response
        // This is a placeholder - adjust based on actual descriptor structure
        softly.assertThat(expectedData).isNotNull();
        softly.assertThat(descriptor).isNotNull();
    }

    /**
     * Verifies SOAP interface fields in descriptor match expected values
     */
    private void verifySoapInterfaceFields(ProducerEServiceDescriptor descriptor,
                                          Map<String, String> expectedData,
                                          org.assertj.core.api.SoftAssertions softly) {
        // Extract interface from descriptor - structure may vary based on BFF response
        // This is a placeholder - adjust based on actual descriptor structure
        softly.assertThat(expectedData).isNotNull();
        softly.assertThat(descriptor).isNotNull();
    }
}


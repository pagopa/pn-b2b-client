package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplates;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.interop.purpose.service.impl.PurposeTemplateClientImpl;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.assistant.PurposeTemplatePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.ParameterTypesInterop.ResourceState;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils.PurposeTemplateResolver;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import javax.annotation.Nonnull;
import java.io.File;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PurposeTemplateSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;

    private final IPurposeTemplateClient purposeTemplateClient;

    private final IPurposeApiClient purposeApiClient;

    private final BlobFileCreator blobFileCreator;

    private final SharedStepsContext sharedStepsContext;

    private final IHttpExecutor httpCallExecutor;

    private final PollingService pollingService;

    private final PurposeTemplatePatchOperationsAssistant patchAssistant;

    private final BFFDataPreparationService dataPreparationService;

    private PurposeTemplateSeed purposeTemplateCreationRequest;

    private CreatedResource createdPurposeTemplate;

    private CreatedResource createdPurposeFromPurposeTemplate;

    private PurposeTemplateWithCompactCreator purposeTemplateWithCompactCreator;

    private CatalogPurposeTemplates catalogPurposeTemplates;

    private PurposeTemplate purposeTemplate;

    private Purpose purpose;

    private RiskAnalysisTemplateAnswerResponse riskAnalysis;

    private RiskAnalysisTemplateAnswerAnnotation annotation;

    private RiskAnalysisTemplateAnswerAnnotationDocument uploadedDocument;

    private File retrievedDocument;

    private List<CompactPurposeTemplateEService> linkedEServices;

    private PurposeTemplateContext purposeTemplateContext;

    private PurposeTemplateResolver resolver;

    /**
     * Quando voglio simulare una casistica di titolo duplicato, la prima volta ne creo uno (con timestamp) e lo setto qua.
     * La seconda volta, quando questa variabile non è più null, ri-applico lo stesso titolo.
     */
    private String duplicatedTitleForPurpose;

    public PurposeTemplateSteps(SharedStepsContext sharedStepsContext,
                                ClientTokenConfigurator clientTokenConfigurator,
                                BlobFileCreator blobFileCreator,
                                PurposeTemplatePatchOperationsAssistant patchAssistant,
                                BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.blobFileCreator = blobFileCreator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();
        ((PurposeTemplateClientImpl) this.purposeTemplateClient).setHttpCallExecutor(this.httpCallExecutor);
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        this.purposeTemplateContext = new PurposeTemplateContext();
        this.resolver = new PurposeTemplateResolver(sharedStepsContext, purposeTemplateContext, sharedStepsContext.getIdentityService());
        this.patchAssistant = patchAssistant;
        this.dataPreparationService = dataPreparationService;
    }

    @AllArgsConstructor
    @Getter
    public enum PurposeTemplateErrorTypes {

        ANSWER_OVER_250("ANSWER OVER 250"),
        NO_PURPOSE_ANSWER("NO PURPOSE ANSWER"),
        NO_PERSONAL_DATA_ANSWER("NO PERSONAL DATA ANSWER"),
        PERSONAL_DATA_CONFLICT("PERSONAL DATA CONFLICT"),
        UPDATE_WITH_EXISTING_TITLE("UPDATE WITH EXISTING TITLE");

        private final String value;
    }

    @When("viene creato un nuovo purpose template")
    public void createPurposeTemplate() {
        prepareCreationRequest(false);
        invokeCreatePurposeTemplate();
    }

    @When("viene creato un nuovo purpose template coerente con la tipologia dell'ente")
    public void createPurposeTemplateWithTenantKindCoherentWithTenantType() {
        TargetTenantKind targetTenantKind = resolveTargetTenantKindFromContextTenantType();
        prepareCreationRequest(false, targetTenantKind);
        invokeCreatePurposeTemplate();
    }

    //Il seguente metodo crea un purpose template in stato draft
    @When("viene creato un nuovo purpose template con handlePersonalData {bool}")
    public void createPurposeTemplateWithHandlePersonalData(Boolean handlePersonalDataValue) {
        prepareCreationRequest(handlePersonalDataValue);
        invokeCreatePurposeTemplate();
    }

    @When("viene creato un nuovo purpose template con errore di tipo {purposeTemplateError}")
    public void createPurposeTemplateWithError(PurposeTemplateErrorTypes error) {
        prepareCreationRequest(false);
        insertErrorsOnPurpose(error);
        invokeCreatePurposeTemplate();
    }

    private void insertErrorsOnPurpose(PurposeTemplateErrorTypes error) {
        switch (error) {
            case PERSONAL_DATA_CONFLICT ->
                    purposeTemplateCreationRequest.setHandlesPersonalData(!purposeTemplateCreationRequest.getHandlesPersonalData());
            case NO_PERSONAL_DATA_ANSWER ->
                    purposeTemplateCreationRequest.getPurposeRiskAnalysisForm().getAnswers().remove("usesPersonalData");
            case NO_PURPOSE_ANSWER ->
                    purposeTemplateCreationRequest.getPurposeRiskAnalysisForm().getAnswers().remove("purpose");
            case ANSWER_OVER_250 ->
                    purposeTemplateCreationRequest.getPurposeRiskAnalysisForm().getAnswers().put("institutionalPurpose",
                            new RiskAnalysisTemplateAnswerSeed().editable(true).suggestedValues(
                                    Arrays.asList("Answer 1", "A".repeat(251))));
            case UPDATE_WITH_EXISTING_TITLE -> {
                httpCallExecutor.performCall(() -> purposeTemplateClient.getCreatorPurposeTemplates(0, 10, null, null, null));
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).isTrue();
                CreatorPurposeTemplates list = (CreatorPurposeTemplates) httpCallExecutor.getResponse();
                assertThat(list).as("Il risultato della get by creator non dev'essere null");
                assertThat(list.getResults()).as("I results della lista non devono essere null").isNotNull();
                CreatorPurposeTemplate previouslyCreatedPurposeTemplate = list.getResults().stream().filter(x ->
                        !x.getPurposeTitle().equals(purposeTemplateCreationRequest.getPurposeTitle())).findFirst().orElse(null);
                assertThat(previouslyCreatedPurposeTemplate).as("Nessun purpose template creato in precedenza con titolo diverso da quello attuale trovato").isNotNull();
                purposeTemplateCreationRequest.setPurposeTitle(previouslyCreatedPurposeTemplate.getPurposeTitle());
            }
        }
    }

    private PurposeTemplateSeed prepareCreationRequest(Boolean handlePersonalDataValue) {
        return prepareCreationRequest(handlePersonalDataValue, TargetTenantKind.PA);
    }

    private TargetTenantKind resolveTargetTenantKindFromContextTenantType() {
        String tenantType = sharedStepsContext.getTenantType();
        if (isNull(tenantType)) {
            throw new IllegalStateException("Tenant type assente nello SharedStepsContext");
        }

        String tenantKind = sharedStepsContext.getIdentityService().getKind(tenantType);
        try {
            return "PA".equals(tenantKind) ? TargetTenantKind.PA : TargetTenantKind.PRIVATE;
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Tenant kind '%s' non supportato per tenantType '%s'"
                    .formatted(tenantKind, tenantType), ex);
        }
    }

    private PurposeTemplateSeed prepareCreationRequest(Boolean handlePersonalDataValue, TargetTenantKind targetTenantKind) {
        purposeTemplateCreationRequest = new PurposeTemplateSeed();
        purposeTemplateCreationRequest.setPurposeTitle("purposeTitle" + DateTime.now());
        purposeTemplateCreationRequest.setPurposeDescription("purposeDescription_CREATE");
        purposeTemplateCreationRequest.setTargetDescription("targetDescription_CREATE");
        purposeTemplateCreationRequest.setTargetTenantKind(targetTenantKind);
        purposeTemplateCreationRequest.setPurposeIsFreeOfCharge(true);
        purposeTemplateCreationRequest.setPurposeDailyCalls(10);

        purposeTemplateCreationRequest.setHandlesPersonalData(handlePersonalDataValue);
        purposeTemplateCreationRequest.setPurposeFreeOfChargeReason("Sono una Pubblica Amministrazione");

        if (handlePersonalDataValue != null) {
            RiskAnalysisFormTemplateSeed riskAnalysisForm = new RiskAnalysisFormTemplateSeed()
                    .version(getPurposeTemplateVersion())
                    .answers(getRiskAnalysysTemplateFormAnswerMap(purposeTemplateCreationRequest.getHandlesPersonalData()));
            purposeTemplateCreationRequest.setPurposeRiskAnalysisForm(riskAnalysisForm);
        }
        return purposeTemplateCreationRequest;
    }

    @Nonnull
    private String getPurposeTemplateVersion() {
        // L'inclusione dei valori null è volta a favorire retrocompatibilità con il comportamento antecedente
        // a questa aggiunta, che considerava "3.1" come versione hardcoded.
        String tenant = sharedStepsContext.getTenantType();
        String tenantKind = isNull(tenant) ? null : sharedStepsContext.getIdentityService().getKind(tenant);
        return isNull(tenantKind) || "PA".equals(tenantKind) ? "3.1" : "2.0";
    }

    private void invokeCreatePurposeTemplate() {
        httpCallExecutor.performCall(() -> purposeTemplateClient.createPurposeTemplate(purposeTemplateCreationRequest));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            createdPurposeTemplate = (CreatedResource) httpCallExecutor.getResponse();
            sharedStepsContext.getPurposeTemplateContext().setPurposeTemplateId(createdPurposeTemplate.getId());
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(createdPurposeTemplate.getId())),
                HttpStatus::is2xxSuccessful,
                "Failed to retrieve the purpose template from client!"
            );
        }
    }

    private Map<String, RiskAnalysisTemplateAnswerSeed> getRiskAnalysysTemplateFormAnswerMap(boolean handlePersonalData) {

        Map<String, RiskAnalysisTemplateAnswerSeed> answersMap = new HashMap<>();

        RiskAnalysisTemplateAnswerSeed answerPurpose = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("INSTITUTIONAL"));
        RiskAnalysisTemplateAnswerSeed answerInstitutionalPurpose = new RiskAnalysisTemplateAnswerSeed().editable(false).suggestedValues(Arrays.asList("Answer1", "Answer2"));
        RiskAnalysisTemplateAnswerSeed answerPersonalData = new RiskAnalysisTemplateAnswerSeed().editable(false).values(handlePersonalData ? List.of("YES") : List.of("NO"));
        RiskAnalysisTemplateAnswerSeed answerThirdParties = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("NO"));
        RiskAnalysisTemplateAnswerSeed answerThirdPartiesPersonalData = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("NO"));

        answersMap.put("purpose", answerPurpose);
        answersMap.put("institutionalPurpose", answerInstitutionalPurpose);
        answersMap.put("usesPersonalData", answerPersonalData);

        String tenant = sharedStepsContext.getTenantType();
        String tenantKind = sharedStepsContext.getIdentityService().getKind(tenant);

        if ("PA".equals(tenantKind)) {
            answersMap.put("isRequestOnBehalfOfThirdParties", answerThirdParties);
        }

        answersMap.put("usesThirdPartyPersonalData", answerThirdPartiesPersonalData);

        if (handlePersonalData) {
            RiskAnalysisTemplateAnswerSeed answerCheckedExistenceMereCorrectnessInteropCatalogue = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("true"));
            RiskAnalysisTemplateAnswerSeed answerDoneDpia = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("NO"));
            RiskAnalysisTemplateAnswerSeed answerConfirmPricipleIntegrityAndDiscretion = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("true"));
            RiskAnalysisTemplateAnswerSeed answerPolicyProvided = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("YES"));
            RiskAnalysisTemplateAnswerSeed answerDataDownload = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("NO"));
            RiskAnalysisTemplateAnswerSeed answerPolicyProvidedMedium = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("PRINT"));
            RiskAnalysisTemplateAnswerSeed answerDeliveryMethod = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("CLEARTEXT"));
            RiskAnalysisTemplateAnswerSeed answerDeclarationConfirmGDPR = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("true"));
            RiskAnalysisTemplateAnswerSeed answerPurposePursuit = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("MERE_CORRECTNESS"));
            RiskAnalysisTemplateAnswerSeed answerKnowsDataQuantity = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("NO"));
            RiskAnalysisTemplateAnswerSeed answerLegalBasis = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("CONSENT"));
            RiskAnalysisTemplateAnswerSeed answerPersonalDataTypes = new RiskAnalysisTemplateAnswerSeed().editable(false).values(List.of("WITH_NON_IDENTIFYING_DATA"));

            answersMap.put("checkedExistenceMereCorrectnessInteropCatalogue", answerCheckedExistenceMereCorrectnessInteropCatalogue);
            answersMap.put("doneDpia", answerDoneDpia);
            answersMap.put("confirmPricipleIntegrityAndDiscretion", answerConfirmPricipleIntegrityAndDiscretion);
            answersMap.put("policyProvided", answerPolicyProvided);
            answersMap.put("dataDownload", answerDataDownload);
            answersMap.put("policyProvidedMedium", answerPolicyProvidedMedium);
            answersMap.put("deliveryMethod", answerDeliveryMethod);
            answersMap.put("declarationConfirmGDPR", answerDeclarationConfirmGDPR);
            answersMap.put("purposePursuit", answerPurposePursuit);
            answersMap.put("knowsDataQuantity", answerKnowsDataQuantity);
            answersMap.put("legalBasis", answerLegalBasis);
            answersMap.put("personalDataTypes", answerPersonalDataTypes);

            answersMap.remove("usesThirdPartyPersonalData");
        }
        return answersMap;
    }

    @When("si effettua la get del purpose template {exists}")
    public void getPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        getPurposeTemplateById(ptId, exists);
    }

    @When("si effettua la get del purpose template")
    public void getPurposeTemplate() {
        boolean exists = createdPurposeTemplate.getId() != null;
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId));
    }

    @When("si effettua la get by creator di tutti i purpose template in stato {string}")
    public void getAllPurposeTemplatesByCreator(String status) {
        List<PurposeTemplateState> state;
        switch (status.toUpperCase()) {
            case "ANY" -> state = null;
            case "ACTIVE" -> state = List.of(PurposeTemplateState.PUBLISHED);
            case "DRAFT" -> state = List.of(PurposeTemplateState.DRAFT);
            case "SUSPENDED" -> state = List.of(PurposeTemplateState.SUSPENDED);
            case "ARCHIVED" -> state = List.of(PurposeTemplateState.ARCHIVED);
            default -> throw new IllegalArgumentException("Invalid PurposeTemplateState: " + status);
        }
        httpCallExecutor.performCall(() -> purposeTemplateClient.getCreatorPurposeTemplates(0, 10, null, null, state));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            CreatorPurposeTemplates creatorPurposeTemplates = (CreatorPurposeTemplates) httpCallExecutor.getResponse();
            assertThat(creatorPurposeTemplates).as("Il risultato della get dei purpose template by Creator" + createdPurposeTemplate.getId() + " non dev'essere null").isNotNull();
        }
    }

    @When("si effettua la get di tutti i purpose template con titolo {string}")
    public void getCatalogPurposeTemplate(String title) {
        getCatalogPurposeTemplateWithPersonalData(title, true);
    }

    @When("si effettua la get di tutti i purpose template con titolo {string} e handlePersonalData {bool}")
    public void getCatalogPurposeTemplateWithPersonalData(String title, Boolean handlePersonalData) {
        String titleFilter = title.equalsIgnoreCase("ANY") ? null : title;
        httpCallExecutor.performCall(() -> purposeTemplateClient.getCatalogPurposeTemplates(0, 10, titleFilter, null, null, null, true, handlePersonalData));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            catalogPurposeTemplates = (CatalogPurposeTemplates) httpCallExecutor.getResponse();
        }
    }

    @When("si aggiorna il purpose template {isVisible}")
    public void updatePurposeTemplateRequest(ResourceState resourceState) {
        purposeTemplateCreationRequest.setPurposeTitle("updated_purposeTitle_" + DateTime.now());
        purposeTemplateCreationRequest.setPurposeDescription("updated_purposeDescription_" + DateTime.now());
        purposeTemplateCreationRequest.setTargetDescription("updated_targetDescription_" + DateTime.now());
        purposeTemplateCreationRequest.setPurposeFreeOfChargeReason("updated_purposeFreeOfChargeReason_" + DateTime.now());
        purposeTemplateCreationRequest.setTargetTenantKind(TargetTenantKind.PA);
        invokeUpdatePurposeTemplate(resourceState);
    }

    @When("si aggiorna il purpose template {exists} con errore di tipo {purposeTemplateError}")
    public void updatePurposeTemplateWithError(boolean exists, PurposeTemplateErrorTypes error) {
        insertErrorsOnPurpose(error);
        invokeUpdatePurposeTemplate(ResourceState.VISIBLE);
    }

    private void invokeUpdatePurposeTemplate(ResourceState resourceState) {
        UUID ptId = resourceState.equals(ResourceState.VISIBLE) || resourceState.equals(ResourceState.NOT_VISIBLE)
                ? createdPurposeTemplate.getId()
                : UUID.randomUUID();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeTemplateClient.updatePurposeTemplate(ptId, purposeTemplateCreationRequest)),
                res -> resourceState.equals(ResourceState.VISIBLE) ? res != HttpStatus.NOT_FOUND : res == HttpStatus.NOT_FOUND,
                "Failed to retrieve the purpose template!"
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il template aggiornato non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getPurposeTitle()).as("Il titolo deve risultare aggiornato").contains("updated_purposeTitle");
        }
    }

    @When("si cancella il purpose template {exists}")
    public void deletePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.deletePurposeTemplate(ptId));
        if (exists) {
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                purposeTemplateWithCompactCreator = getPurposeTemplateById(ptId, false);
                assertThat(purposeTemplateWithCompactCreator).as("Il purpose template non risulta essere stato eliminato").isNull();
            }
        }
    }

    public PurposeTemplateWithCompactCreator getPurposeTemplateById(UUID ptId, boolean exists) {
        if (exists) {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId)),
                    res -> res != HttpStatus.NOT_FOUND,
                    "Failed to retrieve the client!"
            );
            purposeTemplateWithCompactCreator = (PurposeTemplateWithCompactCreator) httpCallExecutor.getResponse();
            assertThat(purposeTemplateWithCompactCreator).as("Il risultato della get del purpose con id" + createdPurposeTemplate.getId() + " non dev'essere null").isNotNull();
            return purposeTemplateWithCompactCreator;
        } else {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId)),
                    res -> res == HttpStatus.NOT_FOUND,
                    "Failed to retrieve the client!"
            );
            return null;
        }
    }

    @And("il purpose template {exists} viene associato all'e-service")
    public void linkPurposeTemplateToEservice(boolean exists) {
        assertThat(sharedStepsContext.getEServicesCommonContext().getEserviceId()).as("L'id dell'eService creato non dev'essere null").isNotNull();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.linkEServiceToPurposeTemplate(ptId, eServiceId));
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            pollingService.makePolling(
                () -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 30, null, null),
                result -> !isEmpty(result.getResults()),
                "Non è stato rilevato alcun e-service associato al purpose template %s".formatted(ptId)
            );
        }
    }

    @Then("si effettua la get degli e-service associati al purpose template {exists}")
    public void getPurposeTemplateEservices(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        if (exists) {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 10, null, null)),
                    res -> !((IPurposeTemplateClient.Resources) httpCallExecutor.getResponse()).getResults().isEmpty(),
                    "Failed to retrieve the client!"
            );
        } else {
            httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 10, null, null));
        }

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            IPurposeTemplateClient.Resources esDescriptorsPt = (IPurposeTemplateClient.Resources) httpCallExecutor.getResponse();
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<LinkableResource> resultList = esDescriptorsPt.getResults();
            linkedEServices = resultList.stream().map(LinkableResource::getEservice).toList();
            checkEServicesList(true);
        }
    }

    @When("la lista di e-service associati {contains} l'e-service atteso")
    public void checkEServicesList(boolean contains) {
        assertThat(sharedStepsContext.getEServicesCommonContext().getEserviceId()).as("L'id dell'eService creato non dev'essere null").isNotNull();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        assertThat(linkedEServices).as("La lista di e-services associati al purpose template non dev'essere null").isNotNull();
        assertThat(linkedEServices).asList().as("La lista di e-services associati al purpose template non dev'essere null").isNotEmpty();

        CompactPurposeTemplateEService found = linkedEServices.stream().filter(x -> x.getId().equals(eServiceId)).findFirst().orElse(null);
        if (contains) {
            assertThat(found).as("La lista di e-services associati dovrebbe contenere l-es con id: " + eServiceId).isNotNull();
        } else {
            assertThat(found).as("La lista di e-services associati non dovrebbe contenere l-es con id: " + eServiceId).isNull();
        }
    }

    @And("il purpose template {exists} viene disassociato dall'e-service")
    public void unlinkPurposeTemplateToEservice(boolean exists) {
        assertThat(sharedStepsContext.getEServicesCommonContext().getEserviceId()).as("L'id dell'eService creato non dev'essere null").isNotNull();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.unlinkEServiceToPurposeTemplate(ptId, eServiceId));
        if (exists) {
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                pollingService.makePolling(
                        () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 10, null, null)),
                        res -> ((IPurposeTemplateClient.Resources) httpCallExecutor.getResponse()).getResults().stream().filter(
                                x -> x.getEservice().getId().equals(eServiceId)).toList().isEmpty(),
                        "Error while checking if the eService is correctly unlinked from purpose template"
                );
            }
        }
    }

    @And("il purpose template {exists} viene spostato in stato {ptState}")
    public void changePurposeTemplateState(boolean exists, PurposeTemplateState ptState) {
        switch (ptState) {
            case PUBLISHED -> activatePurposeTemplate(exists);
            case SUSPENDED -> suspendPurposeTemplate(exists);
            case ARCHIVED -> archivePurposeTemplate(exists);
        }
    }

    @And("il purpose template {exists} viene correttamente spostato in stato {ptState}")
    public void properlyChangePurposeTemplateState(boolean exists, PurposeTemplateState ptState) {
        switch (ptState) {
            case PUBLISHED -> activatePurposeTemplate(exists);
            case SUSPENDED -> {
                activatePurposeTemplate(exists);
                suspendPurposeTemplate(exists);
            }
            case ARCHIVED -> {
                activatePurposeTemplate(exists);
                archivePurposeTemplate(exists);
            }
        }
    }

    /**
     * Come il metodo di sopra, ma esegue il passaggio da uno stato all'altro nell'ordine corretto
     */
    @And("il purpose template viene gradualmente spostato in stato {ptState}")
    public void changePurposeTemplateStateGradually(PurposeTemplateState ptState) {
        switch (ptState) {
            case PUBLISHED -> {
                waitUntilStateIn(PurposeTemplateState.DRAFT);

                activatePurposeTemplate(true);
                waitUntilStateIn(PurposeTemplateState.PUBLISHED);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La pubblicazione non è andata a buon fine").isTrue();
            }
            case SUSPENDED -> {
                waitUntilStateIn(PurposeTemplateState.DRAFT);

                activatePurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La pubblicazione non è andata a buon fine").isTrue();
                waitUntilStateIn(PurposeTemplateState.PUBLISHED);

                suspendPurposeTemplate(true);
                waitUntilStateIn(PurposeTemplateState.SUSPENDED);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La sospensione non è andata a buon fine").isTrue();
            }
            case ARCHIVED -> {
                waitUntilStateIn(PurposeTemplateState.DRAFT);

                activatePurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La pubblicazione non è andata a buon fine").isTrue();
                waitUntilStateIn(PurposeTemplateState.PUBLISHED);

                suspendPurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La sospensione non è andata a buon fine").isTrue();
                waitUntilStateIn(PurposeTemplateState.SUSPENDED);

                archivePurposeTemplate(true);
                waitUntilStateIn(PurposeTemplateState.ARCHIVED);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("L'archiviazione non è andata a buon fine").isTrue();
            }
        }
    }

    private void waitUntilStateIn(@Nonnull PurposeTemplateState ptState) {
        waitUntilStateIn(null, ptState);
    }

    private void waitUntilStateIn(UUID purposeTemplateId, @Nonnull PurposeTemplateState ptState) {
        UUID templateId = isNull(purposeTemplateId)
                ? sharedStepsContext.getPurposeTemplateContext().getPurposeTemplateId()
                : purposeTemplateId;
        pollingService.makePolling(
                () -> purposeTemplateClient.getPurposeTemplateWithHttpInfo(templateId),
                response ->
                        response.getStatusCode().is2xxSuccessful()
                                && nonNull(response.getBody())
                                && ptState.equals(response.getBody().getState()),
                "Non è stato possibile reperire il purpose template '%s' nello stato desiderato '%s'".formatted(templateId, ptState)
        );
    }

    @And("il purpose template {exists} viene riattivato")
    public void reactivateSuspendedPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.unsuspendPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getPollingService().makePolling(
                    () -> getPurposeTemplateById(ptId, exists),
                    res -> res.getState() == PurposeTemplateState.PUBLISHED,
                    "Il purpose template non risulta riattivato"
            );
        }
    }

    private void activatePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.publishPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getPollingService().makePolling(
                    () -> getPurposeTemplateById(ptId, exists),
                    res -> res.getState() == PurposeTemplateState.PUBLISHED,
                    "Il purpose template non risulta attivo"
            );
        }
    }

    private void suspendPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();

        // Come segnalato in https://pagopa.atlassian.net/browse/PIN-9557 al momento c'è un bug nella gestione dell'eventual consistency da parte di prodotto
        // questo impone un polling lato Suite sui cambiamenti di stato. Ci è stata data una finestra temporale (circa 5s) i cui errori 500 si riferisono
        // alla mancata gestione dell'eventual consistency. Se sono presenti errori dopo i 5s allora potrebbero subentrare cause diverse
        try{
            PollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.suspendPurposeTemplate(ptId)),
                    HttpStatus::is2xxSuccessful, // Esce solo se ha successo
                    "Errore durante la sospensione del purpose template",
                    5, 1500 // 5 tentativi ogni 1.5s coprono circa 6 secondi, superando i 5s di soglia
            );
        }catch (PollingPredicateException e){
            log.warn("Errore durante la sospensione del purpose template: {}", httpCallExecutor.getErrorMessage());
        }

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getPollingService().makePolling(
                    () -> getPurposeTemplateById(ptId, exists),
                    res -> res.getState() == PurposeTemplateState.SUSPENDED,
                    "Il purpose template non risulta sospeso"
            );
        }
    }

    private void archivePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();

        try {
            // Come segnalato in https://pagopa.atlassian.net/browse/PIN-9557 al momento c'è un bug nella gestione dell'eventual consistency da parte di prodotto
            // questo impone un polling lato Suite sui cambiamenti di stato. Ci è stata data una finestra temporale (circa 5s) i cui errori 500 si riferisono
            // alla mancata gestione dell'eventual consistency. Se sono presenti errori dopo i 5s allora potrebbero subentrare cause diverse
            PollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.archivePurposeTemplate(ptId)),
                    HttpStatus::is2xxSuccessful, // Esce solo se ha successo
                    "Errore durante l'archiviazione del purpose template",
                    5, 1500 // 5 tentativi ogni 1.5s coprono circa 6 secondi, superando i 5s di soglia
            );
        } catch (PollingPredicateException e) {
            log.warn("Errore durante l'archiviazione del purpose template: {}", httpCallExecutor.getErrorMessage());
        }

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getPollingService().makePolling(
                    () -> getPurposeTemplateById(ptId, exists),
                    res -> res.getState() == PurposeTemplateState.ARCHIVED,
                    "Il purpose template non risulta archiviato"
            );
        }
    }

    @And("viene creato un nuovo purpose template in stato {ptState}")
    public void vieneCreatoUnNuovoPurposeTemplateInStato(PurposeTemplateState ptState) {
        createPurposeTemplate();
        changePurposeTemplateStateGradually(ptState);
    }

    @And("viene creata una risposta di analisi del rischio {string} per il purpose template {exists}")
    public void createRiskAnalysisAnswer(String answerType, boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();

        Map<String, RiskAnalysisTemplateAnswerSeed> answersMap = getRiskAnalysysTemplateFormAnswerMap(false);
        RiskAnalysisTemplateAnswerRequest request = new RiskAnalysisTemplateAnswerRequest();
        switch (answerType.toUpperCase()) {
            case "ENTRO I LIMITI CONSENTITI FREE TEXT" -> {
                String key = "institutionalPurpose";
                RiskAnalysisTemplateAnswerSeed answer = answersMap.get(key);
                answer.setSuggestedValues(
                        Stream.concat(answer.getSuggestedValues().stream(), Stream.of("Y".repeat(250))).toList());
                request.setAnswerKey(key);
                request.setAnswerData(answer);
            }
            case "OLTRE I LIMITI CONSENTITI FREE TEXT" -> {
                String key = "institutionalPurpose";
                RiskAnalysisTemplateAnswerSeed answer = answersMap.get(key);
                answer.setSuggestedValues(
                        Stream.concat(answer.getSuggestedValues().stream(), Stream.of("N".repeat(251))).toList());
                request.setAnswerKey(key);
                request.setAnswerData(answer);
            }
            default -> throw new IllegalArgumentException("Answer type '%s' not supported".formatted(answerType));
        }
        httpCallExecutor.performCall(() -> purposeTemplateClient.addPurposeTemplateRiskAnalysisAnswer(ptId, request));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            riskAnalysis = (RiskAnalysisTemplateAnswerResponse) httpCallExecutor.getResponse();
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId)),
                    res -> res != HttpStatus.NOT_FOUND,
                    "Failed to retrieve the client!"
            );
        }
    }

    @And("viene aggiunta un'annotazione con testo {isInRange} i {int} caratteri ad una risposta {isVisible} del purpose template")
    public void createRiskAnalysisAnswerAnnotation(boolean inRange, int maxLimit, ResourceState resourceState) {
        assertThat(createdPurposeTemplate).as("Il purpose template creato non dev'essere null").isNotNull();
        assertThat(riskAnalysis).as("La risposta di analisi del rischio non dev'essere null").isNotNull();
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = resourceState.equals(ResourceState.VISIBLE) || resourceState.equals(ResourceState.NOT_VISIBLE)
                ? riskAnalysis.getId()
                : UUID.randomUUID();
        RiskAnalysisTemplateAnswerAnnotationSeed annotationText = new RiskAnalysisTemplateAnswerAnnotationSeed();
        String text = inRange ? "Y".repeat(maxLimit) : "N".repeat(maxLimit + 1);
        annotationText.setText(text);
        if (resourceState.equals(ResourceState.VISIBLE)) {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.addPurposeTemplateRiskAnalysisAnswerAnnotation(ptId, answerId, annotationText)),
                    res -> res != HttpStatus.NOT_FOUND,
                    "Failed to retrieve the client!"
            );
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                annotation = (RiskAnalysisTemplateAnswerAnnotation) httpCallExecutor.getResponse();
            }
        } else {
            httpCallExecutor.performCall(() -> purposeTemplateClient.addPurposeTemplateRiskAnalysisAnswerAnnotation(ptId, answerId, annotationText));
        }
    }

    @And("viene aggiunta un'annotazione con testo contenente hyper-link ad una risposta di analisi del rischio del purpose template")
    public void createRiskAnalysisAnswerAnnotationWithHyperLink() {
        assertThat(createdPurposeTemplate).as("Il purpose template creato non dev'essere null").isNotNull();
        assertThat(riskAnalysis).as("La risposta di analisi del rischio non dev'essere null").isNotNull();
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = riskAnalysis.getId();
        RiskAnalysisTemplateAnswerAnnotationSeed annotationText = new RiskAnalysisTemplateAnswerAnnotationSeed();
        annotationText.setText("https://www.google.com");
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeTemplateClient.addPurposeTemplateRiskAnalysisAnswerAnnotation(ptId, answerId, annotationText)),
                res -> res != HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            annotation = (RiskAnalysisTemplateAnswerAnnotation) httpCallExecutor.getResponse();
        }
    }

    @Then("vengono caricati {int} documenti {string} associati all'annotazione {isVisible}")
    public void uploadAnnotationDocument(int docNumber, String casistica, ResourceState resourceState) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId;
        // TODO migliorare con un enhanced switch o architettando meglio ad oggetti il meccanismo alla base di ResourceState
        if(resourceState.equals(ResourceState.VISIBLE) || resourceState.equals(ResourceState.NOT_VISIBLE)) {
            answerId = riskAnalysis.getId();
        } else {
            answerId = UUID.randomUUID();
        }

        for (int i = 1; i <= docNumber; i++) {
            org.springframework.core.io.Resource doc = getDocument(casistica, i);
            String prettyName = getPrettyName(casistica, i);
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.addRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, prettyName, doc)),
                    res -> resourceState.equals(ResourceState.VISIBLE) ? res != HttpStatus.NOT_FOUND : res == HttpStatus.NOT_FOUND,
                    "Failed to retrieve the client!"
            );
            //supponiamo di voler caricare 3 documenti (dove il terzo genera errore), devo accertarmi che i primi due siano andati a buon fine
            if (i < docNumber) {
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("L'upload del documento " + i + " è fallito: " + httpCallExecutor.getErrorMessage()).isTrue();
                uploadedDocument = (RiskAnalysisTemplateAnswerAnnotationDocument) httpCallExecutor.getResponse();
            }
        }
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            uploadedDocument = (RiskAnalysisTemplateAnswerAnnotationDocument) httpCallExecutor.getResponse();
            getAnnotationDocument(true);
        }
    }

    private String getPrettyName(String errorType, int docNum) {
        return switch (errorType.toUpperCase()) {
            case "DIVERSI CON NOME DIVERSO", "UGUALI CON NOME DIVERSO" -> "prettyName_" + docNum + "_" + DateTime.now();
            case "DIVERSI CON NOME UGUALE" -> "prettyNameUguale";
            case "DI TIPO NON PDF" -> "prettyNameTxt" + docNum;
            default -> throw new IllegalStateException("Unexpected value: " + errorType.toUpperCase());
        };
    }

    private org.springframework.core.io.Resource getDocument(String errorType, int docNum) {
        Resource resource;
        switch (errorType.toUpperCase()) {
            case "DIVERSI CON NOME DIVERSO" -> {
                String filePath = String.format("src/main/resources/%s", "dummy" + docNum + ".pdf");
                resource = blobFileCreator.createBlobFile(filePath, "pdfTest" + docNum);
            }
            case "DIVERSI CON NOME UGUALE" -> {
                String filePath = String.format("src/main/resources/%s", "dummy" + docNum + ".pdf");
                resource = blobFileCreator.createBlobFile(filePath, "pdfTest");
            }
            case "UGUALI CON NOME DIVERSO" -> {
                String filePath = String.format("src/main/resources/%s", "dummy.pdf");
                resource = blobFileCreator.createBlobFile(filePath, "pdfTest" + docNum);
            }
            case "DI TIPO NON PDF" -> {
                String filePath = String.format("src/main/resources/%s", "dummy.txt");
                resource = blobFileCreator.createBlobFile(filePath, "txtTest" + docNum);
            }
            default -> throw new IllegalStateException("Unexpected value: " + errorType.toUpperCase());
        }
        return resource;
    }

    @When("viene eliminata l'annotazione {exists} per il purpose template")
    public void deleteAnnotation(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = exists ? riskAnalysis.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.deleteRiskAnalysisTemplateAnswerAnnotation(ptId, answerId));
    }

    @And("l'eliminazione dell'annotation ha avuto successo")
    public void checkAnnotationSuccessfulltDeleted() {
        assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La chiamata di delete non ha avuto successo").isTrue();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getRiskAnalysisTemplateAnswerAnnotationDocument(createdPurposeTemplate.getId(), riskAnalysis.getId(), uploadedDocument.getId())),
                res -> res == HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
    }

    @When("viene eliminato il documento {exists} dell'annotazione precedentemente creata")
    public void deleteAnnotationDocument(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = riskAnalysis.getId();
        UUID docId = exists ? uploadedDocument.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.deleteRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, docId));
    }

    @When("viene eliminato il documento {exists} dell'annotazione precedentemente creata con successo")
    public void successfullyDeleteAnnotationDocument(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = riskAnalysis.getId();
        UUID docId = exists ? uploadedDocument.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.deleteRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, docId));
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, docId)),
            responseStatusCode -> responseStatusCode.equals(HttpStatus.NOT_FOUND),
            "Il documento non è risultato inesistente come previsto"
        );
    }

    @When("viene recuperato il documento {exists} dell'annotazione precedentemente creata")
    public void getAnnotationDocument(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = riskAnalysis.getId();
        UUID docId = exists ? uploadedDocument.getId() : UUID.randomUUID();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, docId)),
                res -> exists ? res != HttpStatus.NOT_FOUND : res == HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            retrievedDocument = (File) httpCallExecutor.getResponse();
            assertThat(retrievedDocument).as("Il documento recuperato con id " + docId + " non dev'essere null").isNotNull();
        } else {
            retrievedDocument = null;
        }
    }

    @And("si crea una finalità a partire dal purpose template {exists}")
    public void createPurposeFromPurposeTemplate(boolean exists) {
        createPurposeFromPurposeTemplateWithParams(exists, "DATI VALIDI");
    }

    @And("si crea una finalità a partire dal purpose template {exists} passando {string}")
    public void createPurposeFromPurposeTemplateWithParams(boolean exists, String parameterType) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        PurposeFromTemplateSeed fromSeed = new PurposeFromTemplateSeed();
        fromSeed.setTitle("PurposeFromTemplateSeed" + DateTime.now());
        fromSeed.setDailyCalls(10);

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        assertThat(eServiceId).as("L'id dell'eService creato risulta null").isNotNull();
        fromSeed.setEserviceId(eServiceId);
        UUID agreementId = sharedStepsContext.getAgreementId();
        assertThat(agreementId).as("L'id dell'agreement creato risulta null").isNotNull();
        Agreement agreement = clientTokenConfigurator.getAgreementClient().getAgreementById(agreementId);
        assertThat(agreement).as("L'agreement restituito risulta null").isNotNull();
        UUID consumerId = agreement.getConsumer().getId();
        assertThat(consumerId).as("Il consumerId restituito risulta null").isNotNull();
        fromSeed.setConsumerId(consumerId);
        fromSeed.setRiskAnalysisForm(getRiskAnalysisForTemplateFromPurposeTemplate());
        switch (parameterType.toUpperCase()) {
            case "DATI NULL" -> {
                fromSeed.setTitle(null);
                fromSeed.setDailyCalls(null);
                fromSeed.setEserviceId(null);
                fromSeed.setConsumerId(null);
            }
            case "TITOLO ESISTENTE" -> {
                if (duplicatedTitleForPurpose == null) {
                    duplicatedTitleForPurpose = "DuplicatedTitle_" + DateTime.now();
                }
                fromSeed.setTitle(duplicatedTitleForPurpose);
            }
        }
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeApiClient.createPurposeFromTemplate(ptId, fromSeed)),
                res -> exists ? res != HttpStatus.NOT_FOUND : res == HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            createdPurposeFromPurposeTemplate = (CreatedResource) httpCallExecutor.getResponse();
            sharedStepsContext.getPurposeCommonContext().setPurposeId(createdPurposeFromPurposeTemplate.getId().toString());
            movePurposeToState(PurposeVersionState.DRAFT);
        }
    }

    private RiskAnalysisFormSeed getRiskAnalysisForTemplateFromPurposeTemplate() {
        RiskAnalysisFormSeed riskAnalysisFormSeed = new RiskAnalysisFormSeed()
                .version(getPurposeTemplateVersion())
                .answers(Map.of("institutionalPurpose", List.of("Answer1")));
        return riskAnalysisFormSeed;
    }

    @When("si modifica la finalità {exists}")
    public void updatePurpose(boolean exists) {
        updatePurposeWithParams(exists, "DATI VALIDI");
    }

    @When("si modifica la finalità {exists} passando {string}")
    public void updatePurposeWithParams(boolean exists, String parameterType) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID purposeId = exists ? createdPurposeFromPurposeTemplate.getId() : UUID.randomUUID();

        PatchPurposeUpdateFromTemplateContent patch = new PatchPurposeUpdateFromTemplateContent();
        switch (parameterType.toUpperCase()) {
            case "EMPTY TITLE" -> {
                patch.setTitle("");
            }
            case "ZERO DAILY CALLS" -> {
                patch.setDailyCalls(0);
            }
            case "VALORI NULL" -> {
                patch.setTitle(null);
                patch.setDailyCalls(null);
                patch.setRiskAnalysisForm(null);
            }
            case "TITLE ESISTENTE" -> {
                UUID lastCreatedPurposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
                Purpose purposeWithTitleToBeCopied = clientTokenConfigurator.getPurposeApiClient().getPurpose(lastCreatedPurposeId);
                assertThat(purposeWithTitleToBeCopied).isNotNull();
                patch.setTitle(purposeWithTitleToBeCopied.getTitle());
            }
            case "NUOVA RA" -> {
                patch.setTitle(purpose.getTitle() + "_updated");
                patch.setDailyCalls(20);
                RiskAnalysis riskAnalysis1 = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
                patch.setRiskAnalysisForm(riskAnalysis1.getRiskAnalysisForm());
            }
            default -> {
                patch.setTitle(purpose.getTitle() + "_updated");
                patch.setDailyCalls(20);
            }
        }
        httpCallExecutor.performCall(() -> purposeApiClient.patchUpdatePurposeFromTemplate(ptId, purposeId, patch));
    }

    @And("la finalità viene portata in stato {purposeVersionState}")
    public void movePurposeToState(PurposeVersionState state) {
        assertThat(createdPurposeFromPurposeTemplate).as("La finalità creata a partire da una finalità agevolata non dev'essere null").isNotNull();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeApiClient.getPurpose(createdPurposeFromPurposeTemplate.getId())),
                res -> res.is2xxSuccessful(),
                "Failed to retrieve the purpose template from client!"
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purpose = (Purpose) httpCallExecutor.getResponse();
        } else {
            throw new RuntimeException("Eccezione in fase di get dellà finalità creata a partire dal purpose template");
        }
        switch (state) {
            case DRAFT -> log.info("Created Purpose: {}", purpose);
            case ACTIVE -> {
                httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
                if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                    sharedStepsContext.getPollingService().makePolling(
                            () -> purposeApiClient.getPurpose(purpose.getId()),
                            res -> res.getCurrentVersion().getState() == PurposeVersionState.ACTIVE,
                            "La finalità creata dal purpose non risulta attiva"
                    );
                }
            }
            case SUSPENDED -> {
                httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
                if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                    sharedStepsContext.getPollingService().makePolling(
                            () -> purposeApiClient.getPurpose(purpose.getId()),
                            res -> res.getCurrentVersion().getState() == PurposeVersionState.ACTIVE,
                            "La finalità creata dal purpose non risulta attiva"
                    );
                }
                httpCallExecutor.performCall(() -> purposeApiClient.suspendPurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
                if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                    sharedStepsContext.getPollingService().makePolling(
                            () -> purposeApiClient.getPurpose(purpose.getId()),
                            res -> res.getCurrentVersion().getState() == PurposeVersionState.SUSPENDED,
                            "La finalità creata dal purpose non risulta sospesa"
                    );
                }
            }
            case ARCHIVED -> {
                httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
                if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                    sharedStepsContext.getPollingService().makePolling(
                            () -> purposeApiClient.getPurpose(purpose.getId()),
                            res -> res.getCurrentVersion().getState() == PurposeVersionState.ACTIVE,
                            "La finalità creata dal purpose non risulta attiva"
                    );
                }
                httpCallExecutor.performCall(() -> purposeApiClient.suspendPurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
                if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                    sharedStepsContext.getPollingService().makePolling(
                            () -> purposeApiClient.getPurpose(purpose.getId()),
                            res -> res.getCurrentVersion().getState() == PurposeVersionState.SUSPENDED,
                            "La finalità creata dal purpose non risulta sospesa"
                    );
                }
                httpCallExecutor.performCall(() -> purposeApiClient.archivePurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
                if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                    sharedStepsContext.getPollingService().makePolling(
                            () -> purposeApiClient.getPurpose(purpose.getId()),
                            res -> res.getCurrentVersion().getState() == PurposeVersionState.ARCHIVED,
                            "La finalità creata dal purpose non risulta archiviata"
                    );
                }
            }
        }
        assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La chiamata per spostare la finalità in stato " + state + " non è andata a buon fine").isTrue();
    }

    @And("viene preparato un purpose template con purposeTitle {string}, eserviceIds {string}, states {string}, targetTenantKind {string}, handlesPersonalData {string}")
    public void preparePurposeTemplate(
            String purposeTitle,
            String eserviceIds,
            String states,
            String targetTenantKind,
            String handlesPersonalData
    ) {
        // 1) Resolve tokens (possono risultare anche null se %null / invalid)
        String purposeTitleValue = resolver.resolvePurposeTitle(purposeTitle);
        List<UUID> eserviceIdsValue = resolver.resolveEserviceIds(eserviceIds);

        List<it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState> desiredStates =
                resolver.resolveStates(states);

        it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind targetTenantKindValue =
                resolver.resolveTargetTenantKind(targetTenantKind);

        Boolean handlesPersonalDataValue = resolver.resolveHandlesPersonalData(handlesPersonalData);

        // 2) Seed con soli campi obbligatori popolati (valori validi di default)
        PurposeTemplateSeed seed = prepareCreationRequest(handlesPersonalDataValue);

        // 3) Override SEMPRE dei campi parametrizzati (anche a null)
        seed.setPurposeTitle(purposeTitleValue);
        seed.setHandlesPersonalData(handlesPersonalDataValue);

        it.pagopa.interop.generated.openapi.clients.bff.model.TargetTenantKind bffKind = null;
        if (targetTenantKindValue != null) {
            bffKind = it.pagopa.interop.generated.openapi.clients.bff.model.TargetTenantKind
                    .valueOf(targetTenantKindValue.name());
        }
        seed.setTargetTenantKind(bffKind);

        // 4) Create
        CreatedResource created = purposeTemplateClient.createPurposeTemplate(seed);

        Assertions.assertThat(created).as("CreatedResource non deve essere null").isNotNull();
        Assertions.assertThat(created.getId()).as("Id creato non deve essere null").isNotNull();

        UUID purposeTemplateId = created.getId();

        waitUntilStateIn(purposeTemplateId, PurposeTemplateState.DRAFT);

        // 5) Recupero template completo (così ho creatorId/state reali)
        PurposeTemplateWithCompactCreator pt = purposeTemplateClient.getPurposeTemplate(purposeTemplateId);
        Assertions.assertThat(pt).as("PurposeTemplate recuperato non deve essere null").isNotNull();

        // 6) Link EService (opzionale)
        if (eserviceIdsValue != null && !eserviceIdsValue.isEmpty()) {
            for (UUID eserviceId : eserviceIdsValue) {
                purposeTemplateClient.linkEServiceToPurposeTemplate(purposeTemplateId, eserviceId);
            }
        }

        // 7) Porta allo stato desiderato (opzionale)
        if (desiredStates != null && !desiredStates.isEmpty()) {
            it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState desired = desiredStates.get(0);
            changePurposeTemplateStateGradually(purposeTemplateId, desired);
            pt = purposeTemplateClient.getPurposeTemplate(purposeTemplateId); // refresh
        }

        // 8) Popola gli "actual" nel context (per riuso nei filtri %actual)
        purposeTemplateContext.setActualPurposeTitle(pt.getPurposeTitle());

        if (pt.getState() != null) {
            purposeTemplateContext.setActualState(
                    it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState
                            .valueOf(pt.getState().name())
            );
        }
    }

    private void changePurposeTemplateStateGradually(
            UUID purposeTemplateId,
            it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState desired
    ) {
        switch (desired) {
            case DRAFT -> {
                // Non vedo metodi "backToDraft" nel client: quindi non posso forzare DRAFT.
                // Se ti serve davvero, serve un endpoint o ricreare un template nuovo.
            }
            case PUBLISHED -> {
                purposeTemplateClient.publishPurposeTemplate(purposeTemplateId);
                waitUntilStateIn(purposeTemplateId, PurposeTemplateState.PUBLISHED);
            }

            case SUSPENDED -> {
                // tipicamente devi pubblicare prima di sospendere
                purposeTemplateClient.publishPurposeTemplate(purposeTemplateId);
                waitUntilStateIn(purposeTemplateId, PurposeTemplateState.PUBLISHED);

                purposeTemplateClient.suspendPurposeTemplate(purposeTemplateId);
                waitUntilStateIn(purposeTemplateId, PurposeTemplateState.SUSPENDED);
            }

            case ARCHIVED -> {
                // spesso si può archiviare da PUBLISHED (o anche da altri stati).
                // Per sicurezza pubblica prima, poi archivia.
                purposeTemplateClient.publishPurposeTemplate(purposeTemplateId);
                waitUntilStateIn(purposeTemplateId, PurposeTemplateState.PUBLISHED);

                purposeTemplateClient.archivePurposeTemplate(purposeTemplateId);
                waitUntilStateIn(purposeTemplateId, PurposeTemplateState.ARCHIVED);
            }

            default -> throw new IllegalArgumentException("Stato non gestito: " + desired);
        }
    }

    @And("esistono purpose templates di test creati tramite data preparation")
    public void createPurposeTemplatesDatasetUsingPreparePurposeTemplate() {

        // NOISE #1
        preparePurposeTemplate(
                "%random",
                "%actual",
                "DRAFT",
                "PA",
                "true"
        );

        // NOISE #2
        preparePurposeTemplate(
                "%random",
                "%actual",
                "PUBLISHED",
                "PA",
                "false"
        );

        // NOISE #3
        preparePurposeTemplate(
                "%random",
                "%actual",
                "SUSPENDED",
                "PA",
                "false"
        );

        // NOISE #4
        preparePurposeTemplate(
                "%random",
                "%actual",
                "ARCHIVED",
                "PA",
                "false"
        );

        // MATCH: quello che vuoi ottenere col GET quando passi %actual
        // Creato per ultimo così overwrita gli "actual" nel context
        preparePurposeTemplate(
                "%random",
                "%actual",
                "PUBLISHED",
                "PA",
                "true"
        );

        purposeTemplateContext.setActualHandlesPersonalData(true);
    }


    @When("vengono recuperati i purpose templates con offset {string}, limit {string}, purposeTitle {string}, creatorIds {string}, eserviceIds {string}, states {string}, targetTenantKind {string}, handlesPersonalData {string}")
    public void getPurposeTemplates(
            String offset,
            String limit,
            String purposeTitle,
            String creatorIds,
            String eserviceIds,
            String states,
            String targetTenantKind,
            String handlesPersonalData
    ) {
        Integer offsetValue = resolver.resolveOffset(offset);
        Integer limitValue = resolver.resolveLimit(limit);

        String purposeTitleValue = resolver.resolvePurposeTitle(purposeTitle);

        List<UUID> creatorIdsValue = resolver.resolveCreatorIds(creatorIds);
        List<UUID> eserviceIdsValue = resolver.resolveEserviceIds(eserviceIds);

        List<it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState> statesValue =
                resolver.resolveStates(states);

        it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind targetTenantKindValue =
                resolver.resolveTargetTenantKind(targetTenantKind);

        Boolean handlesPersonalDataValue = resolver.resolveHandlesPersonalData(handlesPersonalData);

        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplates(
                offsetValue,
                limitValue,
                purposeTitleValue,
                creatorIdsValue,
                eserviceIdsValue,
                statesValue,
                targetTenantKindValue,
                handlesPersonalDataValue
        ));

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            PurposeTemplates response = (PurposeTemplates) httpCallExecutor.getResponse();

            Assertions.assertThat(response)
                .as("La response contenente i purpose templates non deve essere null")
                .isNotNull();

            // === SOSTITUISCI getResults() col getter reale della lista ===
            List<it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate> results =
                    response.getResults();

            Assertions.assertThat(results)
                    .as("La lista dei purpose templates non deve essere null")
                    .isNotNull();

            for (var pt : results) {
                // purposeTitle (match esatto)
                if (purposeTitleValue != null) {
                    Assertions.assertThat(pt.getPurposeTitle())
                            .as("purposeTitle deve rispettare il filtro")
                            .isEqualTo(purposeTitleValue);
                }

                // creatorIds (IN)
                if (creatorIdsValue != null && !creatorIdsValue.isEmpty()) {
                    Assertions.assertThat(pt.getCreatorId())
                            .as("creatorId deve essere contenuto in creatorIds filter")
                            .isIn(creatorIdsValue);
                }

                // states (IN)
                if (statesValue != null && !statesValue.isEmpty()) {
                    Assertions.assertThat(pt.getState())
                            .as("state deve essere contenuto in states filter")
                            .isIn(statesValue);
                }

                // targetTenantKind (match esatto)
                if (targetTenantKindValue != null) {
                    Assertions.assertThat(pt.getTargetTenantKind())
                            .as("targetTenantKind deve rispettare il filtro")
                            .isEqualTo(targetTenantKindValue);
                }

                // handlesPersonalData (match esatto)
                if (handlesPersonalDataValue != null) {
                    Assertions.assertThat(pt.getHandlesPersonalData())
                            .as("handlesPersonalData deve rispettare il filtro")
                            .isEqualTo(handlesPersonalDataValue);
                }

                // eserviceIds: al momento nella response non trovo informazioni esplicite sugli ids
                if (eserviceIdsValue != null && !eserviceIdsValue.isEmpty()) {

                }
            }
        }
    }

    @When("l'utente tenta di effettuare la modifica parziale del purpose template")
    public void patchPurposeTemplate() {
        sharedStepsContext.getPurposeTemplateContext().setUpdatedAt(OffsetDateTime.now());
        PurposeTemplateDraftUpdateSeed request = this.patchAssistant.buildDefaultPatchRequest();
        patchAssistant.patchResource(request);
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale del purpose template")
    public void patchEService(String tenant, M2MRole m2mRole) {
        PurposeTemplateDraftUpdateSeed request = this.patchAssistant.buildDefaultPatchRequest();
        patchAssistant.patchResource(request, tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale del purpose template specificando un sottoinsieme di informazioni")
    public void patchPurposeTemplateSubset() {
        UUID uuid = UUID.randomUUID();
        PurposeTemplateDraftUpdateSeed request = new PurposeTemplateDraftUpdateSeed()
            .targetDescription("minimal patched targetDescription - " + uuid);
        patchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale del purpose template specificando un insieme vuoto di informazioni")
    public void patchPurposeTemplateEmpty() {
        PurposeTemplateDraftUpdateSeed request = new PurposeTemplateDraftUpdateSeed();
        patchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale di un purpose template inesistente")
    public void patchNonExistentPurposeTemplate() {
        patchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale del purpose template con token non valido")
    public void patchPurposeTemplateWithNotValidToken() {
        PurposeTemplateDraftUpdateSeed request = patchAssistant.buildDefaultPatchRequest();
        patchAssistant.patchResourceWithInvalidToken(request);
    }

    @Then("il purpose template restituito è coerente con le modifiche effettuate")
    public void checkPurposeTemplatePatchResult() {
        patchAssistant.checkPatchOperationResult();
    }

    @Then("il purpose template è stato parzialmente modificato correttamente")
    public void checkPurposeTemplateAfterPatch() {
        patchAssistant.checkPatchedResource();
    }

    @Then("il purpose template non ha subito modifiche")
    public void checkPurposeTemplateAfterNonPatch() {
        patchAssistant.checkUnpatchedResource();
    }
}

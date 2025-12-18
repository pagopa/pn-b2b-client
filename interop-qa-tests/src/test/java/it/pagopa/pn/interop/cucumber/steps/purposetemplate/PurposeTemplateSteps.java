package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

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

    /**
     * Quando voglio simulare una casistica di titolo duplicato, la prima volta ne creo uno (con timestamp) e lo setto qua.
     * La seconda volta, quando questa variabile non è più null, ri-applico lo stesso titolo.
     */
    private String duplicatedTitleForPurpose;

    public PurposeTemplateSteps(ClientTokenConfigurator clientTokenConfigurator1,
                                SharedStepsContext sharedStepsContext,
                                ClientTokenConfigurator clientTokenConfigurator,
                                BlobFileCreator blobFileCreator) {
        this.clientTokenConfigurator = clientTokenConfigurator1;
        this.sharedStepsContext = sharedStepsContext;
        this.blobFileCreator = blobFileCreator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
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

    private void prepareCreationRequest(Boolean handlePersonalDataValue) {
        purposeTemplateCreationRequest = new PurposeTemplateSeed();
        purposeTemplateCreationRequest.setPurposeTitle("purposeTitle" + DateTime.now());
        purposeTemplateCreationRequest.setPurposeDescription("purposeDescription_CREATE");
        purposeTemplateCreationRequest.setTargetDescription("targetDescription_CREATE");
        purposeTemplateCreationRequest.setTargetTenantKind(TenantKind.PA);
        purposeTemplateCreationRequest.setPurposeIsFreeOfCharge(true);
        purposeTemplateCreationRequest.setPurposeDailyCalls(10);

        purposeTemplateCreationRequest.setHandlesPersonalData(handlePersonalDataValue);
        purposeTemplateCreationRequest.setPurposeFreeOfChargeReason("Sono una Pubblica Amministrazione");

        if (handlePersonalDataValue != null) {
            RiskAnalysisFormTemplateSeed riskAnalysisForm = new RiskAnalysisFormTemplateSeed()
                    .version("3.1")
                    .answers(getRiskAnalysysTemplateFormAnswerMap(purposeTemplateCreationRequest.getHandlesPersonalData()));
            purposeTemplateCreationRequest.setPurposeRiskAnalysisForm(riskAnalysisForm);
        }
    }

    private void invokeCreatePurposeTemplate() {
        httpCallExecutor.performCall(() -> purposeTemplateClient.createPurposeTemplate(purposeTemplateCreationRequest));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            createdPurposeTemplate = (CreatedResource) httpCallExecutor.getResponse();
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(createdPurposeTemplate.getId())),
                    res -> res.is2xxSuccessful(),
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
        answersMap.put("isRequestOnBehalfOfThirdParties", answerThirdParties);
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

    @When("si aggiorna il purpose template {exists}")
    public void updatePurposeTemplateRequest(boolean exists) {
        purposeTemplateCreationRequest.setPurposeTitle("updated_purposeTitle_" + DateTime.now());
        purposeTemplateCreationRequest.setPurposeDescription("updated_purposeDescription_" + DateTime.now());
        purposeTemplateCreationRequest.setTargetDescription("updated_targetDescription_" + DateTime.now());
        purposeTemplateCreationRequest.setPurposeFreeOfChargeReason("updated_purposeFreeOfChargeReason_" + DateTime.now());
        purposeTemplateCreationRequest.setTargetTenantKind(TenantKind.GSP);
        invokeUpdatePurposeTemplate(exists);
    }

    @When("si aggiorna il purpose template {exists} con errore di tipo {purposeTemplateError}")
    public void updatePurposeTemplateWithError(boolean exists, PurposeTemplateErrorTypes error) {
        insertErrorsOnPurpose(error);
        invokeUpdatePurposeTemplate(exists);
    }

    private void invokeUpdatePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeTemplateClient.updatePurposeTemplate(ptId, purposeTemplateCreationRequest)),
                res -> exists ? res != HttpStatus.NOT_FOUND : res == HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
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

        InlineObject2 inlineObject = new InlineObject2();
        inlineObject.setEserviceId(eServiceId);

        httpCallExecutor.performCall(() -> purposeTemplateClient.linkEServiceToPurposeTemplate(ptId, inlineObject));
    }

    @Then("si effettua la get degli e-service associati al purpose template {exists}")
    public void getPurposeTemplateEservices(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        if (exists) {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 10, null, null)),
                    res -> ((EServiceDescriptorsPurposeTemplate) httpCallExecutor.getResponse()).getResults().size() > 0,
                    "Failed to retrieve the client!"
            );
        } else {
            httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 10, null, null));
        }

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            EServiceDescriptorsPurposeTemplate esDescriptorsPt = (EServiceDescriptorsPurposeTemplate) httpCallExecutor.getResponse();
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            linkedEServices = resultList.stream().map(EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor::getEservice).toList();
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

        InlineObject3 o3 = new InlineObject3();
        o3.setEserviceId(eServiceId);

        httpCallExecutor.performCall(() -> purposeTemplateClient.unlinkEServiceToPurposeTemplate(ptId, o3));
        if (exists) {
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                pollingService.makePolling(
                        () -> httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 0, 10, null, null)),
                        res -> ((EServiceDescriptorsPurposeTemplate) httpCallExecutor.getResponse()).getResults().stream().filter(
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

    /**
     * Come il metodo di sopra, ma esegue il passaggio da uno stato all'altro nell'ordine corretto
     */
    @And("il purpose template viene gradualmente spostato in stato {ptState}")
    public void changePurposeTemplateStateGradually(PurposeTemplateState ptState) {
        switch (ptState) {
            case PUBLISHED -> {
                activatePurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La pubblicazione non è andata a buon fine").isTrue();
            }
            case SUSPENDED -> {
                activatePurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La pubblicazione non è andata a buon fine").isTrue();
                suspendPurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La sospensione non è andata a buon fine").isTrue();
            }
            case ARCHIVED -> {
                activatePurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La pubblicazione non è andata a buon fine").isTrue();
                suspendPurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La sospensione non è andata a buon fine").isTrue();
                archivePurposeTemplate(true);
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("L'archiviazione non è andata a buon fine").isTrue();
            }
        }
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
        httpCallExecutor.performCall(() -> purposeTemplateClient.suspendPurposeTemplate(ptId));
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
        httpCallExecutor.performCall(() -> purposeTemplateClient.archivePurposeTemplate(ptId));
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

    @And("viene aggiunta un'annotazione con testo {isInRange} i {int} caratteri ad una risposta {exists} del purpose template")
    public void createRiskAnalysisAnswerAnnotation(boolean inRange, int maxLimit, boolean answerExists) {
        assertThat(createdPurposeTemplate).as("Il purpose template creato non dev'essere null").isNotNull();
        assertThat(riskAnalysis).as("La risposta di analisi del rischio non dev'essere null").isNotNull();
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = answerExists ? riskAnalysis.getId() : UUID.randomUUID();
        RiskAnalysisTemplateAnswerAnnotationSeed annotationText = new RiskAnalysisTemplateAnswerAnnotationSeed();
        String text = inRange ? "Y".repeat(maxLimit) : "N".repeat(maxLimit + 1);
        annotationText.setText(text);
        if (answerExists) {
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

    @Then("vengono caricati {int} documenti {string} associati all'annotazione {exists}")
    public void uploadAnnotationDocument(int docNumber, String casistica, boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = exists ? riskAnalysis.getId() : UUID.randomUUID();

        for (int i = 1; i <= docNumber; i++) {
            org.springframework.core.io.Resource doc = getDocument(casistica, i);
            String prettyName = getPrettyName(casistica, i);
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(() -> purposeTemplateClient.addRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, prettyName, doc)),
                    res -> exists ? res != HttpStatus.NOT_FOUND : res == HttpStatus.NOT_FOUND,
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
            movePurposeToState(PurposeVersionState.DRAFT);
        }
    }

    private RiskAnalysisFormSeed getRiskAnalysisForTemplateFromPurposeTemplate() {
        RiskAnalysisFormSeed riskAnalysisFormSeed = new RiskAnalysisFormSeed()
                .version("3.1")
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
            case DRAFT -> log.info("Created Purpose: " + purpose);
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
}
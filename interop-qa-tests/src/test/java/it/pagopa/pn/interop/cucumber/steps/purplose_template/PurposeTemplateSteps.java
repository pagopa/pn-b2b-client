package it.pagopa.pn.interop.cucumber.steps.purplose_template;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class PurposeTemplateSteps {

    private final IPurposeTemplateClient purposeTemplateClient;

    private final IPurposeApiClient purposeApiClient;

    private final BlobFileCreator blobFileCreator;

    private CreatedResource createdPurposeTemplate;

    private CreatedResource createdPurposeFromPurposeTemplate;

    private PurposeTemplateWithCompactCreator purposeTemplateWithCompactCreator;

    private CatalogPurposeTemplates catalogPurposeTemplates;

    private PurposeTemplate purposeTemplate;

    private Purpose purpose;

    private RiskAnalysisTemplateAnswerResponse riskAnalysis;

    private RiskAnalysisTemplateAnswerAnnotationDocument uploadedDocument;

    private File retrievedDocument;

    private List<CompactPurposeTemplateEService> linkedEServices;

    private final SharedStepsContext sharedStepsContext;

    private final IHttpExecutor httpCallExecutor;

    public PurposeTemplateSteps(SharedStepsContext sharedStepsContext,
                                ClientTokenConfigurator clientTokenConfigurator,
                                BlobFileCreator blobFileCreator) {
        this.sharedStepsContext = sharedStepsContext;
        this.blobFileCreator = blobFileCreator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
    }

    @When("viene creato un nuovo purpose template")
    public void createPurposeTemplate() {
        createPurposeTemplateWithHandlePersonalData("true");
    }

    //Il seguente metodo crea un purpose template in stato draft
    @When("viene creato un nuovo purpose template con handlePersonalData {string}")
    public void createPurposeTemplateWithHandlePersonalData(String handlePersonalDataValue) {
        PurposeTemplateSeed request = new PurposeTemplateSeed();
        request.setPurposeTitle("purposeTitle" + DateTime.now());
        request.setPurposeDescription("purposeDescription_CREATE");
        request.setTargetDescription("targetDescription_CREATE");
        request.setTargetTenantKind(TenantKind.PA);
        request.setPurposeIsFreeOfCharge(false);

        RiskAnalysisFormTemplateSeed riskAnalysisForm = new RiskAnalysisFormTemplateSeed();
        RiskAnalysisTemplateAnswerSeed answerSeed = new RiskAnalysisTemplateAnswerSeed();
        RiskAnalysisTemplateAnswerAnnotationSeed annotationSeed = new RiskAnalysisTemplateAnswerAnnotationSeed();

        request.setPurposeRiskAnalysisForm(riskAnalysisForm);

        switch (handlePersonalDataValue.toUpperCase()) {
            case "TRUE" -> request.setHandlesPersonalData(true);
            case "FALSE" -> request.setHandlesPersonalData(false);
        }
        httpCallExecutor.performCall(() -> purposeTemplateClient.createPurposeTemplate(request));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            createdPurposeTemplate = (CreatedResource) httpCallExecutor.getResponse();
        }
    }

    @When("si effettua la get del purpose template {exists}")
    public void getPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplateWithCompactCreator = (PurposeTemplateWithCompactCreator) httpCallExecutor.getResponse();
            assertThat(purposeTemplateWithCompactCreator).as("Il risultato della get del purpose con id" + createdPurposeTemplate.getId() + " non dev'essere null").isNotNull();
        }
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
        httpCallExecutor.performCall(() -> purposeTemplateClient.getCreatorPurposeTemplates(1, 10, null, null, state));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            CreatorPurposeTemplates creatorPurposeTemplates = (CreatorPurposeTemplates) httpCallExecutor.getResponse();
            assertThat(creatorPurposeTemplates).as("Il risultato della get dei purpose template by Creator" + createdPurposeTemplate.getId() + " non dev'essere null").isNotNull();
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    @When("si effettua la get di tutti i purpose template con titolo {string}")
    public void getCatalogPurposeTemplate(String title) {
        getCatalogPurposeTemplateWithPersonalData(title, "true");
    }

    @When("si effettua la get di tutti i purpose template con titolo {string} e handlePersonalData {string}")
    public void getCatalogPurposeTemplateWithPersonalData(String title, String handlePersonalDataValue) {
        Boolean handlePersonalData;
        switch (handlePersonalDataValue.toUpperCase()) {
            case "TRUE" -> handlePersonalData = true;
            case "FALSE" -> handlePersonalData = false;
            default -> handlePersonalData = null;
        }
        String titleFilter = title.equalsIgnoreCase("ANY") ? null : title;
        httpCallExecutor.performCall(() -> purposeTemplateClient.getCatalogPurposeTemplates(1, 10, titleFilter, null, null, null, true, handlePersonalData));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            catalogPurposeTemplates = (CatalogPurposeTemplates) httpCallExecutor.getResponse();
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }


    @When("si aggiorna il purpose template {exists}")
    public void updatePurposeTemplate(boolean exists) {
        PurposeTemplateSeed updateRequest = new PurposeTemplateSeed();
        updateRequest.setPurposeTitle("updated_purposeTitle" + DateTime.now());
        updateRequest.setPurposeDescription("updated_purposeDescription" + DateTime.now());

        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.updatePurposeTemplate(ptId, updateRequest));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il template aggiornato non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getPurposeTitle()).as("Il titolo deve risultare aggiornato").contains("updated_title");
            assertThat(purposeTemplate.getPurposeDescription()).as("La descrizione deve risultare aggiornata").contains("updated_description");
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    @When("si cancella il purpose template {exists}")
    public void deletePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.deletePurposeTemplate(ptId));
        if (exists) {
            PurposeTemplateWithCompactCreator pt = getPurposeTemplateById(ptId);
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                assertThat(pt).as("Dopo la delete (OK), il result della get del purpose template con id " + ptId + " dovrebbe essere null").isNull();
            } else {
                assertThat(pt).as("Dopo la delete (KO), il result della get del purpose template con id " + ptId + " non dovrebbe essere null").isNotNull();
            }
        }
    }

    public PurposeTemplateWithCompactCreator getPurposeTemplateById(UUID ptId) {
        PurposeTemplateWithCompactCreator pt = null;
        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplateWithCompactCreator = (PurposeTemplateWithCompactCreator) httpCallExecutor.getResponse();
            return purposeTemplateWithCompactCreator;
        } else {
            log.info("Failed to get PurposeTemplate with id " + ptId);
            log.info(httpCallExecutor.getErrorMessage());
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
        if (exists) {
            EServiceDescriptorPurposeTemplate esdPt = (EServiceDescriptorPurposeTemplate) httpCallExecutor.getResponse();
        }
        httpCallExecutor.performCall(() -> purposeTemplateClient.linkEServiceToPurposeTemplate(ptId, inlineObject));
        if (exists) {
            EServiceDescriptorsPurposeTemplate esDescriptorsPt = purposeTemplateClient.getPurposeTemplateEServices(ptId, 1, 10, null, null);
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            CompactPurposeTemplateEService linkedEService = resultList.stream().filter(x -> x.getEservice().getId().equals(eServiceId)).findFirst().orElse(null).getEservice();
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + " non risulta associato al purpose template con id " + ptId).isNotNull();
            } else {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + " risulta associato al purpose template con id " + ptId).isNull();
            }
        }
    }


    @Then("si effettua la get degli e-service associati al purpose template {exists}")
    public void getPurposeTemplateEservices(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateEServices(ptId, 1, 10, null, null));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            EServiceDescriptorsPurposeTemplate esDescriptorsPt = (EServiceDescriptorsPurposeTemplate) httpCallExecutor.getResponse();
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            linkedEServices = resultList.stream().map(EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor::getEservice).toList();
            checkEServicesList(true);
        } else {
            log.info(httpCallExecutor.getErrorMessage());
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
            EServiceDescriptorsPurposeTemplate esDescriptorsPt = purposeTemplateClient.getPurposeTemplateEServices(ptId, 1, 10, null, null);
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            CompactPurposeTemplateEService linkedEService = resultList.stream().filter(x -> x.getEservice().getId().equals(eServiceId)).findFirst().orElse(null).getEservice();
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + " risulta ancora associato al purpose template con id " + ptId).isNull();
            } else {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + "non risulta più associato al purpose template con id " + ptId).isNotNull();
            }
        }
    }

    @And("il purpose template {exists} viene spostato in stato {ptState}")
    public void changePurposeTemplateState(boolean exists, PurposeTemplateState ptState) {
        switch (ptState) {
            case DRAFT -> log.info("already in DRAFT");
            case PUBLISHED -> activatePurposeTemplate(exists);
            case SUSPENDED -> suspendPurposeTemplate(exists);
            case ARCHIVED -> archivePurposeTemplate(exists);
        }
    }

    @And("il purpose template {exists} viene riattivato")
    public void reactivateSuspendedPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.unsuspendPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta attivo").isEqualTo(PurposeTemplateState.PUBLISHED);
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    private void activatePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.publishPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta attivo").isEqualTo(PurposeTemplateState.PUBLISHED);
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    private void suspendPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.suspendPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta sospeso").isEqualTo(PurposeTemplateState.SUSPENDED);
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    private void archivePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.archivePurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta archiviato").isEqualTo(PurposeTemplateState.ARCHIVED);
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    @And("viene creato un nuovo purpose template in stato {ptState}")
    public void vieneCreatoUnNuovoPurposeTemplateInStato(PurposeTemplateState ptState) {
        createPurposeTemplate();
        switch (ptState) {
            case PUBLISHED -> activatePurposeTemplate(true);
            case SUSPENDED -> {
                activatePurposeTemplate(true);
                suspendPurposeTemplate(true);
            }
            case ARCHIVED -> {
                activatePurposeTemplate(true);
                suspendPurposeTemplate(true);
                archivePurposeTemplate(true);
            }
        }
    }

    //TODO MATTEO, non è così che va creata la request, editare a tempo debito
    @And("viene creata una risposta di analisi del rischio {string} per il purpose template {exists}")
    public void createRiskAnalysisAnswer(String answerType, boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        RiskAnalysisTemplateAnswerRequest request = new RiskAnalysisTemplateAnswerRequest();
        String answerKey = "TODO MATTEO che ci devo mettere ???";
        RiskAnalysisTemplateAnswerSeed answerSeed = new RiskAnalysisTemplateAnswerSeed();
        answerSeed.setEditable(true);
        answerSeed.setAnnotation(new RiskAnalysisTemplateAnswerAnnotationSeed());
        request.setAnswerData(answerSeed);

        String text = "";
        switch (answerType.toUpperCase()) {
            case "ENTRO I LIMITI CONSENTITI" -> text = "A".repeat(2000);
            case "OLTRE I LIMITI CONSENTITI" -> text = "B".repeat(2001);
            case "CONTENENTE HYPER LINK" -> text = "https://www.google.com";
        }
        request.getAnswerData().getAnnotation().setText(text);

        httpCallExecutor.performCall(() -> purposeTemplateClient.addPurposeTemplateRiskAnalysisAnswer(ptId, request));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            riskAnalysis = (RiskAnalysisTemplateAnswerResponse) httpCallExecutor.getResponse();
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    @And("viene {string} un'annotation {string} per il purpose template {exists}")
    public void createRiskAnalysisAnswerAnnotation(String action, String annotationTextType, boolean exists) {
        UUID ptId = exists ? createdPurposeTemplate.getId() : UUID.randomUUID();
        UUID answerId;
        if (action.equalsIgnoreCase("MODIFICATA")) {
            assertThat(riskAnalysis).as("La risposta di analisi del rischio non dev'essere null").isNotNull();
            answerId = riskAnalysis.getId();
        } else if (action.equalsIgnoreCase("MODIFICATA 404")) {
            answerId = UUID.randomUUID();
        } else {
            answerId = null;
        }

        RiskAnalysisTemplateAnswerAnnotationText annotationText = new RiskAnalysisTemplateAnswerAnnotationText();
        String text = "";
        switch (annotationTextType.toUpperCase()) {
            case "ENTRO I LIMITI CONSENTITI" -> text = "A".repeat(250);
            case "OLTRE I LIMITI CONSENTITI" -> text = "B".repeat(251);
            case "CONTENENTE HYPER LINK" -> text = "https://www.google.com";
            case "RIMUOVENDO IL TESTO" -> text = "";
        }
        annotationText.setText(text);
        httpCallExecutor.performCall(() -> purposeTemplateClient.addPurposeTemplateRiskAnalysisAnswerAnnotation(ptId, answerId, annotationText));
    }

    @Then("vengono caricati {int} documenti {string} associati all'annotation {exists}")
    public void uploadAnnotationDocument(int docNumber, String casistica, boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = exists ? riskAnalysis.getId() : UUID.randomUUID();

        for (int i = 1; i <= docNumber; i++) {
            String prettyName = getPrettyName(casistica, i);
            org.springframework.core.io.Resource doc = getDocument(casistica, i);
            httpCallExecutor.performCall(() -> purposeTemplateClient.addRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, prettyName, doc));
            //supponiamo di voler caricare 3 documenti (dove il terzo genera errore), devo accertarmi che i primi due siano andati a buon fine
            if (i < docNumber) {
                assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("L'upload del documento " + i + " è fallito: " + httpCallExecutor.getErrorMessage()).isTrue();
            }
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                uploadedDocument = (RiskAnalysisTemplateAnswerAnnotationDocument) httpCallExecutor.getResponse();
                getAnnotationDocument(true);
            } else {
                log.info(httpCallExecutor.getErrorMessage());
            }
        }
    }

    private String getPrettyName(String errorType, int docNum) {
        return switch (errorType.toUpperCase()) {
            case "DIVERSI CON NOME DIVERSO", "UGUALI CON NOME DIVERSO" -> "prettyName_" + docNum;
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
        ;
        return resource;
    }

    @When("viene eliminata l'annotation {exists} per il purpose template")
    public void deleteAnnotation(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = exists ? riskAnalysis.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.deleteRiskAnalysisTemplateAnswerAnnotation(ptId, answerId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            //TODO MATTEO
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
    }

    @When("viene eliminato il documento {exists} dell'annotation precedentemente creata")
    public void deleteAnnotationDocument(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = riskAnalysis.getId();
        UUID docId = exists ? uploadedDocument.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.deleteRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, docId));
    }

    @When("viene recuperato il documento {exists} dell'annotation precedentemente creata")
    public void getAnnotationDocument(boolean exists) {
        UUID ptId = createdPurposeTemplate.getId();
        UUID answerId = riskAnalysis.getId();
        UUID docId = exists ? uploadedDocument.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.getRiskAnalysisTemplateAnswerAnnotationDocument(ptId, answerId, docId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            retrievedDocument = (File) httpCallExecutor.getResponse();
            assertThat(retrievedDocument).as("Il documento recuperato con id " + docId + " non dev'essere null").isNotNull();
        } else {
            retrievedDocument = null;
            log.info(httpCallExecutor.getErrorMessage());
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
        switch (parameterType.toUpperCase()) {
            case "DATI NULL" -> {
                fromSeed.setTitle(null);
                fromSeed.setDailyCalls(null);
            }
            case "TITOLO ESISTENTE" -> fromSeed.setTitle("Duplicated Title");
            default -> {
                //TODO MATTEO, recuperare questi dati
                fromSeed.setEserviceId(null);
                fromSeed.setConsumerId(null);
            }
        }

        httpCallExecutor.performCall(() -> purposeApiClient.createPurposeFromTemplate(ptId, fromSeed));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            createdPurposeFromPurposeTemplate = (CreatedResource) httpCallExecutor.getResponse();
            movePurposeToState(PurposeVersionState.DRAFT);
        } else {
            log.info(httpCallExecutor.getErrorMessage());
        }
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
            case "DATI NULL" -> {
                patch.setTitle(null);
                patch.setDailyCalls(null);
                patch.setRiskAnalysisForm(null);
            }
            case "TITLE ESISTENTE" -> {
                patch.setTitle("PurposeTitleUpdateFail");
            }
            default -> {
                patch.setTitle(purpose.getTitle() + "_updated");
                patch.setDailyCalls(20);
                //TODO MATTEO settare restanti proprietà
            }
        }
        httpCallExecutor.performCall(() -> purposeApiClient.patchUpdatePurposeFromTemplate(ptId, purposeId, patch));
    }

    @And("la finalità viene portata in stato {purposeVersionState}")
    public void movePurposeToState(PurposeVersionState state) {
        assertThat(createdPurposeFromPurposeTemplate).as("La finalità creata a partire da una finalità agevolata non dev'essere null").isNotNull();
        httpCallExecutor.performCall(() -> purposeApiClient.getPurpose(createdPurposeFromPurposeTemplate.getId()));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purpose = (Purpose) httpCallExecutor.getResponse();
        } else {
            throw new RuntimeException("Eccezione in fase di get dellà finalità creata a partire dal purpose template");
        }
        switch (state) {
            case DRAFT -> log.info("Created Purpose: " + purpose);
            case ACTIVE ->
                    httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
            case SUSPENDED ->
                    httpCallExecutor.performCall(() -> purposeApiClient.suspendPurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
            case ARCHIVED ->
                    httpCallExecutor.performCall(() -> purposeApiClient.archivePurposeVersion(purpose.getId(), purpose.getCurrentVersion().getId()));
        }
        assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful()).as("La chiamata per spostare la finalità in stato " + state + " non è andata a buon fine").isTrue();
    }
}

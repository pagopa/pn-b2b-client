package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.conf.UploadDocumentFilesProperties;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadSupport;
import it.pagopa.pn.interop.cucumber.steps.common.upload.ExpectedOutcome;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadAttemptResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;

import java.util.ArrayList;
import java.util.List;

public class EserviceTemplateDocumentUploadSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final DocumentUploadSupport uploadSupport;
    private final EserviceTemplateDocumentUploadOps uploadOps;

    private final List<UploadAttemptResult> uploadAttempts = new ArrayList<>();

    public EserviceTemplateDocumentUploadSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        UploadDocumentFilesProperties uploadDocumentFilesProperties
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        PollingService pollingService = sharedStepsContext.getPollingService();
        IM2MEServiceTemplateClient eServiceTemplateClient = clientTokenConfigurator.getM2mEServiceTemplateClient();

        this.uploadSupport = new DocumentUploadSupport(uploadDocumentFilesProperties);
        this.uploadOps = new EserviceTemplateDocumentUploadOps(sharedStepsContext, eServiceTemplateClient, pollingService);
    }

    @When("l'utente tenta di caricare uno alla volta il seguente insieme di documenti sulla versione dell'e-service template")
    public void uploadDocumentsByTypeList(List<String> fileTypes) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        uploadSupport.executeUploads(uploadSupport.requestsFromTypeList(fileTypes), uploadAttempts, uploadOps);
    }

    @When("l'utente tenta di caricare uno alla volta i seguenti tipi documenti sulla versione dell'e-service template, con l'estensione specificata")
    public void uploadDocumentsByTypeAndExtension(DataTable dataTable) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        List<UploadRequest> requests = uploadSupport.requestsFromTable(dataTable.asMaps(), "documento", "estensione");
        uploadSupport.executeUploads(requests, uploadAttempts, uploadOps);
    }

    @Then("tutti i tentativi di caricamento sulla versione dell'e-service template hanno esito {expectedOutcome}")
    public void verifyUploadAttemptsOutcome(ExpectedOutcome expectedOutcome) {
        uploadSupport.verifyUploadAttemptsOutcome(uploadAttempts, expectedOutcome);
    }
}


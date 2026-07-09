package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.conf.UploadDocumentFilesProperties;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadSupport;
import it.pagopa.pn.interop.cucumber.steps.common.upload.ExpectedOutcome;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadAttemptResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;

import java.util.ArrayList;
import java.util.List;

public class PurposeTemplateAnnotationDocumentUploadSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final DocumentUploadSupport uploadSupport;
    private final PurposeTemplateAnnotationDocumentUploadOps uploadOps;

    private final List<UploadAttemptResult> uploadAttempts = new ArrayList<>();

    public PurposeTemplateAnnotationDocumentUploadSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        UploadDocumentFilesProperties uploadDocumentFilesProperties
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        PollingService pollingService = sharedStepsContext.getPollingService();
        IPurposeTemplateClient purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();

        this.uploadSupport = new DocumentUploadSupport(uploadDocumentFilesProperties);
        this.uploadOps = new PurposeTemplateAnnotationDocumentUploadOps(
            sharedStepsContext,
            purposeTemplateClient,
            pollingService
        );
    }

    @When("l'utente tenta di caricare uno alla volta il seguente insieme di documenti sulla risk analysis del purpose template")
    public void uploadDocumentsByTypeList(List<String> fileTypes) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        uploadSupport.executeUploads(uploadSupport.requestsFromTypeList(fileTypes), uploadAttempts, uploadOps);
    }

    @When("l'utente tenta di caricare uno alla volta i seguenti tipi documenti sulla risk analysis del purpose template, con l'estensione specificata")
    public void uploadDocumentsByTypeAndExtension(DataTable dataTable) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        List<UploadRequest> requests = uploadSupport.requestsFromTable(dataTable.asMaps(), "documento", "estensione");
        uploadSupport.executeUploads(requests, uploadAttempts, uploadOps);
    }

    @Then("tutti i tentativi di caricamento sulla risk analysis del purpose template hanno esito {expectedOutcome}")
    public void verifyUploadAttemptsOutcome(ExpectedOutcome expectedOutcome) {
        uploadSupport.verifyUploadAttemptsOutcome(uploadAttempts, expectedOutcome);
    }
}


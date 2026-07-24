package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.conf.UploadDocumentFilesProperties;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadSupport;
import it.pagopa.pn.interop.cucumber.steps.common.upload.ExpectedOutcome;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadAttemptResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;

import java.util.ArrayList;
import java.util.List;

public class EserviceDocumentUploadSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final DocumentUploadSupport uploadSupport;
    private final EserviceDocumentUploadOps uploadOps;
    private final EserviceInterfaceUploadOps interfaceUploadOps;

    private final List<UploadAttemptResult> uploadAttempts = new ArrayList<>();
    private final List<UploadAttemptResult> interfaceUploadAttempts = new ArrayList<>();

    public EserviceDocumentUploadSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        UploadDocumentFilesProperties uploadDocumentFilesProperties
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        PollingService pollingService = sharedStepsContext.getPollingService();
        IM2MEserviceDescriptorClient descriptorClient = clientTokenConfigurator.getM2mEServiceDescriptorClient();
        descriptorClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());

        this.uploadSupport = new DocumentUploadSupport(uploadDocumentFilesProperties);
        this.uploadOps = new EserviceDocumentUploadOps(sharedStepsContext, descriptorClient, pollingService);
        this.interfaceUploadOps = new EserviceInterfaceUploadOps(sharedStepsContext, descriptorClient, pollingService);
    }

    @When("l'utente tenta di caricare uno alla volta il seguente insieme di documenti")
    public void uploadDocumentsByTypeList(List<String> fileTypes) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        uploadSupport.executeUploads(uploadSupport.requestsFromTypeList(fileTypes), uploadAttempts, uploadOps);
    }

    @When("l'utente tenta di caricare uno alla volta i seguenti tipi documenti, con l'estensione specificata")
    public void uploadDocumentsByTypeAndExtension(DataTable dataTable) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        List<UploadRequest> requests = uploadSupport.requestsFromTable(dataTable.asMaps(), "documento", "estensione");
        uploadSupport.executeUploads(requests, uploadAttempts, uploadOps);
    }

    @Then("tutti i tentativi di caricamento hanno esito {expectedOutcome}")
    public void verifyUploadAttemptsOutcome(ExpectedOutcome expectedOutcome) {
        uploadSupport.verifyUploadAttemptsOutcome(uploadAttempts, expectedOutcome);
    }

    @When("l'utente tenta di caricare uno alla volta il seguente insieme di documenti come interfaccia del descriptor")
    public void uploadInterfaceByTypeList(List<String> fileTypes) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        uploadSupport.executeUploads(uploadSupport.requestsFromTypeList(fileTypes), interfaceUploadAttempts, interfaceUploadOps);
    }

    @When("l'utente tenta di caricare uno alla volta i seguenti tipi documenti come interfaccia del descriptor, con l'estensione specificata")
    public void uploadInterfaceByTypeAndExtension(DataTable dataTable) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        List<UploadRequest> requests = uploadSupport.requestsFromTable(dataTable.asMaps(), "documento", "estensione");
        uploadSupport.executeUploads(requests, interfaceUploadAttempts, interfaceUploadOps);
    }

    @When("l'utente carica il documento di interfaccia predefinito")
    public void uploadDefaultInterfaceDocument() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        List<UploadRequest> requests = List.of(new UploadRequest("interface", "yaml"));
        uploadSupport.executeUploads(requests, interfaceUploadAttempts, interfaceUploadOps);
    }

    @Then("tutti i tentativi di caricamento come interfaccia del descriptor hanno esito {expectedOutcome}")
    public void verifyInterfaceUploadAttemptsOutcome(ExpectedOutcome expectedOutcome) {
        uploadSupport.verifyUploadAttemptsOutcome(interfaceUploadAttempts, expectedOutcome);
    }
}

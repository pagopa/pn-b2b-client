package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservices;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.IM2MV3EserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
public class EServiceAsyncExchangeCallbackInterfaceSteps {

    private final IHttpExecutor httpExecutor;
    private final IM2MV3EserviceClient eServiceClient;
    private final BlobFileCreator blobFileCreator;
    private final SharedStepsContext sharedStepsContext;

    public EServiceAsyncExchangeCallbackInterfaceSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            BlobFileCreator blobFileCreator,
            DelayService delayService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceClient = clientTokenConfigurator.getM2mV3EserviceClient();
        this.eServiceClient.setHttpCallExecutor(httpExecutor);
        this.blobFileCreator = blobFileCreator;
    }

    @When("l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore")
    public void uploadCallbackInterface() {
        this.uploadAsyncExchangeCallbackInterface(
                "asyncExchangeCallbackInterface.yaml",
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
        "origin-interface.yaml"
        );
    }

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono per un e-service inesistente")
    public void uploadCallbackInterfaceToNonExistentEService() {
        this.uploadAsyncExchangeCallbackInterface(
                "asyncExchangeCallbackInterface.yaml",
                UUID.randomUUID(), UUID.randomUUID(),
                "origin-interface.yaml"
        );
    }

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono per un descrittore inesistente di un e-service")
    public void uploadCallbackInterfaceToNonExistentDescriptor() {
        this.uploadAsyncExchangeCallbackInterface(
                "asyncExchangeCallbackInterface.yaml",
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                UUID.randomUUID(),
                "origin-interface.yaml"
        );
    }

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono di tipo YAML {string}")
    public void uploadInvalidCallbackInterface(String versionState) {
        String filename = switch (versionState) {
            case "senza versione" -> "missing-version-interface.yaml";
            case "con versione obsoleta" -> "invalid-version-interface.yaml";
            case "vuoto" -> "empty-interface.yaml";
            default -> throw new IllegalStateException("Unexpected value: " + versionState);
        };

        this.uploadAsyncExchangeCallbackInterface(
                "asyncExchangeCallbackInterface.yaml",
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                filename
        );
    }

    @When("l'utente carica un'interfaccia di callback di scambio asincrono di tipo {string}")
    public void uploadCallbackInterfaceOfType(String fileType) {
        String fileName = String.format("interface.%s", fileType);
        String interfaceName = String.format("asyncExchangeCallbackInterface.%s", fileType);
        this.uploadAsyncExchangeCallbackInterface(
                interfaceName,
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                fileName
        );
    }

    @When("l'utente carica un'interfaccia di callback di scambio asincrono di tipo {string} che contiene il termine localhost")
    public void uploadCallbackInterfaceWithLocalhost(String fileType) {
        String fileName = String.format("localhost-interface.%s", fileType);
        this.uploadAsyncExchangeCallbackInterface(
                "asyncExchangeCallbackInterface.yaml",
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                fileName
        );
    }

    @When("l'utente tenta di effettuare la rimozione dell'interfaccia di callback di scambio asincrono dell'e-service")
    public void deleteCallbackInterface() {
        this.deleteAsyncExchangeCallbackInterface(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
    }

    @When("l'utente tenta di effettuare la rimozione di un'interfaccia di callback di scambio asincrono per un e-service inesistente")
    public void deleteCallbackInterfaceToNonExistentEService() {
        this.deleteAsyncExchangeCallbackInterface(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta di effettuare la rimozione di un'interfaccia di callback di scambio asincrono per un descrittore inesistente di un e-service")
    public void deleteCallbackInterfaceToNonExistentDescriptor() {
        this.deleteAsyncExchangeCallbackInterface(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                UUID.randomUUID()
        );
    }

    @When("l'utente effettua il download dell'interfaccia di callback di scambio asincrono per quel descrittore")
    public void downloadCallbackInterface() {
        this.downloadAsyncExchangeCallbackInterface(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
    }

    private void uploadAsyncExchangeCallbackInterface(String asyncExchangeCallbackInterfaceName, UUID eServiceId, UUID descriptorId, String fileName) {
        String filePath = String.format("src/main/resources/%s", fileName);
        Resource resource = blobFileCreator.createBlobFile(filePath, fileName);
        sharedStepsContext.getEServicesCommonContext().setCallbackInterfaceName(asyncExchangeCallbackInterfaceName);
        IM2MEserviceClient.EServiceInterfaceUploadRequest request = new IM2MEserviceClient.EServiceInterfaceUploadRequest()
            .prettyName(asyncExchangeCallbackInterfaceName)
            .resource(resource)
            .eServiceId(eServiceId)
            .descriptorId(descriptorId);
        httpExecutor.performCall(() -> eServiceClient.uploadAsyncExchangeCallbackInterface(request));
    }

    private void deleteAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId) {
        httpExecutor.performCall(() -> eServiceClient.deleteEServiceDescriptorAsyncExchangeCallbackInterface(eServiceId, descriptorId));
    }

    private String downloadAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId) {

        httpExecutor.performCall(() -> eServiceClient.downloadEServiceDescriptorAsyncExchangeCallbackInterface(
                eServiceId, descriptorId
        ));

        if (!httpExecutor.getResponseStatus().is2xxSuccessful()) {
            return null;
        }

        try {
            FileDownloadMultipart descriptorInterface = (FileDownloadMultipart) httpExecutor.getResponse();
            byte[] actualInterface = Files.readAllBytes(descriptorInterface.getFile().toPath());
            return new String(actualInterface, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Errore durante il download dell'interfaccia di callback di scambio asincrono", e);
            return null;
        }
    }
}

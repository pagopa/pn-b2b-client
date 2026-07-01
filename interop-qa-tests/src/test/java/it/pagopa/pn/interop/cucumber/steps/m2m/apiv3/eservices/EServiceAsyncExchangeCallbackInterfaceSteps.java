package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservices;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.IM2MV3EserviceClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import org.springframework.core.io.Resource;
import java.util.UUID;

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

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono di tipo YAML {string}")
    public void uploadInvalidCallbackInterface(String versionState) {
        String filename = switch (versionState) {
            case "senza versione" -> "missing-version-interface.yaml";
            case "con versione obsoleta" -> "invalid-version-interface.yaml";
            case "senza contenuto" -> "empty-interface.yaml";
            case "di dimensione non consentita" -> "too-large-interface.yaml";
            default -> throw new IllegalStateException("Unexpected value: " + versionState);
        };

        this.uploadAsyncExchangeCallbackInterface(
                "asyncExchangeCallbackInterface.yaml",
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                filename
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
}

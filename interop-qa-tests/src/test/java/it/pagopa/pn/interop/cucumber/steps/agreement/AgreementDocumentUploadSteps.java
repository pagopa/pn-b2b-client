package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import java.time.LocalDateTime;
import org.springframework.core.io.Resource;

public class AgreementDocumentUploadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BlobFileCreator blobFileCreator;

    public AgreementDocumentUploadSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext,
                                        BlobFileCreator blobFileCreator) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.blobFileCreator = blobFileCreator;
    }

    @When("l'utente carica un documento allegato a quella richiesta di fruizione")
    public void uploadAgreementAttachment() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String name = "documento-test-qa.pdf";
        String prettyName = "documento-test-qa";
        LocalDateTime now = LocalDateTime.now();
        Resource doc = blobFileCreator.createBlobFile("src/main/resources/dummy.pdf", name);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().addAgreementConsumerDocument(sharedStepsContext.getAgreementId(),
                    name, prettyName, doc)
        );
        if (sharedStepsContext.getHttpCallExecutor().getClientResponse().is2xxSuccessful()) {
            sharedStepsContext.getAgreementCommonContext().addDocumentMetadata(
                DocumentMetadata.builder()
                    .name(name)
                    .prettyName(prettyName)
                    .createdAt(now)
                    .build());
        }
    }
}

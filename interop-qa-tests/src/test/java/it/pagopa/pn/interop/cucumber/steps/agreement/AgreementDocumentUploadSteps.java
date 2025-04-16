package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
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
        Resource doc = blobFileCreator.createBlobFile("src/main/resources/dummy.pdf", "documento-test-qa.pdf");
        clientTokenConfigurator.getAgreementClient().addAgreementConsumerDocument(
                sharedStepsContext.getXCorrelationId(), sharedStepsContext.getAgreementId(),
                "documento-test-qa.pdf", "documento-test-qa", doc);

    }
}

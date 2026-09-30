package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import java.time.OffsetDateTime;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
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
        String prettyName = "documento-test-qa";
        Resource doc = new FileSystemResource("src/main/resources/dummy.pdf");
        String name = FilenameUtils.getName(doc.getFilename());
        sharedStepsContext.getHttpCallExecutor().performCall(
            () -> clientTokenConfigurator.getAgreementClient().addAgreementConsumerDocument(sharedStepsContext.getAgreementCommonContext().getAgreementId(),
                name, prettyName, doc)
        );
    }

    @When("l'utente carica un documento allegato a quella richiesta di fruizione con successo")
    public void successfullyUploadAgreementAttachment() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String prettyName = "documento-test-qa";
        OffsetDateTime now = OffsetDateTime.now();
        Resource doc = new FileSystemResource("src/main/resources/dummy.pdf");
        String name = FilenameUtils.getName(doc.getFilename());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().addAgreementConsumerDocument(sharedStepsContext.getAgreementCommonContext().getAgreementId(),
                    name, prettyName, doc)
        );
        if (sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getAgreementCommonContext().addDocumentMetadata(
                DocumentMetadata.builder()
                    .name(name)
                    .prettyName(prettyName)
                    .createdAt(now)
                    .build());
        } else {
            throw new IllegalStateException("Errore durante il caricamento del documento. Visionare logs per maggiori dettagli.");
        }
    }
}

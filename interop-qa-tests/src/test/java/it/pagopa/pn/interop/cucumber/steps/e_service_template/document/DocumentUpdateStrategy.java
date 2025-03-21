package it.pagopa.pn.interop.cucumber.steps.e_service_template.document;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import java.util.UUID;

public interface DocumentUpdateStrategy {
    static DocumentUpdateStrategy from(EServiceTemplateDocumentKind documentKind1, EServiceTemplateDocumentKind documentKind2) {
        if(documentKind1 == documentKind2 && documentKind2 == EServiceTemplateDocumentKind.DOCUMENT) {
            return new DocumentUpdateDocDocStrategy();
        } else if(documentKind1 != documentKind2) {
            return new DocumentUpdateDocIntStrategy();
        } else {
            throw new IllegalArgumentException("Non è possibile creare una strategia di aggiornamento documento per i tipi %s e %s"
                .formatted(documentKind1, documentKind2));
        }
    }

    boolean hasExpectedDocuments(EServiceTemplateVersionDetails version);

    UUID getDocumentToUpdate(EServiceTemplateVersionDetails version);

    UpdateEServiceTemplateVersionDocumentSeed buildDocumentUpdateSeed(
        EServiceTemplateVersionDetails version);

    /* DEV. NOTE 21/03/2025: le seguenti implementazioni sono strettamente dipendenti dall'attuale
     * assetto degli step Cucumber in cui vengono utilizzate queste classi */

    class DocumentUpdateDocDocStrategy implements DocumentUpdateStrategy {

        @Override
        public boolean hasExpectedDocuments(EServiceTemplateVersionDetails version) {
            return version.getDocs().size() >= 2;
        }

        @Override
        public UUID getDocumentToUpdate(EServiceTemplateVersionDetails version) {
            return version.getDocs().get(0).getId();
        }

        @Override
        public UpdateEServiceTemplateVersionDocumentSeed buildDocumentUpdateSeed(
            EServiceTemplateVersionDetails version) {
            return new UpdateEServiceTemplateVersionDocumentSeed()
                .prettyName(version.getDocs().get(1).getPrettyName());
        }
    }

    class DocumentUpdateDocIntStrategy implements DocumentUpdateStrategy {

        @Override
        public boolean hasExpectedDocuments(EServiceTemplateVersionDetails version) {
            return version.getDocs().size() == 1 && notEmpty(version.getInterface());
        }

        @Override
        public UUID getDocumentToUpdate(EServiceTemplateVersionDetails version) {
            EServiceDoc theInterface = version.getInterface();
            if(isNull(theInterface)) {
                throw new IllegalStateException("L'e-service template %s non ha un documento di tipo interfaccia"
                    .formatted(version.getEserviceTemplate().getName()));
            }

            return theInterface.getId();
        }

        @Override
        public UpdateEServiceTemplateVersionDocumentSeed buildDocumentUpdateSeed(
            EServiceTemplateVersionDetails version) {
            return new UpdateEServiceTemplateVersionDocumentSeed()
                .prettyName(version.getDocs().get(0).getPrettyName());
        }

        @SuppressWarnings("all")
        private boolean notEmpty(EServiceDoc doc) {
            return nonNull(doc) && nonNull(doc.getId());
        }
    }
}

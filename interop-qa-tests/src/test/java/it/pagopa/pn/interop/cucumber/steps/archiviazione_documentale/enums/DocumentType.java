package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import lombok.Getter;

@Getter
public enum DocumentType {
    AGREEMENT_ACTIVATED("pdf" ),
    AGREEMENT_UPGRADED("pdf"),
    PURPOSE_ACTIVATED("pdf"),
    NEW_PURPOSE_VERSION_ACTIVATED("pdf"),
    PURPOSE_VERSION_ACTIVATED("pdf"),
    PRODUCER_DELEGATION_APPROVED("pdf"),
    CONSUMER_DELEGATION_APPROVED("pdf"),
    CONSUMER_DELEGATION_REVOKED("pdf"),
    PRODUCER_DELEGATION_REVOKED("pdf"),
    PURPOSE_TEMPLATE_PUBLISHED("pdf");

    private final String extension;

    DocumentType(String extension) {
        this.extension = extension;
    }
}

package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import lombok.Getter;

@Getter
public enum FileType {
    // Documenti
    AGREEMENT_ACTIVATED("pdf", "name" ),
    AGREEMENT_UPGRADED("pdf", "name"),
    PURPOSE_ACTIVATED("pdf", "name"),
    NEW_PURPOSE_VERSION_ACTIVATED("pdf", "name"),
    PURPOSE_VERSION_ACTIVATED("pdf", "name"),
    PRODUCER_DELEGATION_APPROVED("pdf", "name"),
    CONSUMER_DELEGATION_APPROVED("pdf", "name"),
    CONSUMER_DELEGATION_REVOKED("pdf", "name"),
    PRODUCER_DELEGATION_REVOKED("pdf", "name"),
    PURPOSE_TEMPLATE_PUBLISHED("pdf", "name"),

    // Eventi (potrebbero mancare RISK_ANALYSIS_DOCUMENT_ADDED, AGREEMENT_CONTRACT_ADDED)
    VOUCHER_EVENT("zip", "name"),
    KEYS_ADDED_EVENT("zip", "name"),
    KEY_DELETED_EVENT("zip", "name"),
    CLIENT_DELETED_EVENT("zip", "name"),
    AGREEMENT_UPGRADED_EVENT("zip", "name"),
    PURPOSE_UPGRADED_EVENT("zip", "name"),
    DESCRIPTOR_ESERVICE_UPGRADED_EVENT("zip", "name"),
    PRODUCER_DELEGATION_APPROVED_EVENT("zip", "name"),
    CONSUMER_DELEGATION_APPROVED_EVENT("zip", "name"),
    CONSUMER_DELEGATION_REVOKED_EVENT("zip", "name"),
    PRODUCER_DELEGATION_REVOKED_EVENT("zip", "name");

    private final String extension;
    private final String expectedBaseName; //nome del file (ignorando il timestamp)

    FileType(String extension, String expectedBaseName) {
        this.extension = extension;
        this.expectedBaseName = expectedBaseName;
    }
}

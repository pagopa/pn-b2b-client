package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import lombok.Getter;

@Getter
public enum FileType {
    // Documenti
    AGREEMENT_ACTIVATED("pdf" ),
    AGREEMENT_UPGRADED("pdf"),
    PURPOSE_ACTIVATED("pdf"),
    NEW_PURPOSE_VERSION_ACTIVATED("pdf"),
    PURPOSE_VERSION_ACTIVATED("pdf"),
    PRODUCER_DELEGATION_APPROVED("pdf"),
    CONSUMER_DELEGATION_APPROVED("pdf"),
    CONSUMER_DELEGATION_REVOKED("pdf"),
    PRODUCER_DELEGATION_REVOKED("pdf"),
    PURPOSE_TEMPLATE_PUBLISHED("pdf"),

    // Eventi (potrebbero mancare RISK_ANALYSIS_DOCUMENT_ADDED, AGREEMENT_CONTRACT_ADDED)
    VOUCHER_EVENT("zip"),
    KEYS_ADDED_EVENT("zip"),
    KEY_DELETED_EVENT("zip"),
    CLIENT_DELETED_EVENT("zip"),
    AGREEMENT_UPGRADED_EVENT("zip"),
    PURPOSE_UPGRADED_EVENT("zip"),
    DESCRIPTOR_ESERVICE_UPGRADED_EVENT("zip"),
    PRODUCER_DELEGATION_APPROVED_EVENT("zip"),
    CONSUMER_DELEGATION_APPROVED_EVENT("zip"),
    CONSUMER_DELEGATION_REVOKED_EVENT("zip"),
    PRODUCER_DELEGATION_REVOKED_EVENT("zip");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }
}

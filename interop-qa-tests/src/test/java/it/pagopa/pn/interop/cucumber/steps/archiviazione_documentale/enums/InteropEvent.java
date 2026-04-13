package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteropEvent {
    ESERVICE_ADDED("EServiceAdded"),
    ESERVICE_DESCRIPTOR_PUBLISHED("EServiceDescriptorPublished"),
    DRAFT_ESERVICE_UPDATED("DraftEServiceUpdated"),
    AGREEMENT_ACTIVATED("AgreementActivated"),
    AGREEMENT_UPGRADED("AgreementUpgraded"),
    AGREEMENT_SUSPENDED_BY_CONSUMER("AgreementSuspendedByConsumer"),
    AGREEMENT_ARCHIVED_BY_CONSUMER("AgreementArchivedByConsumer"),
    PURPOSE_ACTIVATED("PurposeActivated"),
    NEW_PURPOSE_VERSION_ACTIVATED("NewPurposeVersionActivated"),
    PURPOSE_VERSION_ACTIVATED("PurposeVersionActivated"),
    PRODUCER_DELEGATION_APPROVED("ProducerDelegationApproved"),
    CONSUMER_DELEGATION_APPROVED("ConsumerDelegationApproved"),
    PRODUCER_DELEGATION_REVOKED("ProducerDelegationRevoked"),
    CONSUMER_DELEGATION_REVOKED("ConsumerDelegationRevoked"),
    CLIENT_KEY_DELETED("ClientKeyDeleted"),
    DESCRIPTOR_ESERVICE_UPGRADED("DescriptorEServiceUpgraded"),
    CLIENT_DELETE("ClientDelete"),
    CLIENT_KEY_ADDED("ClientKeyAdded"),
    RISK_ANALYSIS_TEMPLATE_DOCUMENT_GENERATED("RiskAnalysisTemplateDocumentGenerated"),
    PURPOSE_TEMPLATE_SUSPENDED("PurposeTemplateSuspended"),
    PURPOSE_TEMPLATE_UNSUSPENDED("PurposeTemplateUnsuspended"),
    PURPOSE_TEMPLATE_ARCHIVED("PurposeTemplateArchived"),
    PURPOSE_TEMPLATE_PUBLISHED("PurposeTemplatePublished");

    private final String value;

    public static InteropEvent fromValue(String value) {
        for (InteropEvent interopEvent : InteropEvent.values()) {
            if (interopEvent.value.equals(value)) {
                return interopEvent;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }
}

package it.pagopa.interop.event.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteropEvent {
    ESERVICE_ADDED("EServiceAdded", Family.ESERVICE),
    ESERVICE_DESCRIPTOR_PUBLISHED("EServiceDescriptorPublished", Family.ESERVICE),
    ESERVICE_DESCRIPTOR_APPROVED_BY_DELEGATOR("EServiceDescriptorApprovedByDelegator", Family.ESERVICE),
    ESERVICE_DESCRIPTOR_ADDED("EServiceDescriptorAdded", Family.ESERVICE),
    ESERVICE_DESCRIPTOR_ASYNC_EXCHANGE_CALLBACK_INTERFACE_ADDED("EServiceDescriptorAsyncExchangeCallbackInterfaceAdded", Family.ESERVICE),
    ESERVICE_DESCRIPTOR_ASYNC_EXCHANGE_CALLBACK_INTERFACE_UPDATED("EServiceDescriptorAsyncExchangeCallbackInterfaceUpdated", Family.ESERVICE),
    ESERVICE_DESCRIPTOR_ASYNC_EXCHANGE_CALLBACK_INTERFACE_DELETED("EServiceDescriptorAsyncExchangeCallbackInterfaceDeleted", Family.ESERVICE),
    ESERVICE_TEMPLATE_VERSION_ASYNC_EXCHANGE_CALLBACK_INTERFACE_ADDED("EServiceTemplateVersionAsyncExchangeCallbackInterfaceAdded", Family.ESERVICE_TEMPLATE),
    ESERVICE_TEMPLATE_VERSION_ASYNC_EXCHANGE_CALLBACK_INTERFACE_UPDATED("EServiceTemplateVersionAsyncExchangeCallbackInterfaceUpdated", Family.ESERVICE_TEMPLATE),
    ESERVICE_TEMPLATE_VERSION_ASYNC_EXCHANGE_CALLBACK_INTERFACE_DELETED("EServiceTemplateVersionAsyncExchangeCallbackInterfaceDeleted", Family.ESERVICE_TEMPLATE),
    DRAFT_ESERVICE_UPDATED("DraftEServiceUpdated", Family.ESERVICE),
    AGREEMENT_ADDED("AgreementAdded", Family.AGREEMENT),
    AGREEMENT_SUBMITTED("AgreementSubmitted", Family.AGREEMENT),
    AGREEMENT_ACTIVATED("AgreementActivated", Family.AGREEMENT),
    AGREEMENT_UPGRADED("AgreementUpgraded", Family.AGREEMENT),
    AGREEMENT_SUSPENDED_BY_CONSUMER("AgreementSuspendedByConsumer", Family.AGREEMENT),
    AGREEMENT_ARCHIVED_BY_CONSUMER("AgreementArchivedByConsumer", Family.AGREEMENT),
    PURPOSE_ACTIVATED("PurposeActivated", Family.PURPOSE),
    NEW_PURPOSE_VERSION_ACTIVATED("NewPurposeVersionActivated", Family.PURPOSE),
    PURPOSE_VERSION_ACTIVATED("PurposeVersionActivated", Family.PURPOSE),
    PRODUCER_DELEGATION_APPROVED("ProducerDelegationApproved", Family.PRODUCER_DELEGATION),
    CONSUMER_DELEGATION_APPROVED("ConsumerDelegationApproved", Family.CONSUMER_DELEGATION),
    PRODUCER_DELEGATION_REVOKED("ProducerDelegationRevoked", Family.PRODUCER_DELEGATION),
    CONSUMER_DELEGATION_REVOKED("ConsumerDelegationRevoked", Family.CONSUMER_DELEGATION),
    CLIENT_KEY_DELETED("ClientKeyDeleted", Family.KEY),
    DESCRIPTOR_ESERVICE_UPGRADED("DescriptorEServiceUpgraded", Family.ESERVICE),
    CLIENT_DELETE("ClientDelete", Family.CLIENT),
    CLIENT_KEY_ADDED("ClientKeyAdded", Family.KEY),
    RISK_ANALYSIS_TEMPLATE_DOCUMENT_GENERATED("RiskAnalysisTemplateDocumentGenerated", Family.PURPOSE_TEMPLATE),
    PURPOSE_TEMPLATE_SUSPENDED("PurposeTemplateSuspended", Family.PURPOSE_TEMPLATE),
    PURPOSE_TEMPLATE_ADDED("PurposeTemplateAdded", Family.PURPOSE_TEMPLATE),
    PURPOSE_TEMPLATE_UNSUSPENDED("PurposeTemplateUnsuspended", Family.PURPOSE_TEMPLATE),
    PURPOSE_TEMPLATE_ARCHIVED("PurposeTemplateArchived", Family.PURPOSE_TEMPLATE),
    PURPOSE_TEMPLATE_PUBLISHED("PurposeTemplatePublished", Family.PURPOSE_TEMPLATE);

    private final String value;
    private final Family family;

    public static InteropEvent fromValue(String value) {
        for (InteropEvent interopEvent : InteropEvent.values()) {
            if (interopEvent.value.equals(value)) {
                return interopEvent;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }

    public static InteropEvent fromValueAndFamily(String value, String family) {
        for (InteropEvent interopEvent : InteropEvent.values()) {
            if (interopEvent.value.equals(value) && interopEvent.family.name().equals(family)) {
                return interopEvent;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value + " and family " + family);
    }

    public enum Family {
        PURPOSE_TEMPLATE,
        ESERVICE,
        AGREEMENT,
        PURPOSE,
        CONSUMER_DELEGATION,
        PRODUCER_DELEGATION,
        CLIENT,
        KEY,
        ESERVICE_TEMPLATE,
        ATTRIBUTE,
        TENANT,
        PRODUCER_KEY,
        PRODUCER_KEYCHAIN
    }
}

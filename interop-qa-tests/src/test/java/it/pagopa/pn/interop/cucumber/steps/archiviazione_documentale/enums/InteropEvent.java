package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteropEvent {
    AGREEMENT_ACTIVATED("AgreementActivated"),
    AGREEMENT_UPGRADED("AgreementUpgraded"),
    AGREEMENT_SUSPENDED_BY_CONSUMER("AgreementSuspendedByConsumer"),
    AGREEMENT_ARCHIVED_BY_CONSUMER("AgreementArchivedByConsumer"),
    PURPOSE_ACTIVATED("PurposeActivated "),
    NEW_PURPOSE_VERSION_ACTIVATED("NewPurposeVersionActivated "),
    PURPOSE_VERSION_ACTIVATED("PurposeVersionActivated "),
    PRODUCER_DELEGATION_APPROVED("ProducerDelegationApproved "),
    CONSUMER_DELEGATION_APPROVED("ConsumerDelegationApproved "),
    PRODUCER_DELEGATION_REVOKED("ProducerDelegationRevoked  "),
    CONSUMER_DELEGATION_REVOKED("ConsumerDelegationRevoked");

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

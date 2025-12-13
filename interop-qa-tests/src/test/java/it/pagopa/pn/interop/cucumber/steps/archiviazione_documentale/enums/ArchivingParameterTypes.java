package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import io.cucumber.java.ParameterType;

public class ArchivingParameterTypes {
    @ParameterType("AgreementActivated|AgreementActivatedEvent|AgreementActivatedEventSigned|AgreementActivatedSigned|AgreementUpgraded|PurposeActivated|PurposeActivatedSigned|NewPurposeVersionActivated|PurposeVersionActivated|ProducerDelegationApproved|ConsumerDelegationApproved|ConsumerDelegationApprovedSigned|ConsumerDelegationRevoked|ProducerDelegationRevoked|PurposeTemplatePublished|KeysAddedEvent|KeyDeletedEvent|ClientDeletedEvent|AgreementUpgradedEvent|DescriptorEserviceUpgradedEvent|ProducerDelegationApprovedEvent|ConsumerDelegationApprovedEvent|ConsumerDelegationApprovedEventSigned|ProducerDelegationRevokedEvent|ConsumerDelegationRevokedEvent|VoucherEvent|PurposeUpgradedEvent")
    public FileType documentType(String type) {
        return FileType.valueOf(type.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase());
    }

    @ParameterType("signed|SIGNED|unsigned|UNSIGNED")
    public boolean bucketType(String type) {
        switch (type) {
            case "signed", "SIGNED" -> {
                return true;
            }
            case "unsigned", "UNSIGNED" -> {
                return false;
            }
            default -> throw new IllegalArgumentException("Tipo di bucket non riconosciuto: " + type);
        }
    }
}

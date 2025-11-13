package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import io.cucumber.java.ParameterType;

public class ArchivingParameterTypes {
    @ParameterType("AgreementActivated|AgreementUpgraded|PurposeActivated|NewPurposeVersionActivated|PurposeVersionActivated|ProducerDelegationApproved|ConsumerDelegationApproved|ConsumerDelegationRevoked|ProducerDelegationRevoked|PurposeTemplatePublished|KeysAddedEvent|KeyDeletedEvent|ClientDeletedEvent|AgreementUpgradedEvent|DescriptorEserviceUpgradedEvent|ProducerDelegationApprovedEvent|ConsumerDelegationApprovedEvent|ProducerDelegationRevokedEvent|ConsumerDelegationRevokedEvent|VoucherEvent|PurposeUpgradedEvent")
    public FileType documentType(String type) {
        return FileType.valueOf(type);
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

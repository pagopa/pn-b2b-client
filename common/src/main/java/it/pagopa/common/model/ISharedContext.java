package it.pagopa.common.model;

public interface ISharedContext {
    String getAgreementId();
    String getEServiceName();
    String getOldEServiceName();
    String getEServiceId();
    String getDescriptorId();
    String getOldDescriptorId();
    String getProducerName();
    String getTemplateProducerName();
    String getConsumerName();
    String getPurposeId();
    String getNewPurposeId();
    String getPurposeTitle();
    String getEServiceTemplateId();
    String getEServiceTemplateVersionId();
    String getEServiceTemplateName();
    String getNewEServiceTemplateName();
    String getDocumentName();
    String getAttributeName();
    String getKeychainId();
    String getKeychainName();
    String getProducerKeyName();
    String getClientId();
    String getClientName();
    String getNewKeyId();
    String getDelegationId();
    String getDelegateName();
    String getCertifierName();
}

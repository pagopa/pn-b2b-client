package it.pagopa.common.model;

public interface ISharedContext {
    String getAgreementId();
    String getEServiceName();
    String getEServiceId();
    String getDescriptorId();
    String getOldDescriptorId();
    String getProducerName();
    String getConsumerName();
    String getPurposeId();
    String getPurposeTitle();
}

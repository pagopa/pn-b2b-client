package it.pagopa.interop.event.mapper;

import it.pagopa.interop.event.domain.dto.*;
import it.pagopa.interop.event.domain.dto.events.*;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface M2MV3EventMapper {

    @Mapping(target = "eserviceId", source = "eserviceId")
    @Mapping(target = "producerDelegationId", source = "producerDelegationId")
    @Mapping(target = "descriptorId", source = "descriptorId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MEserviceEvent map(EServiceEvent event);

    @Mapping(target = "agreementId", source = "agreementId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "producerDelegationId", source = "producerDelegationId")
    @Mapping(target = "consumerDelegationId", source = "consumerDelegationId")
    M2MAgreementEvent map(AgreementEvent event);

    @Mapping(target = "attributeId", source = "attributeId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MAttributeEvent map(AttributeEvent event);

    @Mapping(target = "purposeId", source = "purposeId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "producerDelegationId", source = "producerDelegationId")
    @Mapping(target = "consumerDelegationId", source = "consumerDelegationId")
    M2MPurposeEvent map(PurposeEvent event);

    @Mapping(target = "tenantId", source = "tenantId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MTenantEvent map(TenantEvent event);

    @Mapping(target = "eserviceTemplateId", source = "eserviceTemplateId")
    @Mapping(target = "eserviceTemplateVersionId", source = "eserviceTemplateVersionId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MEServiceTemplateEvent map(EServiceTemplateEvent event);

    @Mapping(target = "kid", source = "kid")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MKeyEvent map(KeyEvent event);

    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MClientEvent map(ClientEvent event);

    @Mapping(target = "kid", source = "kid")
    @Mapping(target = "producerKeychainId", source = "producerKeychainId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MProducerKeyEvent map(ProducerKeyEvent event);

    @Mapping(target = "producerKeychainId", source = "producerKeychainId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MProducerKeychainEvent map(ProducerKeychainEvent event);

    @Mapping(target = "delegationId", source = "delegationId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MProducerDelegationEvent map(ProducerDelegationEvent event);

    @Mapping(target = "delegationId", source = "delegationId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MConsumerDelegationEvent map(ConsumerDelegationEvent event);

    M2MEServiceEvents map(EServiceEvents event);
    M2MAgreementEvents map(AgreementEvents event);
    M2MAttributeEvents map(AttributeEvents event);
    M2MPurposeEvents map(PurposeEvents event);
    M2MTenantEvents map(TenantEvents event);
    M2MEServiceTemplateEvents map(EServiceTemplateEvents event);
    M2MKeyEvents map(KeyEvents event);
    M2MClientEvents map(ClientEvents event);
    M2MProducerKeyEvents map(ProducerKeyEvents event);
    M2MProducerKeychainEvents map(ProducerKeychainEvents event);
    M2MProducerDelegationEvents map(ProducerDelegationEvents event);
    M2MConsumerDelegationEvents map(ConsumerDelegationEvents event);
    //TODO: decommentare per la feature di eventi finalità agevoltata
    //M2MPurposeTemplateEvents map(PurposeTemplateEvents event);

    /* TODO 28/11/2025: questa logica di conversione compare identica altrove in più occasioni.
        Andrebbe incapsulata in una utility condivisa. */
    default OffsetDateTime map(String timestamp) {
        return OffsetDateTime.parse(timestamp);
    }

}
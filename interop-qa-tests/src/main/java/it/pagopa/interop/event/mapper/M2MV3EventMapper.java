package it.pagopa.interop.event.mapper;

import it.pagopa.interop.event.domain.dto.*;
import it.pagopa.interop.event.domain.dto.events.*;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface M2MV3EventMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eserviceId", source = "eserviceId")
    @Mapping(target = "producerDelegationId", source = "producerDelegationId")
    @Mapping(target = "descriptorId", source = "descriptorId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MEserviceEvent map(EServiceEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "agreementId", source = "agreementId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "producerDelegationId", source = "producerDelegationId")
    @Mapping(target = "consumerDelegationId", source = "consumerDelegationId")
    M2MAgreementEvent map(AgreementEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "attributeId", source = "attributeId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MAttributeEvent map(AttributeEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "purposeId", source = "purposeId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "producerDelegationId", source = "producerDelegationId")
    @Mapping(target = "consumerDelegationId", source = "consumerDelegationId")
    M2MPurposeEvent map(PurposeEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "tenantId", source = "tenantId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MTenantEvent map(TenantEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eserviceTemplateId", source = "eserviceTemplateId")
    @Mapping(target = "eserviceTemplateVersionId", source = "eserviceTemplateVersionId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MEServiceTemplateEvent map(EServiceTemplateEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "kid", source = "kid")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MKeyEvent map(KeyEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MClientEvent map(ClientEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "kid", source = "kid")
    @Mapping(target = "producerKeychainId", source = "producerKeychainId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MProducerKeyEvent map(ProducerKeyEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "producerKeychainId", source = "producerKeychainId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MProducerKeychainEvent map(ProducerKeychainEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "delegationId", source = "delegationId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MProducerDelegationEvent map(ProducerDelegationEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "delegationId", source = "delegationId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MConsumerDelegationEvent map(ConsumerDelegationEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "purposeTemplateId", source = "purposeTemplateId")
    @Mapping(target = "eventTimestamp", source = "eventTimestamp")
    @Mapping(target = "eventType", source = "eventType")
    M2MPurposeTemplateEvent map(PurposeTemplateEvent event);

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
    M2MPurposeTemplateEvents map(PurposeTemplateEvents event);

    default Instant map(String timestamp) {
        return Instant.parse(timestamp);
    }
}
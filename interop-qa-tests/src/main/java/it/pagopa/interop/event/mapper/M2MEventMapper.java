package it.pagopa.interop.event.mapper;

import it.pagopa.interop.event.domain.M2MEvent;
import it.pagopa.interop.event.domain.M2MEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AttributeEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AttributeEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ClientEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ClientEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegationEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegationEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.KeyEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.KeyEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegationEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegationEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeyEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeyEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeychainEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeychainEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantEvents;
import java.time.OffsetDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface M2MEventMapper {

    @Mapping(target = "resourceId", source = "eserviceId")
    @Mapping(target = "subResourceId", source = "descriptorId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "consumerDelegationId", ignore = true)
    M2MEvent map(EServiceEvent event);

    @Mapping(target = "resourceId", source = "agreementId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    M2MEvent map(AgreementEvent event);


    @Mapping(target = "resourceId", source = "attributeId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    @NoDelegations
    M2MEvent map(AttributeEvent event);

    @Mapping(target = "resourceId", source = "purposeId")
    @Mapping(target = "subResourceId", source = "purposeVersionId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    M2MEvent map(PurposeEvent event);

    @Mapping(target = "resourceId", source = "tenantId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    @NoDelegations
    M2MEvent map(TenantEvent event);

    @Mapping(target = "resourceId", source = "eserviceTemplateId")
    @Mapping(target = "subResourceId", source = "eserviceTemplateVersionId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @NoDelegations
    M2MEvent map(EServiceTemplateEvent event);

    @Mapping(target = "resourceId", source = "kid")
    @Mapping(target = "subResourceId", source = "clientId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @NoDelegations
    M2MEvent map(KeyEvent event);

    @Mapping(target = "resourceId", source = "clientId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    @NoDelegations
    M2MEvent map(ClientEvent event);

    @Mapping(target = "resourceId", source = "kid")
    @Mapping(target = "subResourceId", source = "producerKeychainId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @NoDelegations
    M2MEvent map(ProducerKeyEvent event);

    @Mapping(target = "resourceId", source = "producerKeychainId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    @NoDelegations
    M2MEvent map(ProducerKeychainEvent event);

    @Mapping(target = "resourceId", source = "delegationId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    @NoDelegations
    M2MEvent map(ProducerDelegationEvent event);

    @Mapping(target = "resourceId", source = "delegationId")
    @Mapping(target = "creationTimestamp", source = "eventTimestamp")
    @Mapping(target = "subResourceId", ignore = true)
    @NoDelegations
    M2MEvent map(ConsumerDelegationEvent event);

    M2MEvents map(EServiceEvents event);
    M2MEvents map(AgreementEvents event);
    M2MEvents map(AttributeEvents event);
    M2MEvents map(PurposeEvents event);
    M2MEvents map(TenantEvents event);
    M2MEvents map(EServiceTemplateEvents event);
    M2MEvents map(KeyEvents event);
    M2MEvents map(ClientEvents event);
    M2MEvents map(ProducerKeyEvents event);
    M2MEvents map(ProducerKeychainEvents event);
    M2MEvents map(ProducerDelegationEvents event);
    M2MEvents map(ConsumerDelegationEvents event);

    /* TODO 28/11/2025: questa logica di conversione compare identica altrove in più occasioni.
        Andrebbe incapsulata in una utility condivisa. */
    default OffsetDateTime map(String timestamp) {
        return OffsetDateTime.parse(timestamp);
    }

}
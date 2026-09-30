package it.pagopa.interop.event.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.domain.dto.events.*;
import it.pagopa.interop.event.domain.request.M2MAgreementEventRequest;
import it.pagopa.interop.event.domain.request.M2MEserviceEventRequest;
import it.pagopa.interop.event.domain.request.M2MEventRequest;
import it.pagopa.interop.event.domain.request.M2MPurposeEventRequest;
import it.pagopa.interop.event.filter.EventFilter;
import it.pagopa.interop.event.filter.EventPredicate;

import java.time.Instant;
import java.util.Optional;

public interface IM2MEventClient extends SettableBearerToken, SettableHttpCallExecutor {
    void setReferenceTime(Instant reference);

    M2MEServiceEvents getEServicesEvents(M2MEserviceEventRequest request);
    M2MEServiceEvents getAllEServicesEvents(M2MEserviceEventRequest request);

    M2MEServiceTemplateEvents getEServiceTemplateEvents(M2MEventRequest request);
    M2MEServiceTemplateEvents getAllEServiceTemplateEvents(M2MEventRequest request);

    M2MConsumerDelegationEvents getConsumerDelegationEvents(M2MEventRequest request);
    M2MConsumerDelegationEvents getAllConsumerDelegationEvents(M2MEventRequest request);

    M2MClientEvents getClientEvents(M2MEventRequest request);
    M2MClientEvents getAllClientEvents(M2MEventRequest request);

    M2MAttributeEvents getAttributesEvents(M2MEventRequest request);
    M2MAttributeEvents getAllAttributesEvents(M2MEventRequest request);

    M2MAgreementEvents getAgreementsEvents(M2MAgreementEventRequest request);
    M2MAgreementEvents getAllAgreementsEvents(M2MAgreementEventRequest request);

    M2MKeyEvents getKeyEvents(M2MEventRequest request);
    M2MKeyEvents getAllKeyEvents(M2MEventRequest request);

    M2MProducerDelegationEvents getProducerDelegationEvents(M2MEventRequest request);
    M2MProducerDelegationEvents getAllProducerDelegationEvents(M2MEventRequest request);

    M2MProducerKeyEvents getProducerKeyEvents(M2MEventRequest request);
    M2MProducerKeyEvents getAllProducerKeyEvents(M2MEventRequest request);

    M2MProducerKeychainEvents getProducerKeychainEvents(M2MEventRequest request);
    M2MProducerKeychainEvents getAllProducerKeychainEvents(M2MEventRequest request);

    M2MPurposeEvents getPurposeEvents(M2MPurposeEventRequest request);
    M2MPurposeEvents getAllPurposeEvents(M2MPurposeEventRequest request);

    M2MTenantEvents getTenantEvents(M2MEventRequest request);
    M2MTenantEvents getAllTenantEvents(M2MEventRequest request);

    M2MPurposeTemplateEvents getPurposeTemplateEvents(M2MEventRequest request);
    M2MPurposeTemplateEvents getAllPurposeTemplateEvents(M2MEventRequest request);

    Optional<M2MEvent> findEvent(M2MEventRequest request);
    M2MEvents findEvents(M2MEventRequest request, EventPredicate filter);

    M2MEvents getEvents(M2MEventRequest request);
}

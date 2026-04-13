package it.pagopa.interop.event.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.event.domain.M2MEventRequest;
import it.pagopa.interop.event.domain.M2MEvents;
import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateM2MEvents;

import java.util.List;
import java.util.UUID;

public interface IM2MEventClient extends SettableBearerToken {
    M2MEvents getEServicesEvents(M2MEventRequest request);

    M2MEvents getEServiceTemplateEvents(M2MEventRequest request);

    M2MEvents getConsumerDelegationEvents(M2MEventRequest request);

    M2MEvents getClientEvents(M2MEventRequest request);

    M2MEvents getAttributesEvents(M2MEventRequest request);

    M2MEvents getAgreementsEvents(M2MEventRequest request);

    M2MEvents getKeyEvents(M2MEventRequest request);

    M2MEvents getProducerDelegationEvents(M2MEventRequest request);

    M2MEvents getProducerKeyEvents(M2MEventRequest request);

    M2MEvents getProducerKeychainEvents(M2MEventRequest request);

    M2MEvents getPurposeEvents(M2MEventRequest request);

    M2MEvents getTenantEvents(M2MEventRequest request);

    <Event, EventRequest> List<Event> getEvents(EventRequest request);

    <Event, EventRequest> List<Event> getEventsAfter(EventRequest request, UUID lastEventId);
}

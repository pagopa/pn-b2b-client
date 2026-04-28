package it.pagopa.interop.event.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class M2MAgreementEventRequest extends M2MEventRequest{
    protected UUID delegationId;

    public static M2MAgreementEventRequest from(M2MEventRequest request){
        M2MAgreementEventRequest newRequest  = new M2MAgreementEventRequest();
        newRequest.setTenantType(request.getTenantType());
        newRequest.setLimit(request.getLimit());
        newRequest.setEvent(request.getEvent());
        newRequest.setFilter(request.getFilter());
        newRequest.setLastEventId(request.getLastEventId());
        return newRequest;
    }
}

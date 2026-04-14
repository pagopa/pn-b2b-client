package it.pagopa.interop.event.domain.request;

import it.pagopa.interop.event.enums.InteropEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class M2MEventRequest {
    public static final int EVENTS_MAX_LIMIT = 500;
    protected UUID lastEventId;
    protected InteropEvent.Family eventFamily;
    protected String tenantType;
    protected Integer limit;

    public static M2MEventRequest from(M2MEventRequest request){
        M2MEventRequest newRequest = new M2MEventRequest();
        newRequest.setTenantType(request.getTenantType());
        newRequest.setLimit(request.getLimit());
        return newRequest;
    }
}

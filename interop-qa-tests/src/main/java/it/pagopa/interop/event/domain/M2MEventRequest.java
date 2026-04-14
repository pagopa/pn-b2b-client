package it.pagopa.interop.event.domain;

import it.pagopa.interop.event.enums.InteropEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class M2MEventRequest {
    public static final int EVENTS_MAX_LIMIT = 500;

    private UUID lastEventId;
    private int limit = EVENTS_MAX_LIMIT;
    private UUID delegationId;
    private InteropEvent.Family eventFamily;
    private String tenantType;

    public static M2MEventRequest of(UUID lastEventId) {
        M2MEventRequest m2MEventRequest = new M2MEventRequest();
        m2MEventRequest.setLastEventId(lastEventId);
        return m2MEventRequest;
    }

    public static M2MEventRequest of(UUID lastEventId, UUID delegationId) {
        M2MEventRequest m2MEventRequest = M2MEventRequest.of(lastEventId);
        m2MEventRequest.setDelegationId(delegationId);
        return m2MEventRequest;
    }

    public static M2MEventRequest of(String tenantType, InteropEvent.Family eventFamily) {
        M2MEventRequest m2MEventRequest = new M2MEventRequest();
        m2MEventRequest.setEventFamily(eventFamily);
        m2MEventRequest.setTenantType(tenantType);
        return m2MEventRequest;
    }
}

package it.pagopa.interop.event.domain;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class M2MEventRequest {
    @Nullable       private UUID lastEventId;
    @Nonnull        private int limit = 500;
    @Nullable       private UUID delegationId;

    public static M2MEventRequest minimal() {
        return new M2MEventRequest();
    }

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
}

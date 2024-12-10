package it.pagopa.interop.purpose.domain;

import lombok.Data;

import java.util.UUID;

public abstract class TEServiceMode {
    private final UUID eserviceId;
    private final UUID consumerId;

    public TEServiceMode(UUID eserviceId, UUID consumerId) {
        this.eserviceId = eserviceId;
        this.consumerId = consumerId;
    }

    public UUID getEserviceId() {
        return eserviceId;
    }

    public UUID getConsumerId() {
        return consumerId;
    }
}

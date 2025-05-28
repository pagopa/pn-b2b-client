package it.pagopa.interop.purpose.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CreatedEserviceVersion {
    private UUID purposeId;
    private UUID currentVersionId;
    private UUID waitingForApprovalVersionId;
}

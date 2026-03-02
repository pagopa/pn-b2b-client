package it.pagopa.pn.interop.cucumber.steps;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentMetadata {
    private UUID id;
    private String name;
    private String prettyName;
    private OffsetDateTime createdAt;
}

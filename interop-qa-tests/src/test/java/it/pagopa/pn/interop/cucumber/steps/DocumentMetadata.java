package it.pagopa.pn.interop.cucumber.steps;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentMetadata {
    private UUID id;
    private String name;
    private String prettyName;
    private String uploadPath;
    private OffsetDateTime createdAt;
}

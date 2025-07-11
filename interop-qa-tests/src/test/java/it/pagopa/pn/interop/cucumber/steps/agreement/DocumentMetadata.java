package it.pagopa.pn.interop.cucumber.steps.agreement;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentMetadata {
    private String name;
    private String prettyName;
    private LocalDateTime createdAt;
}

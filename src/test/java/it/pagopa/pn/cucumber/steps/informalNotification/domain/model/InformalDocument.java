package it.pagopa.pn.cucumber.steps.informalNotification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformalDocument {
    private String key;
    private String contentType;
    private String sha256;
    private String versionToken;
}

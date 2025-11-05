package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EServiceTemplateAttributesKey {
    protected UUID templateId;
    protected UUID versionId;
}

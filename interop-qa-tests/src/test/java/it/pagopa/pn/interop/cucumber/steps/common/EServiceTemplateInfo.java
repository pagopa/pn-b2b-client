package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Data
@With
@AllArgsConstructor
public class EServiceTemplateInfo {
    private String name;
    private String intendedTarget;
    private String eServiceDescription;
    private EServiceTechnology technology;
    private EServiceMode mode;
    private UUID id;
    private java.util.UUID lastVersionId;
    private Boolean personalData;
    private Boolean async;
}

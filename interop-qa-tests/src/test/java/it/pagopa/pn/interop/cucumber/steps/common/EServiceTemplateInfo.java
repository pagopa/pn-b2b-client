package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class EServiceTemplateInfo {
    private String name;
    private String intendedTarget;
    private String eServiceDescription;
    private EServiceMode mode;
    private UUID id;
    private java.util.UUID lastVersionId;
    private Boolean personalData;
}
